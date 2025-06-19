<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">

        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="75px">
          <el-form-item label="模型名称" prop="modelName">
            <el-input
                v-model="queryParams.modelName"
                placeholder="请输入模型名称"
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
                v-hasPermi="['llmgate:multiplier:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                plain
                icon="el-icon-delete"
                size="mini"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['llmgate:multiplier:delete']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="multiplierList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="模型名称" align="center" key="modelName" prop="modelName" v-if="columns[0].visible" :show-overflow-tooltip="true" />
          <el-table-column label="输入定价((/K tokens)" align="center" key="inputPrivice" prop="inputPrivice" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="回复定价(/K tokens)" align="center" key="inputPrivice" prop="inputPrivice" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="倍率" align="center" key="rate" prop="rate" v-if="columns[3].visible" :show-overflow-tooltip="true" />
          <el-table-column label="时间" align="center" prop="createTime" v-if="columns[0].visible" width="160">
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

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="选择模型" prop="modelName">
              <el-select
                  v-model="form.modelName"
                  placeholder="请选择模型"
                  clearable
                  filterable
                  style="width: 100%;"
              >
                <el-option
                    v-for="item in modelOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="输入定价" prop="inputPrice">
              <el-input-number
                  v-model="form.inputPrivice"
                  :min="0"
                  :max="10"
                  :step="0.01"
                  :precision="3"
                  placeholder="请输入输入定价(/k Tokens)"
                  style="width: 100%;"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="回复定价" prop="outputPrice">
              <el-input-number
                  v-model="form.outputPrivice"
                  :min="0"
                  :max="10"
                  :step="0.01"
                  :precision="3"
                  placeholder="请输入回复定价(/k Tokens)"
                  style="width: 100%;"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="倍率" prop="rate">
              <el-input-number
                  v-model="form.rate"
                  :min="1"
                  :max="10"
                  :step="0.1"
                  :precision="3"
                  placeholder="请输入倍率"
                  style="width: 100%;"
              />
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
import {getMultiplierList,addMultiplier,delMultiplier} from "@/api/llmgate/multiplier";
import {modelMapList} from "@/api/llmgate/modelmap";
import {SYS_DICT, TOAST_POSITION, TOAST_TYPE} from '@/plugins/Constants'
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { showTextMessage } from '@/plugins/toastification';


export default {
  name: "Multiplier",
  components: { RightToolbar,Pagination },
  data() {
    return {
      modelOptions:[],
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
      multiplierList: null,
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
        modelName:undefined,
      },
      // 列信息
      columns: [
        { key: 1, label: `模型名称`, visible: true },
        { key: 2, label: `输入定价((/K tokens)`, visible: true },
        { key: 3, label: `回复定价((/K tokens)`, visible: true },
        { key: 4, label: `倍率`, visible: true },
        { key: 5, label: `时间`, visible: true }
      ],
      rules:{

      },
      // 字典
      dict:SYS_DICT
    };
  },
  created() {
    this.getList();
  },
  methods: {
    async loadModelOptions() {
      try {
        console.log(1111)
        const param={
              pageNum: 1,
              pageSize: 10
        }
        // 并行请求模型映射表和已设置倍率的列表
        const [mapRes, multiplierRes] = await Promise.all([
          modelMapList(param),        // 所有模型列表（已注册的模型）
          getMultiplierList(param)      // 已设置倍率的模型列表
        ]);
        console.log(1111)
        const allModelList = mapRes.rows || [];
        const multiplierList = multiplierRes.rows || [];

        // 提取已设置倍率的模型名称集合
        const usedModelNames = new Set(multiplierList.map(item => item.modelName));

        // 差集过滤：只保留未设置倍率的模型
        this.modelOptions = allModelList
            .filter(item => !usedModelNames.has(item.modelName))
            .map(item => ({
              label: item.modelName,
              value: item.modelName
            }));

      } catch (error) {
        console.error("获取模型选项失败：", error);
        this.modelOptions = [];
      }
    },

    handleAdd() {
      this.reset();
      this.loadModelOptions();

      this.isUpdate=false;
      this.open = true;
      this.title = "设置倍率";
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      getMultiplierList(this.queryParams).then(response => {
            this.multiplierList = response.rows;
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

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        modelName: undefined,
        inputPrivice: 0,
        outputPrivice: 0,
        rate: 1,
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
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },


    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
            addMultiplier(this.form).then(response => {
              showTextMessage(TOAST_TYPE.success,'新增成功',TOAST_POSITION.top)
              this.open = false;
              this.getList();
            });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const Ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除所选数据项？').then(function() {
        return delMultiplier(Ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>