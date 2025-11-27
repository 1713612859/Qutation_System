# 报价管理系统 (Quotation Management System)

## 项目概述

这是一个全栈报价管理系统，为公司销售部门提供报价单创建、管理、审批和导出功能。

## 技术栈

### 后端
- Spring Boot 2.7.12
- MyBatis Plus 3.5.4
- MySQL 8.0
- JWT 认证
- BCrypt 密码加密

### 前端
- Vue 3
- Vuetify
- Vue Router
- Axios
- Vite

## 功能模块

### ✅ 已实现功能

1. **用户管理**
   - 用户登录、注册
   - 角色管理（ADMIN、SALES、APPROVER、VIEWER）
   - 密码加密存储
   - JWT Token认证

2. **客户管理**
   - 客户信息CRUD
   - 客户搜索

3. **产品管理**
   - 产品信息CRUD
   - SKU管理
   - 价格和税率设置

4. **套餐管理**
   - 套餐创建和编辑
   - 套餐产品关联管理
   - 套餐折扣设置

5. **报价单管理**
   - 报价单创建（支持从套餐加载）
   - 报价明细动态编辑
   - 自动计算总价（含税、含折扣）
   - 报价单状态流转（草稿→提交→审批→通过/拒绝）
   - PDF导出（HTML格式）

6. **审批流程**
   - 审批记录追踪
   - 审批操作历史

7. **附件管理**
   - 文件上传
   - 文件下载

8. **系统设置**
   - 系统参数配置

## 快速开始

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE quotation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p quotation < backend/src/main/resources/schema.sql
```

3. 修改数据库配置（`backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quotation?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: your_password
```

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

## 默认账号

- 用户名: `admin`
- 密码: `admin123`
- 角色: ADMIN

## API文档

### 认证接口
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册
- `GET /api/auth/me` - 获取当前用户信息

### 客户管理
- `GET /api/customers` - 获取客户列表
- `POST /api/customers` - 创建客户
- `PUT /api/customers/{id}` - 更新客户
- `DELETE /api/customers/{id}` - 删除客户

### 产品管理
- `GET /api/products` - 获取产品列表
- `POST /api/products` - 创建产品
- `PUT /api/products/{id}` - 更新产品
- `DELETE /api/products/{id}` - 删除产品

### 套餐管理
- `GET /api/packages` - 获取套餐列表
- `POST /api/packages` - 创建套餐
- `GET /api/packages/{id}/items` - 获取套餐产品
- `POST /api/packages/{id}/items` - 添加套餐产品

### 报价单管理
- `GET /api/quotations` - 获取报价单列表
- `POST /api/quotations` - 创建报价单
- `GET /api/quotations/{id}` - 获取报价单详情
- `POST /api/quotations/from-package` - 从套餐创建报价单
- `POST /api/quotations/{id}/submit` - 提交审批
- `POST /api/quotations/{id}/approve` - 审批通过
- `POST /api/quotations/{id}/reject` - 审批拒绝
- `GET /api/pdf/quotation/{id}` - 导出PDF

## 项目结构

```
quotation_fullstack_mybatis_plus/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/example/quotation/
│   │       ├── common/      # 通用类
│   │       ├── config/      # 配置类
│   │       ├── controller/   # 控制器
│   │       ├── entity/      # 实体类
│   │       ├── mapper/      # Mapper接口
│   │       ├── service/     # 服务层
│   │       └── util/        # 工具类
│   └── src/main/resources/
│       ├── application.yml  # 配置文件
│       └── schema.sql       # 数据库脚本
│
└── frontend/                # 前端项目
    ├── src/
    │   ├── views/           # 页面组件
    │   ├── router/          # 路由配置
    │   ├── api.js           # API封装
    │   └── main.js          # 入口文件
    └── package.json
```

## 注意事项

1. PDF导出功能目前返回HTML格式，如需真正的PDF，需要集成iText7或wkhtmltopdf
2. 文件上传目录默认为 `./uploads`，需要确保有写入权限
3. JWT Secret需要在实际部署时修改
4. 数据库密码加密使用了BCrypt算法

## 后续优化建议

1. 完善权限控制，基于角色的细粒度权限管理
2. 添加邮件发送功能，报价单发送给客户
3. 完善PDF导出，使用真正的PDF库
4. 添加数据统计报表功能
5. 添加操作日志记录
6. 支持多语言
7. 移动端适配优化

