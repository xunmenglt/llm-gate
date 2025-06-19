<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="令牌名称" prop="name">
            <el-input
                v-model="queryParams.name"
                placeholder="请输入apikey名称"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="创建者" prop="userName">
            <el-input
                v-model="queryParams.userName"
                placeholder="请输入创建者名称"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
                v-model="queryParams.status"
                placeholder="apikey状态"
                clearable
                style="width: 200px"
            >
              <el-option
                  v-for="dict in dict.sys_apikey_status"
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
                v-hasPermi="['llmgate:apikey:add']"
            >创建新的APIKEY</el-button>
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
        <el-table v-loading="loading" :data="apikeyList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="名称" align="center" key="name" prop="name" v-if="columns[0].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="创建者" align="center" key="userName" prop="userName" v-if="columns[1].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="状态" align="center" key="status" prop="status" v-if="columns[2].visible">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
              label="额度限制"
              align="center"
              key="quota"
              prop="quota"
              v-if="columns[3].visible"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.unlimited === 1">无限制</span>
              <span v-else>{{ scope.row.quota }}</span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="过期时间" align="center" prop="expiresTime" v-if="columns[5].visible" width="160">
            <template slot-scope="scope">
              <span>{{ scope.row.expiresTime === null ? '永不过期' : parseTime(scope.row.expiresTime) }}</span>
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
                  icon="el-icon-copy-document"
                  @click="handleCopy(scope.row)"
              >复制</el-button>
              <el-button
                  size="mini"
                  type="text"
                  :icon="scope.row.status === 1 ? 'el-icon-close' : 'el-icon-check'"
                  @click="handleStatusChange(scope.row)"
                  v-hasPermi="['llmgate:apikey:edit']"
              >{{ scope.row.status === 1? '禁用' : '启用' }}</el-button>
              <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleEdit(scope.row)"
                  v-hasPermi="['llmgate:apikey:edit']"
              >编辑</el-button>
              <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="handleDetail(scope.row)"
              >使用详情</el-button>
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
    <!-- 使用详情对话框 -->
    <el-dialog title="APIKey 使用详情" :visible.sync="detailOpen" width="500px" append-to-body>
      <div class="detail-card-list">
        <el-card
            v-for="item in detailDisplayList"
            :key="item.label"
            shadow="never"
            class="detail-card"
        >
          <el-row>
            <el-col :span="8" class="detail-label">{{ item.label }}</el-col>
            <el-col :span="16" class="detail-value">{{ item.value }}</el-col>
          </el-row>
        </el-card>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>






    <!-- 添加或修改令牌对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="apikey名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入apikey名称" />
        </el-form-item>
        <el-form-item label="额度限制" prop="unlimited">
          <el-radio-group v-model="form.unlimitedType">
            <el-radio label= "1" >无限制</el-radio>
            <el-radio label= "0">自定义</el-radio>
          </el-radio-group>
          <el-input-number
              v-if="form.unlimitedType === '0'"
              v-model="form.quota"
              :min="0.01"
              :max="1000"
              placeholder="请输入额度(0~1000)"
              style="width: 100%; margin-top: 10px;"
          />
        </el-form-item>
        <el-form-item label="过期时间" prop="expiresTime">
          <el-radio-group v-model="form.expiresTimeType">
            <el-radio label="1" >永不过期</el-radio>
            <el-radio label="0">自定义</el-radio>
          </el-radio-group>
          <el-date-picker
              v-if="form.expiresTimeType === '0'"
              v-model="form.expiresTime"
              type="datetime"
              placeholder="选择过期时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 100%; margin-top: 10px;"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入apikey备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getApiKeyList, addApiKey, updateApiKey, deleteApiKey, getApiKeyDetail,getApiKeySelfList  } from "@/api/llmgate/apikey";
