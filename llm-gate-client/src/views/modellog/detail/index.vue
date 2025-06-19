<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">

        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="75px">
          <el-form-item label="用户名称" prop="userName">
            <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名称"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="提供商" prop="providerName" >
            <el-input
                v-model="queryParams.providerName"
                placeholder="请输入提供商名称"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="模型名称" prop="modelName">
            <el-input
                v-model="queryParams.modelName"
                placeholder="请输入模型名称"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="modelLogList" >
          <el-table-column label="时间" align="center" prop="createTime" v-if="columns[0].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="提供商名称" align="center" key="providerName" prop="providerName" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="模型名称" align="center" key="modelName" prop="modelName" v-if="columns[3].visible" :show-overflow-tooltip="true" />
          <el-table-column label="apikey" align="center" key="apiKey" prop="apiKey" v-if="columns[4].visible" :show-overflow-tooltip="true" />
          <el-table-column label="输入消耗" align="center" key="inputTokens" prop="inputTokens" v-if="columns[5].visible" :show-overflow-tooltip="true" />
          <el-table-column label="回复消耗" align="center" key="ouputTokens" prop="ouputTokens" v-if="columns[6].visible" :show-overflow-tooltip="true" />
          <el-table-column
            label="消耗额度"
            align="center"
            key="quota"
            prop="quota"
            v-if="columns[7].visible">
          <template slot-scope="scope">
            {{ scope.row.quota.toFixed(5)}}
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
import {getModelLog,getModelSelfLog} from "@/api/llmgate/modellog";
import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import {changeUserStatus} from "@/api/system/user";

export default {
  name: "ModelLog",
  components: { RightToolbar,Pagination },
  data() {
    return {

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
      modelLogList: null,
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
        providerName:undefined,
        modelName:undefined,
        userName: undefined
      },
      // 列信息
      columns: [
        { key: 1, label: `时间`, visible: true },
        { key: 8, label: `用户名称`, visible: true},
        { key: 2, label: `提供商名称`, visible: true },
        { key: 3, label: `模型名称`, visible: true },
        { key: 4, label: `apikey`, visible: true },
        { key: 5, label: `输入消耗`, visible: true },
        { key: 6, label: `回复消耗`, visible: true },
        { key: 7, label: `消耗额度`, visible: true }
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

      getModelLog(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
            this.modelLogList = response.rows;
            this.total = response.total;
            this.loading = false;
          }
      );
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },
    // 用户状态修改
    handleStatusChange(row) {
      let text = row.enabled === 1 ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userName, row.enabled);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.enabled = row.enabled === "0" ? "1" : "0";
      });
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

    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case "handleResetPwd":
          this.handleResetPwd(row);
          break;
        case "handleAuthRole":
          this.handleAuthRole(row);
          break;
        default:
          break;
      }
    },

  }
};
</script>