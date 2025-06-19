import request from "@/utils/request";

//获取倍率列表
export function getMultiplierList(query) {
    return request({
      url: "multiplier/list",
      method: "get",
      params: query,
    });
}

//删除
export function delMultiplier(ids) {
    return request({
      url: `multiplier/delete/${ids}`,
      method: "delete",
    });
}

//新增
export function addMultiplier(data) {
    return request({
        url: "/multiplier/add",
        method: "post",
        data: data,
    });
}