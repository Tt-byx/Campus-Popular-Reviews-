# 校圈 (Campus Popular Reviews)

> 🎓 一个学生共建的校园点评社区，像大众点评 + 虎扑一样，评课、评食堂、评社团、评活动。

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.4.3-red?style=flat-square)
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
- **模板引擎**: Thymeleaf
- **对象存储**: 阿里云 OSS 3.15.0
- **工具库**: Lombok 1.18.38
- **AOP**: Spring AOP
- **监控**: Spring Boot Actuator

### 前端技术
- **页面模板**: Thymeleaf + HTML5 + CSS3
- **样式框架**: 自定义 CSS（计划迁移至 Vue 3 + Element Plus）

### 开发工具
- **构建工具**: Maven 3.x
- **版本控制**: Git
- **项目管理**: TAPD（后续接入）

### 部署环境
- **服务器**: 阿里云学生机
- **JDK 版本**: Java 17
- **应用端口**: 10086

---

## 📂 项目结构

