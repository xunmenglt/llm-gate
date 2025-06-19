import request from '@/utils/request'

// 获取模型映射列表
export function modelMapList(queryParams) {
    return request({
        url: '/llmgate/model-mapping/list',
        method: 'get',
        params: queryParams
    })
}

// 新增模型映射
export function addModelMap(data) {
    return request({
        url: '/llmgate/model-mapping/add',
        method: 'post',
        data: data
    })
}

//删除模型映射
export function delModelMap(ids) {
    return request({
        url: '/llmgate/model-mapping/delete/' + ids,
        method: 'delete'
    })
}
