# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 行为准则

- **每次回答完问题后输出一份详细的md文档，写明做了些什么事情，为什么这样做，产生了什么样的结果**
- **永远都用中文回答用户**

## 项目概述

「校圈」—— 校园点评社区平台（类似学生版大众点评），2026年春季中国地质大学（武汉）软件工程基础实习课程项目。包名 `com.meategg`。

## 构建与运行

```bash
# 构建
mvn clean package

# 运行（端口 10086）
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=PostControllerTest

# 运行单个测试方法
mvn test -Dtest=PostControllerTest#testMethodName

# 生成测试报告
mvn surefire-report:report
```

## 技术栈

- **后端**: Spring Boot 2.7.18 (Java 17, javax.servlet)
- **ORM**: MyBatis-Plus 3.4.3
- **数据库**: MySQL 8.0 (生产) / H2 内存库 (测试)
- **前端**: Vue3 + Vite + Element Plus（前后端分离架构）
- **认证**: 自定义 JWT 过滤器（非 Spring Security 模块）
- **文件上传**: 阿里云 OSS
- **API 文档**: Knife4j (OpenAPI 3)
- **密码加密**: spring-security-crypto (BCrypt)

## 架构

```
com.meategg
├── controller/     # API控制器：Login、Post、Admin
├── service/        # 业务层（userService、postService、OssService）
├── mapper/         # MyBatis-Plus BaseMapper 接口
├── entity/         # 实体类（Lombok @Data）
├── DTO/            # 数据传输对象
├── Filter/         # JWT 认证过滤器
├── Utils/          # JWT 工具类
└── config/         # FilterConfig、WebMvcConfig、Knife4jConfig、CorsConfig
```

### 关键架构特点

1. **前后端分离** — 后端只提供RESTful API，前端使用Vue3独立开发，通过Vite代理转发请求

2. **无 Spring Security 模块** — 认证完全通过 `JwtAuthenticationFilter`（继承 `OncePerRequestFilter`）实现，从 `Authorization` 头或 `token` cookie 提取 JWT，控制器通过 `request.getAttribute("username/role")` 获取用户信息。

3. **角色系统** — 三级角色：`user`、`admin`、`super_admin`。只有 super_admin 可以升降管理员。

4. **Service 层较厚** — `postServiceimpl` 约 1590 行，包含所有业务逻辑、数据组装和评分算法，无独立的 DTO 转换层。

5. **评论模型两张表** — `comment_user`（谁评论了什么）和 `comment_content`（评论内容和评分），每个用户对每个点评目标只能评论一次。

6. **级联删除** — 删除帖子会级联删除关联的点评目标、评论用户和评论内容；注销账号也会级联清理。

7. **违禁词过滤** — `postServiceimpl.checkBannedWords()` 每次提交内容时从数据库加载全部违禁词做字符串包含检查。

## 数据库表

| 表名 | 说明 |
|------|------|
| `user` | 用户（id, username, password, role, status, avatar, signature） |
| `post` | 帖子（id, userId, title, content, tag, imageUrl, viewCount） |
| `review_target` | 点评目标（id, postId, targetName） |
| `comment_user` | 评论用户（id, reviewTargetId, username） |
| `comment_content` | 评论内容（id, reviewTargetId, commentId, content, score 1-5） |
| `banned_word` | 违禁词（id, word） |

## 测试

- Controller 测试：`@WebMvcTest` + MockMvc + Mockito
- Service 测试：Mockito `@ExtendWith(MockitoExtension.class)`
- 集成测试：`@SpringBootTest` + H2 内存库 + `@ActiveProfiles("test")`
- 测试配置：`src/test/resources/application-test.yml`

## API 路由前缀

- `/user/*` — 登录、注册、个人信息
- `/post/*` — 帖子、点评目标、评论、搜索、排行
- `/admin/*` — 管理后台（用户管理、内容管理、违禁词）

## 前端项目

前端代码位于 `campus-forum-web/` 目录，使用Vue3 + Vite + Element Plus：

```bash
# 安装依赖
cd campus-forum-web
npm install

# 启动开发服务器（端口80）
npm run dev

# 构建生产版本
npm run build
```

前端通过Vite代理将 `/api` 请求转发到后端 `http://localhost:10086`
