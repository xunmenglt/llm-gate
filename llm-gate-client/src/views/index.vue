<template>
  <div class="gateway-home p-6">
    <!-- 顶部介绍卡片 -->
    <el-card shadow="hover" class="mb-6">
      <template #header>
        <span class="text-xl font-semibold">Codura 智能网关平台</span>
      </template>
      <p>
        本平台对外提供统一的 OpenAI API 接口，用户在平台注册后，通过兑换码充值金额，
        创建专属 API-KEY，即可与传统 OpenAI 接口一样方便地调用模型服务。
      </p>
    </el-card>

    <!-- 主体区域：栅格布局 -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="font-semibold">主要功能流程</span>
          </template>
          <ul class="list-disc list-inside leading-7">
            <li>使用兑换码充值金额</li>
            <li>创建自己的 API-KEY 并设置额度</li>
            <li>通过 API-KEY + 代理接口调用模型</li>
            <li>可查看余额、资金流水和调用日志</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="font-semibold">整体系统逻辑概览</span>
          </template>
          <ol class="list-decimal list-inside leading-7">
            <li>兑换码：管理员创建，用户充值使用</li>
            <li>资金流水：记录用户充值和消费情况</li>
            <li>API-KEY：用户自行创建和配置额度</li>
            <li>余额：决定用户可用调用额度</li>
            <li>模型配置：管理员配置支持的模型</li>
            <li>模型服务商：管理员配置模型来源</li>
            <li>调用日志：记录用户每次模型调用</li>
            <li>倍率管理：控制不同模型调用费用</li>
          </ol>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="font-semibold">普通用户可以做的</span>
          </template>
          <ul class="list-disc list-inside leading-7">
            <li>充值兑换码</li>
            <li>创建/管理 API-KEY</li>
            <li>查看余额和调用流水</li>
            <li>调用模型服务</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="font-semibold">管理员可以做的</span>
          </template>
          <ul class="list-disc list-inside leading-7">
            <li>创建兑换码</li>
            <li>配置模型和服务商</li>
            <li>设置调用倍率</li>
            <li>查看/管理调用日志</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 静态展示，如需动态获取数据可在这里接接口
</script>

<style scoped>
.gateway-home {
  background: #f9fafb; /* 柔和浅灰背景 */
  min-height: 100vh;
}
.mb-6 {
  margin-bottom: 1.5rem;
}
.font-semibold {
  font-weight: 600;
}
</style>
