import request from '@/utils/request'

//查询当前用户余额
export function getBalance() {
    return request({
        url: '/account/balance',
        method: 'get'
    })
}

//账户资金日志
export function getAccountLog(queryParams) {
    return request({
        url: '/balanceLog/list',
        method: 'get',
        params: queryParams
    })
}