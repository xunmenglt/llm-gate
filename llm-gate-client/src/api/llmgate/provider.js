import request from "@/utils/request";

// 获取所有provider
export function getProviders(params) {
    return request({
      url: "/system/provider/list",
      method: "get",
      params:params,
    });
}

//新增provider
export function addProvider(data) {
    return request({
      url: "/system/provider/add",
      method: "post",
      data:data,
    });
}
// 修改provider
export function updateProvider(data) {
    return request({
        url: "/system/provider/update",
        method: "put",
        data:data,
    });
}
//删除
export function deleteProvider(id) {
    return request({
      url: "/system/provider/delete/"+id,
      method: "delete",
    });
}

//提供商详情
export function getProviderDetail(id) {
    return request({
      url: "/system/provider/detail/"+id,
      method: "get",
    });
}