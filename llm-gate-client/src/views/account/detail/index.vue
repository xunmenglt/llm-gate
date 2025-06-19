<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card" shadow="hover" style="margin-bottom: 20px;">
          <div>当前账户余额：<strong style="color: #409EFF;">{{ currentBalance.toFixed(2) }}</strong> 元</div>
        </el-card>
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="用户名称" prop="userName">
            <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名称"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-select
                v-model="queryParams.type"
                placeholder="用户状态"
                clearable
                style="width: 240px"
            >
              <el-option
                  v-for="dict in dict.sys_account_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
              />
            </el-select>
          </el-form-item>


          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>

          </el-form-item>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-form>

        <el-table v-loading="loading" :data="accountList" >
          <el-table-column label="日志Id" align="center" key="logId" prop="logId" v-if="columns[0].visible" :show-overflow-tooltip="true" />
          <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="类型" align="center" key="type" prop="type" v-if="columns[2].visible"  >
            <template slot-scope="scope">
              {{ typeMap[scope.row.type]|| scope.row.type }}
            </template>
          </el-table-column>
          <el-table-column
              label="余额变化"
              align="center"
              key="deltaAmount"
              prop="deltaAmount"
              v-if="columns[3].visible">
            <template slot-scope="scope">
              {{ scope.row.deltaAmount.toFixed(5)}}
            </template>
          </el-table-column>
          <el-table-column label="时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>

        </el-table>

        <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
        />
      </el-col>
    </el-row>

  </div>
</template>

<script>
import {getBalance,getAccountLog} from "@/api/llmgate/balance";
import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';

export default {
  name: "Account",
  components: { RightToolbar,Pagination },
  data() {
    return {
      typeMap: {
        MODEL_CALL: '模型调用',
        VOUCHER_CODE_USE: '兑换码使用',
        SYSTEM_ADJUST: '系统调整',
        ADMIN_RECHARGE: '管理员充值'
      },
      //当前余额
      currentBalance:0.0,
      // 更新
      isUpdate:false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      accountList: null,
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 日期范围
      dateRange: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderByColumn:'create_time',
        userName: undefined,
        type: undefined,
        logId: undefined
      },
      // 列信息
      columns: [
        { key: 1, label: `日志Id`, visible: true },
        { key: 2, label: `用户名称`, visible: true },
        { key: 4, label: `类型`, visible: true },
        { key: 5, label: `余额变化`, visible: true },
        { key: 6, label: `时间`, visible: true }
      ],
      // 字典
      dict:SYS_DICT
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      getBalance().then(response => {
        this.currentBalance = response.data;
      });
      getAccountLog(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
            this.accountList = response.rows;
            this.total = response.total;
            this.loading = false;
          }
      );
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        userName: undefined,
        nickName: undefined,
        password: undefined,
        email: undefined,
        status: 0,
        remark: undefined,
        roleIds: []
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },


  }
};
</script>