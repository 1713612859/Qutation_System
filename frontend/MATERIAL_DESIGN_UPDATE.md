# Material Design UI 更新说明

## 更新内容

前端UI框架已从 Element Plus 更新为 Vuetify (Google Material Design)。

## 安装依赖

请运行以下命令安装新的依赖：

```bash
cd frontend
npm install
```

## 主要变更

### 1. 依赖更新
- 移除了 `element-plus` 和 `@element-plus/icons-vue`
- 添加了 `vuetify`、`@mdi/font` 和 `vite-plugin-vuetify`

### 2. 配置更新
- `vite.config.js`: 添加了 Vuetify 插件配置
- `main.js`: 配置了 Vuetify 主题和图标
- `App.vue`: 使用 `v-app` 组件包裹应用

### 3. 组件更新
所有页面组件已更新为 Material Design 风格：
- ✅ Layout.vue - 使用 Material 导航栏和侧边栏
- ✅ Login.vue - Material 风格的登录表单
- ✅ Register.vue - Material 风格的注册表单
- ✅ Dashboard.vue - Material 风格的统计卡片
- ✅ QuotationList.vue - Material 风格的数据表格
- ✅ QuotationForm.vue - Material 风格的报价单表单
- ✅ CustomerList.vue - Material 风格的客户管理
- ✅ ProductList.vue - Material 风格的产品管理
- ✅ PackageList.vue - Material 风格的套餐管理
- ✅ UserList.vue - Material 风格的用户管理

## Material Design 特性

### 主题颜色
- Primary: #1976D2 (蓝色)
- Secondary: #424242 (灰色)
- Accent: #82B1FF (浅蓝色)
- Error: #FF5252 (红色)
- Success: #4CAF50 (绿色)
- Warning: #FB8C00 (橙色)
- Info: #2196F3 (信息蓝)

### 图标
使用 Material Design Icons (MDI)

### 组件风格
- 卡片式布局 (Cards)
- 数据表格 (Data Tables)
- Material 按钮和输入框
- 对话框 (Dialogs)
- 导航栏和侧边栏

## 注意事项

1. 提示信息：由于简化处理，部分提示使用了 `alert()` 而不是 Vuetify 的 snackbar。可以在后续优化中替换为更优雅的提示方式。

2. 日期选择器：Material Design 的日期选择器与 Element Plus 略有不同，请确保日期格式正确。

3. 数据表格：Vuetify 的数据表格功能强大，支持搜索和排序等功能。

## 启动项目

```bash
cd frontend
npm run dev
```

访问 http://localhost:5173 查看更新后的 Material Design 界面。

