package com.xunmeng.llmgate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xunmeng.llmgate.pojo.ApiKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * api-key密钥表 Mapper 接口
 * </p>
 *
 * @author LiuTeng
 * @since 2025-06-01
 */
public interface ApiKeyMapper extends BaseMapper<ApiKey> {

    List<ApiKey> selectByConditions(@Param("name") String name,
                                    @Param("userId") String userId,
                                    @Param("status") Integer status);

    List<ApiKey> selectInSelfByConditions(@Param("name") String name,
                                          @Param("userId") String userId,
                                          @Param("status") Integer status);
}