import { SYS_DICT, TOAST_POSITION, TOAST_TYPE } from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';
export default {
  name: "ApiKey",
  components: { RightToolbar, Pagination },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: undefined,
      apikeyNames:undefined,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 令牌表格数据
      apikeyList: null,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 表单参数
      form: {
        unlimitedType : '1',
        expiresTimeType : '1',
        id:undefined,
        createTime: undefined,
        expiresTime: new Date(),
        name: undefined,
        quota: undefined,
        remark: undefined,
        status: 1,
        unlimited: 1,
        updateTime: undefined,
      },
      detailOpen: false,

      detailForm: {
        totalInputTokens: null,
        totalOutputTokens: null,
        totalCalls: 0,
        errorCalls: null,
        totalQuotaUsed: null,
        remark: ''
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        userId:undefined,
        status: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `名称`, visible: true },
        { key: 1, label: `创建者`, visible: true },
        { key: 2, label: `状态`, visible: true },
        { key: 3, label: `额度限制`, visible: true },
        { key: 4, label: `创建时间`, visible: true },
        { key: 5, label: `过期时间`, visible: true }
      ],
      // 表单校验
      rules: {
        name: [
          { required: true, message: "令牌名称不能为空", trigger: "blur" }
        ],
        quotaLimit: [
          { required: true, message: "额度不能为空", trigger: "blur" },
          { type: 'number', min: 0.01, message: '额度必须大于0', trigger: 'blur' }
        ],
        expireTime: [
          { required: true, message: "请选择过期时间", trigger: "blur" }
        ]
      },
      // 字典
      dict: SYS_DICT
    };
  },
  created() {
    this.getList();
  },
  computed: {
    detailDisplayList() {
      return [
        { label: '总调用次数', value: this.detailForm.totalCalls ?? '无数据' },
        { label: '总输入 Tokens', value: this.detailForm.totalInputTokens ?? '无数据' },
        { label: '总输出 Tokens', value: this.detailForm.totalOutputTokens ?? '无数据' },
        { label: '错误调用次数', value: this.detailForm.errorCalls ?? '无数据' },
        { label: '已用额度', value: this.detailForm.totalQuotaUsed ?? '无数据' },
        { label: '备注', value: this.detailForm.remark || '无' }
      ];
    }
  },

  methods: {
    /** 查询令牌列表 */
    getList() {
      this.loading = true;
      console.log(11)
      getApiKeyList(this.queryParams).then(response => {
        this.apikeyList = response.rows;
        console.log(this.apikeyList);
        this.total = response.total;
        this.loading = false;
      }).catch((e) => {console.log(e)});
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        unlimitedType : '1',
        expiresTimeType : '1',
        id:undefined,
        createTime: undefined,
        expiresTime: new Date(),
        name: undefined,
        quota: undefined,
        remark: undefined,
        status: 1,
        unlimited: 1,
        updateTime: undefined,
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
      this.open = true;
      this.title = "添加APIKEY";
    },
    handleDetail(row) {
      const apikey = row.key;
      getApiKeyDetail(apikey).then(response => {
        console.log("响应详情", response.data);

        const data= response.data;
        this.detailForm = {
          totalInputTokens: data.totalInputTokens,
          totalOutputTokens: data.totalOutputTokens,
          totalCalls: data.totalCalls,
          errorCalls: data.errorCalls,
          totalQuotaUsed: data.totalQuotaUsed,
          remark: data.apiKeyInfo.remark
        };
        this.detailOpen = true;
      });
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.reset();
      const apikey = row.key;
      getApiKeyDetail(apikey).then(response => {
        this.form = response.data.apiKeyInfo;
        this.form.unlimitedType = this.form.unlimited === 1 ? '1' : '0';
        this.form.expiresTimeType = this.form.expiresTime === null ? '1' : '0';
        this.open = true;
        this.title = "修改APIKEY";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 处理无限制额度的情况
          if (this.form.unlimitedType === '1') {
            this.form.quota = null;
          }else{
            this.form.unlimited = 0;
          }
          // 处理永不过期的情况
          if (this.form.expiresTimeType === '1') {
            this.form.expiresTime = null;
          }else{
            // 在提交数据的逻辑中（如 axios 请求前）
            this.form.expiresTime = this.form.expiresTime.replace(" ", "T");
          }

          if (this.form.id !== undefined) {
            updateApiKey(this.form).then(response => {
              showTextMessage(TOAST_TYPE.success, "修改成功", TOAST_POSITION.top);
              this.open = false;
              this.getList();
            });
          } else {
            addApiKey(this.form).then(response => {
              showTextMessage(TOAST_TYPE.success, "新增成功", TOAST_POSITION.top);
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const Ids = row.id || this.ids;
      // 根据apikeyNames数组，如果数组元素大于2则取前2个拼接字符串
      const delnames = this.apikeyNames.length > 2 ? this.apikeyNames.slice(0, 2).join(",") + "..." : this.apikeyNames.join(",");
      this.$modal.confirm('是否确认删除名称为"' + delnames + '"的apikey？').then(function() {
        return deleteApiKey(Ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 复制令牌操作 */
    handleCopy(row) {
      navigator.clipboard.writeText(row.key)
          .then(() => {
            showTextMessage(TOAST_TYPE.success, "复制成功", TOAST_POSITION.top);
          })
          .catch(() => {
            showTextMessage(TOAST_TYPE.error, "复制失败", TOAST_POSITION.top);
          });
    },
    /** 令牌状态修改 */
    handleStatusChange(row) {
      let text = row.status === 1 ? "禁用" : "启用";
      this.$confirm('确认要"' + text + '""' + row.name + '"令牌吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        let data = {
          ...row,
          status: row.status === 1? 0:1
        }
        return updateApiKey(data);
      }).then(() => {
        showTextMessage(TOAST_TYPE.success, text + "成功", TOAST_POSITION.top);
        this.getList();
      }).catch(() => {});
    },

  }
};
</script>

<style scoped>
.el-button + .el-button {
  margin-left: 5px;
}
.detail-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-card {
  padding: 10px;
  background-color: #fafafa;
  border-radius: 6px;
}

.detail-label {
  font-weight: 600;
  color: #303133;
}

.detail-value {
  color: #606266;
}

</style>

