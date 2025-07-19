<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
          <el-form-item label="提供商名称" prop="providerName">
            <el-input
                v-model="queryParams.providerName"
                placeholder="请输入提供商名称"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="接口类型" prop="type">
            <el-select
                v-model="queryParams.enabled"
                placeholder="类型"
                clearable
                style="width: 240px"
            >
              <el-option
                  v-for="dict in dict.sys_provider_type"
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
        </el-form>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="mini"
                @click="handleAdd"
                v-hasPermi="['system:user:add']"
            >新增提供商</el-button>
          </el-col>

          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>
        <el-table v-loading="loading" :data="providerList">
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="提供商名称" align="center" key="providerName" prop="providerName" v-if="columns[0].visible" :show-overflow-tooltip="true" />
          <el-table-column label="接口类型" align="center" key="type" prop="type" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="并发上限" align="center" key="maxConcurrency" prop="maxConcurrency" v-if="columns[2].visible"  />
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[3].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
              label="操作"
              align="center"
              width="300"
              class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="handleModelDetail(scope.row)"
              >详情</el-button>
              <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['llmgate:provider:edit']"
              >配置</el-button>
              <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['llmgate:provider:delete']"
              >删除</el-button>
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

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提供商名称" prop="providerName">
              <el-input v-model="form.providerName" placeholder="请输入提供商名称" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option
                    v-for="item in dict.sys_provider_type"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="并发上限" prop="maxConcurrency">
              <el-input-number v-model="form.maxConcurrency" :min="1" :max="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="API Key" prop="apiKey">
              <el-input v-model="form.apiKey" placeholder="请输入 API Key" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="代理地址" prop="proxyUrl">
              <el-input v-model="form.proxyUrl" placeholder="请输入代理地址" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 提供商信息 -->
    <el-dialog
        title="提供商模型详情"
        :visible.sync="modelDetailOpen"
        width="600px"
        append-to-body
    >
      <el-table :data="modelDetailList" border style="width: 100%">
        <el-table-column label="模型名称" prop="modelName" />
        <el-table-column label="别名">
          <template slot-scope="scope">
            <el-tag
                v-for="(alias, index) in scope.row.alias"
                :key="index"
                type="info"
                size="mini"
                style="margin-right: 4px"
            >
              {{ alias }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="并发上限" prop="maxConcurrency" align="center" />
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="modelDetailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getProviders, getProviderDetail, deleteProvider, addProvider, updateProvider } from "@/api/llmgate/provider";

import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';

export default {
  name: "Provider",
  components: { RightToolbar,Pagination },
  data() {
    return {
      modelDetailOpen: false,
      modelDetailList: [],
      // 是否更新
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
      providerList: null,
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: 'admin123',
      // 日期范围
      dateRange: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],

      defaultProps: {
        children: "children",
        label: "label"
      },


      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderByColumn:'create_time',
        type: undefined,
        providerName: undefined,
      },
      // 列信息
      columns: [
        { key: 1, label: `提供商名称`, visible: true },
        { key: 2, label: `类型`, visible: true },
        { key: 4, label: `并发上限`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      form: {
        providerName: '',
        type: '',
        maxConcurrency: 1,
        apiKey: '',
        proxyUrl: ''
      },
      rules: {
        providerName: [
          { required: true, message: '请输入提供商名称', trigger: 'blur' },
          { max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择接口类型', trigger: 'change' }
        ],
        maxConcurrency: [
          { type: 'number', required: true, message: '请输入并发上限', trigger: 'change' },
          { type: 'number', min: 1, max: 100, message: '并发上限需在 1 到 100 之间', trigger: 'change' }
        ],
        apiKey: [
          { required: true, message: '请输入 API Key', trigger: 'blur' }
        ],
        proxyUrl: [
          { required: false },
          { type: 'url', message: '请输入正确的 URL 地址', trigger: 'blur' }
        ]
      },
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
      getProviders(this.queryParams).then(response => {
            this.providerList = response.rows;
            this.total = response.total;
            this.loading = false;
          }
      );
    },
    handleModelDetail(row) {
      this.modelDetailOpen = true;
      getProviderDetail(row.providerId).then(response => {
        this.modelDetailList = response.data.models || [];
      })
    },


    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        providerName: undefined,
        apiKey: undefined,
        maxConcurrency: 1,
        proxyUrl: undefined,
        type:undefined
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


    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.isUpdate=false;
      this.open = true;
      this.title = "添加提供商";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.isUpdate=true
      this.reset();
      getProviderDetail(row.providerId).then(response => {
        this.form = response.data.provider;
        this.open = true;
        this.title = "编辑提供商配置";
      })
    },

    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.isUpdate) {
            updateProvider(this.form).then(response => {
              showTextMessage(TOAST_TYPE.success,'修改成功',TOAST_POSITION.top)
              this.open = false;
              this.getList();
            }).catch(e=>{
              console.log(e)
            });
          } else {
            addProvider(this.form).then(response => {
              showTextMessage(TOAST_TYPE.success,'新增成功',TOAST_POSITION.top)
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const Id = row.id;
      this.$modal.confirm('是否确认删除？').then(function() {
        return deleteProvider(Id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>