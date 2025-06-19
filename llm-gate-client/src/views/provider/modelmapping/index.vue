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
          <el-form-item label="模型名称" prop="modelName">
            <el-input
                v-model="queryParams.modelName"
                placeholder="请输入提供商名称"
                clearable
                style="width: 240px"
                @keyup.enter.native="handleQuery"
            />
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
            >新增模型映射</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                plain
                icon="el-icon-delete"
                size="mini"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['llmgate:apikey:delete']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>
        <el-table v-loading="loading" :data="modelList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="模型名称" align="center" key="modelName" prop="modelName" v-if="columns[0].visible" :show-overflow-tooltip="true" />
          <el-table-column label="提供商名称" align="center" key="providerName" prop="providerName" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="别名" align="center" key="modelNameAlias" prop="modelNameAlias" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="并发上限" align="center" key="maxConcurrency" prop="maxConcurrency" v-if="columns[3].visible"  />
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
              label="操作"
              align="center"
              width="100"
              class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
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

    <!-- 添加配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="提供商名称" prop="providerName">
              <el-select v-model="form.providerName" placeholder="请选择模型提供商">
                <el-option
                    v-for="item in providerOptions"
                    :key="item.providerId"
                    :label="item.providerName"
                    :value="item.providerName"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" placeholder="请输入模型名称" maxlength="50" />
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
            <el-form-item label="模型别名" prop="modelNameAlias">
              <el-input v-model="form.modelNameAlias" placeholder="请输入模型别名"  />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>



  </div>
</template>

<script>
import { modelMapList,addModelMap,delModelMap} from "@/api/llmgate/modelmap";
import {getProviders} from "@/api/llmgate/provider";
import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';


export default {
  name: "Provider",
  components: { RightToolbar,Pagination },
  data() {
    return {
      //提供商列表
      providerOptions: [],
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
      modelList: null,
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
        modelName: undefined,
        providerName: undefined,
      },
      // 列信息
      columns: [
        { key: 1, label: `模型名称`, visible: true },
        { key: 2, label: `提供商名称`, visible: true },
        { key: 3, label: `别名`, visible: true },
        { key: 4, label: `并发上限`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      // 表单校验
      rules: {
        //pass
      },
      // 字典
      dict:SYS_DICT
    };
  },
  created() {
    this.getList();
    this.loadProviders();
  },
  methods: {
    loadProviders() {
      getProviders().then(response => {
        this.providerOptions = response.rows || [];
      });
    },
    getList() {
      this.loading = true;
      modelMapList(this.queryParams).then(response => {
            this.modelList = response.rows;
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
        providerName: undefined,
        modelName: undefined,
        maxConcurrency: 1,
        providerId: undefined,
        modelNameAlias: undefined
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.apikeyNames = selection.map(item => item.name);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.isUpdate = false;
      this.open = true;
      this.title = "添加模型映射";
    },

    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          getProviders({
            pageNum: 1,
            pageSize: 1,
            providerName: this.form.providerName
          }).then(response => {
            if (response.rows && response.rows.length > 0) {
              this.form.providerId = response.rows[0].providerId;
              return addModelMap(this.form);
            } else {
              return Promise.reject(new Error("未找到匹配的提供商"));
            }
          }).then(() => {
            showTextMessage(TOAST_TYPE.success, "新增成功", TOAST_POSITION.top);
            this.open = false;
            this.getList();
          }).catch(error => {
            console.error(error);
            showTextMessage(TOAST_TYPE.error, "新增失败：" + error.message, TOAST_POSITION.top);
          });


        }

      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const Ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除？').then(function () {
        return delModelMap(Ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
  }
};
</script>