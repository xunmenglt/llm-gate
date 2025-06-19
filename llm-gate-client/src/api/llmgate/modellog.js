import request from '@/utils/request'

//获取模型调用日志
export function getModelLog(queryParams) {
    return request({
      url: '/llmgate/llm-log/list',
      method: 'get',
      params: queryParams
    })
}

//获取模型调用日志(当前用户)
export function getModelSelfLog(queryParams) {
    return request({
        url: '/llmgate/llm-log/selflist',
        method: 'get',
        params: queryParams
    })
}