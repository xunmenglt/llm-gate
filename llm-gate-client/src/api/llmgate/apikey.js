import request from '@/utils/request'

// 新增 API Key
export function addApiKey(data) {
    return request({
        url: '/apikey/add',
        method: 'post',
        data: data
    })
}

// 删除 API Key
export function deleteApiKey(ids) {
    return request({
        url: `/apikey/delete/${ids}`,
        method: 'delete'
    })
}

// 更新 API Key
export function updateApiKey(data) {
    return request({
        url: '/apikey/update',
        method: 'put',
        data: data
    })
}

// API Key 列表（所有）
export function getApiKeyList(queryParams) {
    return request({
        url: '/apikey/list',
        method: 'get',
        params: queryParams
    })
}
// API Key 列表（单个用户）
export function getApiKeySelfList(queryParams) {
    return request({
        url: '/apikey/selflist',
        method: 'get',
        params: queryParams
    })
}
// API Key 详情
export function getApiKeyDetail(key) {
    return request({
        url: `/apikey/usage-summary/${key}`,
        method: 'get'
    })
}