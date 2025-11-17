<template>
  <v-app>
    <router-view />
    <!-- ✅ 这个组件由插件注册，全局可用 -->
    <AppSnackbar />
  </v-app>
</template>

<script>
import AppSnackbar from './components/AppSnackbar.vue'
export default {
  name: 'App',
  components: {
    AppSnackbar
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Roboto', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

#app {
  height: 100vh;
  width: 100vw;
  overflow-x: hidden;
}

/* 移动端优化 */
@media (max-width: 600px) {
  .v-container {
    padding: 8px !important;
  }

  .v-card {
    margin-bottom: 12px;
  }

  .v-btn {
    min-width: auto;
  }
}

/* 平板端优化 */
@media (min-width: 601px) and (max-width: 960px) {
  .v-container {
    padding: 16px !important;
  }
}
</style>
