<template>
  <div class="redeem-page">
    <!-- 金额卡片 -->
    <el-card class="redeem-card balance-card" shadow="hover">
      <div class="balance-section">
        <div class="balance-amount">${{balance.toFixed(2)}}</div>
        <div class="balance-text">当前可用额度</div>
      </div>
    </el-card>

    <!-- 兑换码卡片 -->
    <el-card class="redeem-card redeem-code-card" shadow="hover">
      <div class="code-section">
        <div class="code-title">
          <i class="el-icon-ticket"></i>
          兑换码充值
        </div>
        <el-input
            v-model="redeemCode"
            placeholder="请输入兑换码"
            prefix-icon="el-icon-key"
            class="redeem-input"
            clearable
        >
          <template #append>
            <el-button icon="el-icon-document-copy" @click="pasteClipboard">粘贴</el-button>
          </template>
        </el-input>
        <el-button type="success" size="medium" class="redeem-button" @click="handleRedeem">
          立即兑换
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import {consumeVoucherCode} from "@/api/llmgate/voucher";
import{getBalance} from "@/api/llmgate/balance";

export default {
  name: "Voucher",
  data() {
    return {
      balance:0.0,
      redeemCode: ""
    };
  },
  created() {
    this.fetchBalance()
  },
  methods: {
    fetchBalance() {
      getBalance().then(res => {
          this.balance = res.data?res.data:0.0;
      }).catch(error => {
        console.error('获取余额失败:', error);
        this.balance = 0.0;
      });
    },
    handleRedeem() {
      consumeVoucherCode(this.redeemCode).then(res => {
        if (res.code === 200) {
          this.$message.success('兑换成功');
          this.redeemCode = '';
          // 兑换成功后重新获取余额
          this.fetchBalance();
        } else {
          this.$message.error('兑换失败');
        }
      }).catch(error => {
        console.error('兑换失败:', error);
        this.$message.error('兑换失败，请稍后重试');
      });
    },
    pasteClipboard() {
      navigator.clipboard.readText().then(text => {
        this.redeemCode = text;
      }).catch(err => {
        console.error('无法读取剪贴板内容:', err);
        this.$message.error('读取剪贴板内容失败');
      });
    }

  }
};
</script>

<style scoped>
.redeem-page {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
}

.redeem-card {
  padding: 24px;
  border-radius: 10px;
}

.balance-section {
  text-align: center;
  padding: 16px;
}

.balance-amount {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
}

.balance-text {
  font-size: 18px;
  color: #409EFF;
  font-weight: bold;
  margin-top: 8px;
}


.code-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.code-title {
  font-size: 16px;
  font-weight: bold;
  color: #67c23a;
}

.redeem-input {
  width: 100%;
}

.redeem-button {
  width: 100%;
}
</style>