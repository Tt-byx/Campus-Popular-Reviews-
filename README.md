# 校圈 (Campus Popular Reviews)

> 🎓 一个学生共建的校园点评社区，像大众点评 + 虎扑一样，评课、评食堂、评社团、评活动。

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.4.3-red?style=flat-square)
![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?style=flat-square&logo=vuedotjs)
![Vite](https://img.shields.io/badge/Vite-5.0-646CFF?style=flat-square&logo=vite)
![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5-409EFF?style=flat-square&logo=element)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

**武汉中国地质大学软件工程课程实习作品 · 2026年春季**

</div>

---

## 📖 项目简介

**校圈**是一个面向大学生的校园生活点评平台，致力于构建真实、有用、有趣的校园信息共享社区。通过学号验证确保用户真实性，同时支持匿名发布保护隐私，让学生们可以自由分享对课程、教师、食堂、社团等校园生活的真实评价。

### ✨ 核心功能

- 📚 **课程 & 教师评价**
  - 多维度评分系统（教学质量、作业量、给分情况等）
  - 标签化评价（#给分好 #作业多 #讲课生动）
  - 雷达图可视化展示
  
- 🍜 **食堂档口反馈**
  - 实时评分与评论
  - 排队情况实时更新
  - 美食实拍上传分享
  
- 🎯 **社团/活动聚合**
  - 招新信息集中展示
  - 成员真实体验分享
  - 活动质量评价
  
- 🔒 **身份验证与隐私保护**
  - 学号实名认证确保可信度
  - 支持匿名发布保护隐私
  - JWT Token 安全认证

---

## 🛠 技术栈

### 后端技术
- **核心框架**: Spring Boot 2.7.18
- **安全认证**: Spring Security Crypto + JWT (jjwt 0.11.5)
- **持久层**: MyBatis Plus 3.4.3
- **数据库**: MySQL 8.0
- **对象存储**: 阿里云 OSS 3.15.0
- **工具库**: Lombok 1.18.38
- **API 文档**: Knife4j (Swagger)
- **AOP**: Spring AOP
- **监控**: Spring Boot Actuator

### 前端技术
- **核心框架**: Vue 3.4（Composition API + `<script setup>`）
- **构建工具**: Vite 5.0
- **UI 组件库**: Element Plus 2.5
- **路由管理**: Vue Router 4
- **状态管理**: Pinia
- **HTTP 客户端**: Axios
- **样式预处理**: SCSS

### 开发工具
- **后端构建**: Maven 3.x
- **前端构建**: Vite + npm
- **版本控制**: Git
- **项目管理**: TAPD（后续接入）

### 部署环境
- **服务器**: 阿里云学生机
- **JDK 版本**: Java 17
- **后端端口**: 10086
- **前端端口**: 5173（开发）/ Nginx（生产）

---

## 📂 项目结构

```
Campus-Popular-Reviews-/
├── campus-forum-web/          ← Vue3 前端项目
│   ├── src/
│   │   ├── api/               ← API 接口封装
│   │   ├── assets/styles/     ← 全局样式（SCSS）
│   │   ├── components/        ← 公共组件
│   │   │   ├── common/        ← 通用组件（头像等）
│   │   │   ├── layout/        ← 布局组件（Header、Sidebar）
│   │   │   ├── post/          ← 帖子相关组件
│   │   │   └── social/        ← 社交组件（点赞、分享、聊天）
│   │   ├── router/            ← Vue Router 路由配置
│   │   ├── stores/            ← Pinia 状态管理
│   │   ├── utils/             ← 工具函数
│   │   ├── views/             ← 页面视图
│   │   │   └── admin/         ← 管理后台页面
│   │   ├── App.vue            ← 根组件
│   │   └── main.js            ← 入口文件
│   ├── index.html             ← HTML 模板
│   ├── package.json           ← 前端依赖
│   └── vite.config.js         ← Vite 配置
├── src/                       ← Spring Boot 后端项目
│   ├── main/java/com/meategg/
│   │   ├── config/            ← 配置类（CORS、MyBatis Plus、Knife4j）
│   │   ├── controller/        ← RESTful API 控制器
│   │   ├── DTO/               ← 数据传输对象
│   │   ├── entity/            ← 数据库实体
│   │   ├── Filter/            ← JWT 认证过滤器
│   │   ├── mapper/            ← MyBatis Plus Mapper
│   │   ├── service/           ← 业务逻辑层
│   │   └── Utils/             ← 工具类（JWT）
│   ├── main/resources/
│   │   ├── application.yaml   ← 应用配置
│   │   └── *.xml              ← MyBatis XML 映射
│   └── test/                  ← 单元测试
├── docs/                      ← 文档与 SQL 脚本
├── pom.xml                    ← Maven 配置
└── .gitignore
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.x
- Node.js 18+
- MySQL 8.0

### 后端启动

```bash
# 1. 创建数据库并导入表结构
mysql -u root -p < docs/schema-migration.sql

# 2. 修改 src/main/resources/application.yaml 中的数据库连接信息

# 3. 启动后端服务
mvn spring-boot:run
```

后端启动后访问：
- API 服务：http://localhost:10086
- API 文档（Knife4j）：http://localhost:10086/doc.html

### 前端启动

```bash
# 进入前端目录
cd campus-forum-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问：http://localhost:5173

### 生产构建

```bash
# 前端打包
cd campus-forum-web
npm run build

# 将 dist/ 目录部署到 Nginx 或其他静态服务器
```

