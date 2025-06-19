export const TOAST_POSITION={
    "top_right":"top-right",
    "top":"top-center",
    "top_left":"top-left",
    "bottom_right":"bottom-right",
    "bottom_center":"bottom-center",
    "bottom_left":"bottom-left"
}

export const TOAST_TYPE={
    "defalut":"defalut",
    "success":"success",
    "info":"info",
    "warning":"warning",
    "error":"error",
    "danger":"danger"
}

export const SYS_DICT={
    "sys_provider_type":[
        { value: 'openai', label: 'openai', listClass: 'primary' },
        { value: 'claude', label: 'claude', listClass: 'success' },
    ],
    "sys_account_type":[
        { value: 'MODEL_CALL', label: '模型调用', listClass: 'primary' },
        { value: 'VOUCHER_CODE_USE', label: '兑换码使用', listClass: 'success' },
        { value: 'SYSTEM_ADJUST', label: '系统调整', listClass: 'warning' },
        { value: 'ADMIN_RECHARGE', label: '管理员充值', listClass: 'danger' }
    ],
   "sys_apikey_status":[
        {value:0,label:"禁用",listClass:"danger"},
        {value:1,label:"启用",listClass:"success"},
    ],
    "sys_voucher_status":[
        {value:0,label:"禁用",listClass:"danger"},
        {value:1,label:"已使用",listClass:"success"},
        {value:-1,label:"未使用",listClass:"primary"},
    ],
    "sys_normal_disable":[
        {value:0,label:"停用",listClass:"danger"},
        {value:1,label:"正常",listClass:"primary"}
    ],
    "sys_show_hide":[
        {value:0,label:"隐藏",listClass:"danger"},
        {value:1,label:"显示",listClass:"primary"}
    ],
    "notice_type":[
        {value:1,label:"通知",listClass:"warning"},
        {value:2,label:"公告",listClass:"success"}
    ],
    "sys_notice_status":[
        {value:1,label:"正常",listClass:"primary"},
        {value:0,label:"关闭",listClass:"danger"}
    ],
    "sys_oper_type":[
        {value:0,label:"其它",listClass:"primary"},
        {value:1,label:"新增",listClass:"info"},
        {value:2,label:"修改",listClass:"info"},
        {value:3,label:"删除",listClass:"danger"},
        {value:4,label:"授权",listClass:"primary"},
        {value:5,label:"导出",listClass:"warning"},
        {value:6,label:"导入",listClass:"warning"},
        {value:7,label:"强退",listClass:"danger"},
        {value:8,label:"生成",listClass:"warning"},
        {value:9,label:"情空",listClass:"danger"},
    ],
    "sys_common_status":[
        {value:1,label:"成功",listClass:"success"},
        {value:0,label:"失败",listClass:"danger"}
    ],
    "articel_audit_status":[
        {value:0,label:"待审核",listClass:"warning"},
        {value:1,label:"审核成功",listClass:"success"},
        {value:2,label:"审核失败",listClass:"danger"},
    ],
    "comment_audit_status":[
        {value:0,label:"不通过",listClass:"danger"},
        {value:1,label:"通过",listClass:"success"},
    ],
    "search_status_in_lucene":[
        {value:0,label:"未检索",listClass:"danger"},
        {value:1,label:"已检索",listClass:"success"},
    ],
    "embedding_status_in_db":[
        {value:0,label:"未向量化",listClass:"danger"},
        {value:1,label:"已向量化",listClass:"success"},
    ],
    "article_recovery_status":[
        {value:0,label:"未回收",listClass:"success"},
        {value:1,label:"已回收",listClass:"danger"},
    ]
    
}