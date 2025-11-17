<template>
  <div>
    <v-row>
      <v-col cols="12">
        <h2 class="text-h5 text-sm-h4 mb-2 mb-sm-6">
          <v-icon class="mr-2" color="primary">mdi-view-dashboard</v-icon>
          Dashboard
        </h2>
      </v-col>
    </v-row>

    <!-- 统计卡片 -->
    <v-row class="mb-4">
      <!-- 报价单总数 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-primary'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 primary--text mb-2 font-weight-bold">
                  {{ stats.totalQuotations }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-file-document-multiple</v-icon>
                  Total Quotations
                </div>
              </div>
              <v-avatar class="stat-icon" color="primary" size="64">
                <v-icon color="white" size="32">mdi-file-document</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 待审批 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-warning'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 warning--text mb-2 font-weight-bold">
                  {{ stats.submittedQuotations }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-clock-alert</v-icon>
                  Pending
                </div>
              </div>
              <v-avatar class="stat-icon" color="warning" size="64">
                <v-icon color="white" size="32">mdi-clipboard-clock</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 已审批 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-success'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 success--text mb-2 font-weight-bold">
                  {{ stats.approvedQuotations }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-check-circle</v-icon>
                  Approved
                </div>
              </div>
              <v-avatar class="stat-icon" color="success" size="64">
                <v-icon color="white" size="32">mdi-check-circle-outline</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 客户数 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-info'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 info--text mb-2 font-weight-bold">
                  {{ stats.customers }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-account-group</v-icon>
                  Total Customers
                </div>
              </div>
              <v-avatar class="stat-icon" color="info" size="64">
                <v-icon color="white" size="32">mdi-account-multiple</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 第二行统计 -->
    <v-row class="mb-4">
      <!-- 产品数 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-purple'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 purple--text mb-2 font-weight-bold">
                  {{ stats.products }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-package-variant</v-icon>
                  Total Products
                </div>
              </div>
              <v-avatar class="stat-icon" color="purple" size="64">
                <v-icon color="white" size="32">mdi-package-variant-closed</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 套餐数 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-teal'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 teal--text mb-2 font-weight-bold">
                  {{ stats.packages }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-package</v-icon>
                  Total Packages
                </div>
              </div>
              <v-avatar class="stat-icon" color="teal" size="64">
                <v-icon color="white" size="32">mdi-package-variant</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 总金额 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-green'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h5 text-sm-h3 green--text mb-2 font-weight-bold">
                  {{ formatCurrency(stats.totalAmount) }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-currency-php</v-icon>
                  Total Amount
                </div>
              </div>
              <v-avatar class="stat-icon" color="green" size="64">
                <v-icon color="white" size="32">mdi-cash-multiple</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 草稿数 -->
      <v-col cols="12" md="3" sm="6" xs="12">
        <v-card :class="'stat-card-grey'" class="mx-auto" elevation="4">
          <v-card-text class="pa-4">
            <div class="d-flex align-center justify-space-between">
              <div class="flex-grow-1">
                <div class="text-h4 text-sm-h3 grey--text mb-2 font-weight-bold">
                  {{ stats.draftQuotations }}
                </div>
                <div class="text-body-2 text-sm-body-1 text-grey-darken-1">
                  <v-icon class="mr-1" small>mdi-file-edit</v-icon>
                  Draft Quotations
                </div>
              </div>
              <v-avatar class="stat-icon" color="grey" size="64">
                <v-icon color="white" size="32">mdi-file-edit-outline</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 快速操作卡片 -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="bg-primary text-white">
            <v-icon class="mr-2">mdi-lightning-bolt</v-icon>
            Quick operation
          </v-card-title>
          <v-card-text class="pa-4">
            <v-row dense>
              <v-col cols="12" sm="6">
                <v-btn
                    block
                    color="primary"
                    size="large"
                    variant="outlined"
                    @click="$router.push('/quotations/new')"
                >
                  <v-icon left>mdi-plus-circle</v-icon>
                  Add Quotation
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                    block
                    color="success"
                    size="large"
                    variant="outlined"
                    @click="$router.push('/customers')"
                >
                  <v-icon left>mdi-account-plus</v-icon>
                  Add Customer
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                    v-if="isAdmin"
                    block
                    color="info"
                    size="large"
                    variant="outlined"
                    @click="$router.push('/products')"
                >
                  <v-icon left>mdi-package-plus</v-icon>
                  Add Product
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                    v-if="isAdmin"
                    block
                    color="warning"
                    size="large"
                    variant="outlined"
                    @click="$router.push('/packages')"
                >
                  <v-icon left>mdi-package-variant-closed-plus</v-icon>
                  Add Package
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card elevation="2">
          <v-card-title class="bg-success text-white">
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            Data Summary
          </v-card-title>
          <v-card-text class="pa-4">
            <v-list density="compact">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="primary">mdi-file-document-check</v-icon>
                </template>
                <v-list-item-title>Approved Quote Ratio</v-list-item-title>
                <template v-slot:append>
                  <v-chip color="success" size="small">
                    {{ getApprovalRate() }}%
                  </v-chip>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="warning">mdi-clock</v-icon>
                </template>
                <v-list-item-title>Pending Quote Ratio</v-list-item-title>
                <template v-slot:append>
                  <v-chip color="warning" size="small">
                    {{ getPendingRate() }}%
                  </v-chip>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="info">mdi-account</v-icon>
                </template>
                <v-list-item-title>Average Quotes per Customer</v-list-item-title>
                <template v-slot:append>
                  <v-chip color="info" size="small">
                    {{ getAvgQuotationsPerCustomer() }}
                  </v-chip>
                </template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon color="purple">mdi-cash</v-icon>
                </template>
                <v-list-item-title>Average Quote Amount</v-list-item-title>
                <template v-slot:append>
                  <v-chip color="purple" size="small">
                    {{ formatCurrency(getAvgAmount()) }}
                  </v-chip>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import api from '../api.js'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        totalQuotations: 0,
        submittedQuotations: 0,
        approvedQuotations: 0,
        draftQuotations: 0,
        customers: 0,
        products: 0,
        packages: 0,
        totalAmount: 0
      },
      quotations: [],
      quotationAmounts: [],
      userInfo: {},
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadStats()
  },

  computed: {
    isAdmin() {
      return this.userInfo?.role === 'ADMIN'
    }
  },

  methods: {

    async loadUserInfo() {
      try {
        const res = await api.get('/auth/me')
        this.userInfo = res.data.data
      } catch (error) {
        console.error('Loading userinfo failed ', error)
      }
    },
    // 加载统计值
    async loadStats() {
      try {
        const [quotations, customers, products, packages] = await Promise.all([
          api.get('/quotations'),
          api.get('/customers'),
          api.get('/products'),
          api.get('/packages')
        ])

        this.quotations = quotations.data.data
        this.stats.totalQuotations = this.quotations.length || 0
        this.stats.customers = customers.data.data.length || 0
        this.stats.products = products.data.data.length || 0
        this.stats.packages = packages.data.data.length || 0

        const submitted = this.quotations.filter(q => q.status === 'SUBMITTED')
        const approved = this.quotations.filter(q => q.status === 'APPROVED')
        const drafts = this.quotations.filter(q => q.status === 'DRAFT')

        this.stats.submittedQuotations = submitted.length || 0
        this.stats.approvedQuotations = approved.length || 0
        this.stats.draftQuotations = drafts.length || 0

        // 计算总金额
        this.quotationAmounts = this.quotations
            .filter(q => q.total != null && q.status !== 'DRAFT')
            .map(q => parseFloat(q.total) || 0)
        this.stats.totalAmount = this.quotationAmounts.reduce((sum, amt) => sum + amt, 0)
      } catch (error) {
        console.error('加载统计数据失败', error)
      }
    },
    // 格式化
    formatCurrency(amount) {
      if (!amount && amount !== 0) return '0.00'
      return new Intl.NumberFormat('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(amount)
    },
    // 审核通过百分比
    getApprovalRate() {
      if (this.stats.totalQuotations === 0) return 0
      return Math.round((this.stats.approvedQuotations / this.stats.totalQuotations) * 100)
    },
    // 待办百分比
    getPendingRate() {
      if (this.stats.totalQuotations === 0) return 0
      return Math.round((this.stats.submittedQuotations / this.stats.totalQuotations) * 100)
    },
    // 获取平均客单价
    getAvgQuotationsPerCustomer() {
      if (this.stats.customers === 0) return '0.0'
      return (this.stats.totalQuotations / this.stats.customers).toFixed(1)
    },
    // 获取平均单价
    getAvgAmount() {
      if (this.quotationAmounts.length === 0) return 0
      return this.quotationAmounts.reduce((sum, amt) => sum + amt, 0) / this.quotationAmounts.length
    }
  }
}
</script>

<style scoped>
.stat-card-primary {
  background: linear-gradient(135deg, rgba(25, 118, 210, 0.1) 0%, rgba(25, 118, 210, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-primary:hover {
  transform: translateY(-4px);
}

.stat-card-warning {
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.1) 0%, rgba(255, 152, 0, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-warning:hover {
  transform: translateY(-4px);
}

.stat-card-success {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1) 0%, rgba(76, 175, 80, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-success:hover {
  transform: translateY(-4px);
}

.stat-card-info {
  background: linear-gradient(135deg, rgba(33, 150, 243, 0.1) 0%, rgba(33, 150, 243, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-info:hover {
  transform: translateY(-4px);
}

.stat-card-purple {
  background: linear-gradient(135deg, rgba(156, 39, 176, 0.1) 0%, rgba(156, 39, 176, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-purple:hover {
  transform: translateY(-4px);
}

.stat-card-teal {
  background: linear-gradient(135deg, rgba(0, 150, 136, 0.1) 0%, rgba(0, 150, 136, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-teal:hover {
  transform: translateY(-4px);
}

.stat-card-green {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1) 0%, rgba(76, 175, 80, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-green:hover {
  transform: translateY(-4px);
}

.stat-card-grey {
  background: linear-gradient(135deg, rgba(158, 158, 158, 0.1) 0%, rgba(158, 158, 158, 0.05) 100%);
  transition: transform 0.2s;
}

.stat-card-grey:hover {
  transform: translateY(-4px);
}

.stat-icon {
  opacity: 0.8;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.8;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}
</style>
