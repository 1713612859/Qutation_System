<template>
  <v-app>
    <v-app-bar color="primary" dark>
      <v-app-bar-nav-icon
          @click.stop="drawer = !drawer"
          class="d-md-none"
      ></v-app-bar-nav-icon>
      <v-app-bar-title>Quotation System</v-app-bar-title>
      <v-spacer></v-spacer>
      <v-chip color="white" variant="text" class="mr-2 d-none d-sm-flex">
        {{ userInfo.fullName || userInfo.username }}
      </v-chip>
      <v-btn icon @click="logout">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </v-app-bar>

    <v-navigation-drawer
        v-model="drawer"
        :permanent="$vuetify.display.mdAndUp"
        temporary
    >
      <v-list density="compact" nav>
        <v-list-item
            prepend-icon="mdi-view-dashboard"
            title="Dashboard"
            value="dashboard"
            :to="'/dashboard'"
            :active="activeMenu === '/dashboard'"
        ></v-list-item>
        <v-list-item
            prepend-icon="mdi-file-document"
            title="Quotations"
            value="quotations"
            :to="'/quotations'"
            :active="activeMenu === '/quotations'"
        ></v-list-item>
        <v-list-item
            prepend-icon="mdi-account-group"
            title="Customers"
            value="customers"
            :to="'/customers'"
            :active="activeMenu === '/customers'"
        ></v-list-item>
        <v-list-item
            prepend-icon="mdi-watermark"
            title="Brands"
            value="brands"
            :to="'/brands'"
            :active="activeMenu === '/brands'"
        ></v-list-item>
        <v-list-item
            prepend-icon="mdi-shape"
            title="Category"
            value="category"
            :to="'/category'"
            :active="activeMenu === '/category'"
        ></v-list-item>
        <v-list-item
            v-if="isAdmin"
            prepend-icon="mdi-package-variant"
            title="Products"
            value="products"
            :to="'/products'"
            :active="activeMenu === '/products'"
        ></v-list-item>
        <v-list-item
            v-if="isAdmin"
            prepend-icon="mdi-package-variant-closed"
            title="Packages"
            value="packages"
            :to="'/packages'"
            :active="activeMenu === '/packages'"
        ></v-list-item>
        <v-list-item
            v-if="isAdmin"
            prepend-icon="mdi-account"
            title="Users"
            value="users"
            :to="'/users'"
            :active="activeMenu === '/users'"
        ></v-list-item>

      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid class="pa-2 pa-sm-4">
        <Suspense>
          <template #default>
            <router-view/>
          </template>
          <template #fallback>
            <div class="loading-state-overlay">
              <v-progress-circular
                  indeterminate
                  color="primary"
                  size="64"
                  width="6"
              ></v-progress-circular>
              <p class="mt-4 text-subtitle-1">Loading...</p>
            </div>
          </template>
        </Suspense>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import api from '../api.js'

export default {
  name: 'Layout',
  // 注意：在 Options API 中，无需显式导入 Suspense，Vue 会识别模板中的标签。
  data() {
    return {
      drawer: null,
      userInfo: {}
    }
  },
  computed: {
    activeMenu() {
      // 修正：确保只匹配基础路径，以正确高亮菜单
      // 例如：/quotations/1/preview 应该高亮 /quotations 菜单
      const pathSegments = this.$route.path.split('/');
      // 基础路径，例如 '/quotations'
      return '/' + pathSegments[1]
    },
    isAdmin() {
      return this.userInfo.role === 'ADMIN'
    }
  },
  mounted() {
    this.loadUserInfo()
    // 根据屏幕大小设置drawer初始状态
    this.drawer = this.$vuetify.display.mdAndUp
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
    logout() {
      localStorage.removeItem('token')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* 确保加载状态居中显示 */
.loading-state-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh; /* 确保它占据页面大部分空间 */
  padding: 20px;
}
</style>
