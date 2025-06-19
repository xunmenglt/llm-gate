import request from '@/utils/request'

// 新增兑换码码
export function addVoucher(data) {
    return request({
        url: '/voucher/add',
        method: 'post',
        data: data
    })
}
// 禁用兑换码
export function disableVoucher(code) {
    return request({
        url: `/voucher/disable/${code}`,
        method: 'post'
    })
}
// 消费兑换码
export function consumeVoucherCode(code) {
    return request({
        url: `/voucher/consume/${code}`,
        method: 'post'
    })
}

// 兑换码列表
export function getVoucherCodeList(queryParams) {
    return request({
        url: '/voucher/list',
        method: 'get',
        params: queryParams
    })
}

