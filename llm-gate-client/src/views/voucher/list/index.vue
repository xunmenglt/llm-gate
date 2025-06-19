<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="创建者" prop="creator">
            <el-input
                v-model="queryParams.creator"
                placeholder="请输入创建者"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="消费者" prop="consumer">
            <el-input
                v-model="queryParams.consumer"
                placeholder="请输入消费者"
                clearable
                style="width: 200px"
                @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
                v-model="queryParams.status"
                placeholder="兑换码状态"
                clearable
                style="width: 200px"
            >
              <el-option
                  v-for="dict in dict.sys_voucher_status"
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
                v-hasPermi="['llmgate:voucher:add']"
            >新增</el-button>
          </el-col>

          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>
        <el-table v-loading="loading" :data="voucherList" >
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="兑换码" align="center" key="voucherCode" prop="voucherCode" v-if="columns[0].visible" :show-overflow-tooltip="true"  width="280"/>
          <el-table-column label="额度" align="center" key="quota" prop="quota" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="创建者" align="center" key="creator" prop="creator" v-if="columns[2].visible"  />
          <el-table-column label="消费者" align="center" key="consumer" prop="consumer" v-if="columns[3].visible"  />
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="状态" align="center" key="status" prop="status" v-if="columns[5].visible">
            <template slot-scope="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getVoucherStatusLabel(scope.row.status) }}
              </el-tag>
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
                  type="danger"
                  icon="el-icon-close"
                  :disabled="scope.row.status !== -1"
                  @click="handleDisable(scope.row.voucherCode)"
                  v-hasPermi="['llmgate:voucher:disable']"
              >
                禁用
              </el-button>
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
    <el-dialog :title="title" :visible.sync="open" width="400px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="额度" prop="quota">
          <el-input-number
              v-model="form.quota"
              :min="0.01"
              :max="1000"
              placeholder="请输入额度"
              style="width: 100%;"
          />
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
import {addVoucher,getVoucherCodeList,disableVoucher} from "@/api/llmgate/voucher";
import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';

export default {
  name: "Voucher",
  components: { RightToolbar,Pagination },
  data() {
    return {
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
      voucherList: null,
      // 弹出层标题
      title: "",

      // 是否显示弹出层
      open: false,
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
        creator:undefined,
        consumer:undefined,
        status:undefined,
      },
      // 列信息
      columns: [
        { key: 1, label: `兑换码`, visible: true },
        { key: 2, label: `额度`, visible: true },
        { key: 3, label: `创建者`, visible: true },
        { key: 4, label: `消费者`, visible: true },
        { key: 5, label: `创建时间`, visible: true },
        { key: 6, label: `状态`, visible: true },
      ],
      // 表单校验（只要判断输入额度）
      rules: {
        quota: [
          { required: true, message: "额度不能为空", trigger: "blur" },
          { type: 'number', min: 0.01, max: 1000, message: '额度只能在（0.01-1000）之间', trigger: 'blur' }
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

    getVoucherStatusLabel(status) {
      const item = this.dict.sys_voucher_status.find(d => d.value === status);
      return item ? item.label : '未知状态';
    },
    getStatusTagType(status) {
        switch (status) {
          case 0:
            return TOAST_TYPE.danger;
          case -1:
            return TOAST_TYPE.info;
          case 1:
            return TOAST_TYPE.success;
          default:
            return TOAST_TYPE.warning;
        }
      },


    handleDisable(voucherCode) {
        this.$confirm('确定要禁用该兑换码吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          disableVoucher(voucherCode).then(() => {
            this.$message({
              type: 'success',
              message: '禁用成功!'
            });
            this.getList(); // 禁用后刷新列表
          }).catch(() => {
            this.$message({
              type: 'error',
              message: '禁用失败，请稍后重试'
            });
          });
        }).catch(() => {
          // 取消禁用，什么都不做
        });
    },


    /** 获取兑换码列表 */
    getList() {
      this.loading = true;
      getVoucherCodeList(this.queryParams).then(response => {
        this.loading = false;
        this.voucherList = response.rows;
        this.total = response.total;
      });
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

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        money:undefined
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
      this.reset();                 // 清空表单
      this.isUpdate = false;       // 表示是新增而非编辑
      this.open = true;            // 打开弹窗
      this.title = "新增兑换码";   // 设置弹窗标题
      // 初始化新字段，quota 为 ""，确保双向绑定是字符串
      this.form = {
        quota: undefined
      };
    },

    /** 提交表单 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.quota = Number(this.form.quota) || 0;
          addVoucher(this.form).then(response => {
            showTextMessage(TOAST_TYPE.success, '新增成功', TOAST_POSITION.top);
            this.open = false;
            this.getList();
          });
        }
      });
    }

  }
};
</script>