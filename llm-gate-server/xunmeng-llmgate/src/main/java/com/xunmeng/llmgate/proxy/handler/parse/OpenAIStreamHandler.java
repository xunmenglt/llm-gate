package com.xunmeng.llmgate.proxy.handler.parse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmeng.llmgate.model.response.OpenAIStreamResponse;
import com.xunmeng.llmgate.proxy.error.ModelProviderServerException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

@Slf4j
public class OpenAIStreamHandler extends StreamHandler {

    private final PipedOutputStream pipeOut;
    private final PipedInputStream pipeIn;
    private final BufferedReader reader;

    private static final String MESSAGE_SPLIT = "\n\n";

    private static final String DATA_PREFIX="data: ";

    private boolean isGzip;

    private ExecutorService executorService;

    private ByteBuf fullBuf = Unpooled.buffer();

    private static final ObjectMapper mapper = new ObjectMapper();

    private ChannelHandlerContext ctx;


    public OpenAIStreamHandler() throws IOException {
        this.pipeOut = new PipedOutputStream();
        this.pipeIn = new PipedInputStream(pipeOut, 8192); // 可调缓冲区
        this.reader = new BufferedReader(new InputStreamReader(pipeIn, StandardCharsets.UTF_8));
        this.isGzip=false;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 使用带异常处理的线程池
        this.executorService = Executors.newFixedThreadPool(1, r -> {
            Thread t = new Thread(r, "openai-stream-handler");
            t.setUncaughtExceptionHandler((thread, e) -> {
                log.error("OpenAI 流处理线程异常", e);
            });
            return t;
        });

        run();
    }

    private void run(){
        Future<?> future = executorService.submit(() -> {
            StringBuilder buffer = new StringBuilder();
            try {
                char[] charBuf = new char[1024];
                int len;
                while ((len = reader.read(charBuf)) != -1) {
                    buffer.append(charBuf, 0, len);
                    int pos;
                    while ((pos = buffer.indexOf(MESSAGE_SPLIT)) != -1) {
                        String line = buffer.substring(0, pos);
                        buffer.delete(0, pos + MESSAGE_SPLIT.length());
                        caculateUsage(line.trim());
                    }
                }
                if (buffer.length() > 0) {
                    caculateUsage(buffer.toString().trim());
                }
            } catch (Exception e) {
                if (ctx!=null){
                    ctx.fireExceptionCaught(new ModelProviderServerException("模型提供商非标准openai响应"));
                }
            }
        });

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.ctx=ctx;
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.info("响应状态: {}", response.status());
            response.headers().forEach(h -> log.info("响应头: {}: {}", h.getKey(), h.getValue()));
            String contentEncoding = response != null
                    ? response.headers().get(HttpHeaderNames.CONTENT_ENCODING, "identity")
                    : "identity";
            if ("gzip".equalsIgnoreCase(contentEncoding)) {
                this.isGzip=true;
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            if (isGzip) {
                fullBuf.writeBytes(buf);
            } else {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                pipeOut.write(bytes);
            }
            pipeOut.flush();
        }
        if (msg instanceof LastHttpContent){
            if (isGzip){
                byte[] bytes = new byte[fullBuf.readableBytes()];
                fullBuf.readBytes(bytes);
                pipeOut.write(decompressGzip(bytes));
            }
        }
    }

    private byte[] decompressGzip(byte[] compressed) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ObjectUtils.isNotEmpty(pipeOut)){
            pipeOut.close();
        }
        if (ObjectUtils.isNotEmpty(pipeIn)){
            pipeIn.close();
        }
        if (ObjectUtils.isNotEmpty(reader)){
            reader.close();
        }
        super.channelInactive(ctx);
    }

    @Override
    void caculateUsage(String line) {
        String jsonDataStr=parseLineText(line);
        if (StringUtils.isEmpty(jsonDataStr)){
            return;
        }

        try {
            OpenAIStreamResponse response = mapper.readValue(jsonDataStr, OpenAIStreamResponse.class);
            OpenAIStreamResponse.Usage usage = response.getUsage();
            if (ObjectUtils.isNotEmpty(usage)){
                int promptTokens = usage.getPromptTokens();
                int completionTokens = usage.getCompletionTokens();
                inputTokens+=promptTokens;
                outputTokens+=completionTokens;
            }
        } catch (Exception e) {
            log.error("模型提供商响应内容解析异常");
            throw new ModelProviderServerException("模型提供商非标准openai响应");
        }
    }

    @Override
    String parseLineText(String line) {
        if (StringUtils.isEmpty(line)) return null;
        if (!line.startsWith(DATA_PREFIX)) return null;
        String jsonDataStr = line.substring(DATA_PREFIX.length()).trim();
        if (jsonDataStr.equals("[DONE]")) return null;
        return jsonDataStr;
    }

}