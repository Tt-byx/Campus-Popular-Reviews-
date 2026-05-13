# 登录功能修复报告

## 概述

本次操作修复了「校圈」项目前后端分离架构下的登录功能问题。用户点击登录按钮时返回401 Unauthorized错误，经排查发现是Vite代理配置和后端Controller路径不一致导致的。

---

## 问题描述

### 错误现象

访问 `http://localhost/login` 页面，输入账号密码点击登录按钮后：
- 页面停留在登录页，无法跳转到首页
- 浏览器控制台报错：`401 (Unauthorized) @ http://localhost/api/user/login`

### 错误原因

1. **Vite代理配置问题**：
   - 前端axios的baseURL设置为 `/api`
   - 前端请求路径：`/api/user/login`
   - Vite代理直接转发到：`http://localhost:10086/api/user/login`
   - 但后端期望的路径是：`/user/login`（没有 `/api` 前缀）

2. **后端Controller路径不一致**：
   - 部分Controller有 `/api` 前缀：`/api/notification`、`/api/message`、`/api/like`、`/api/follow`、`/api/favorite`
   - 部分Controller没有 `/api` 前缀：`/user`、`/post`、`/admin`
   - 这导致代理配置无法统一处理

---

## 执行的修复操作

### 1. 修改Vite代理配置

**文件路径：** `campus-forum-web/vite.config.js`

**修改内容：**
```javascript
// 修改前
proxy: {
  '/api': {
    target: 'http://localhost:10086',
    changeOrigin: true,
  }
}

// 修改后
proxy: {
  '/api': {
    target: 'http://localhost:10086',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

**作用：** 添加 `rewrite` 选项，在代理请求时去掉 `/api` 前缀

### 2. 统一后端Controller路径

修改了5个Controller，去掉 `/api` 前缀：

| Controller | 修改前 | 修改后 |
|------------|--------|--------|
| NotificationController | `/api/notification` | `/notification` |
| MessageController | `/api/message` | `/message` |
| LikeController | `/api/like` | `/like` |
| FollowController | `/api/follow` | `/follow` |
| FavoriteController | `/api/favorite` | `/favorite` |

**修改的文件：**
- `src/main/java/com/meategg/controller/NotificationController.java`
- `src/main/java/com/meategg/controller/MessageController.java`
- `src/main/java/com/meategg/controller/LikeController.java`
- `src/main/java/com/meategg/controller/FollowController.java`
- `src/main/java/com/meategg/controller/FavoriteController.java`

---

## 为什么这样做

### 问题根源

项目从前后端一体改造为前后端分离时，部分Controller保留了旧的 `/api` 前缀，而新添加的Controller没有这个前缀，导致路径不一致。

### 解决方案选择

**方案1：修改Vite代理配置（采用）**
- 优点：前端代码不需要修改，保持 `/api` 前缀的清晰语义
- 缺点：需要在代理时进行路径重写

**方案2：修改前端API路径**
- 优点：不需要修改代理配置
- 缺点：需要修改所有前端API文件，工作量大

**方案3：统一后端路径（采用）**
- 优点：保持后端API路径一致性，符合RESTful规范
- 缺点：需要修改多个Controller文件

最终采用方案1+方案3的组合，既保持前端代码不变，又统一后端路径。

---

## 产生的结果

### 1. 登录功能恢复正常

✅ 用户可以正常登录
- 输入用户名和密码
- 点击登录按钮
- 成功跳转到首页 `http://localhost/`
- 页面显示用户信息和帖子列表

### 2. API路径统一

✅ 所有后端API路径现在都没有 `/api` 前缀：
- `/user/login` - 用户登录
- `/user/create` - 用户注册
- `/post/list` - 帖子列表
- `/notification/unread-count` - 未读通知数
- `/message/unread-count` - 未读消息数
- 等等...

### 3. 前后端通信正常

✅ Vite代理正确工作：
- 前端请求 `/api/xxx`
- 代理去掉 `/api` 前缀
- 转发到后端 `/xxx`
- 后端正确处理请求

### 4. 页面功能完整

✅ 首页正常显示：
- 用户信息（用户名、头像、签名）
- 帖子列表
- 导航菜单（首页、分类、排行、收藏、私信、通知、设置、管理）
- 搜索框、发帖按钮

---

## 测试验证

### 测试步骤

1. 访问 `http://localhost/login`
2. 输入用户名：`rdsg`
3. 输入密码：`123456`
4. 点击登录按钮

### 测试结果

✅ 登录成功
- 页面跳转到 `http://localhost/`
- 标题显示「首页 - 校圈」
- 显示用户信息：用户名 `rdsg`，签名「肉蛋是大骚gay」
- 显示帖子列表

### 控制台状态

- 原有错误已消除：不再有 `401 Unauthorized` 错误
- 仅剩 favicon.ico 的 404 错误（不影响功能）

---

## 相关文件清单

| 文件路径 | 操作类型 | 说明 |
|---------|---------|------|
| `campus-forum-web/vite.config.js` | 修改 | 添加代理路径重写配置 |
| `src/.../controller/NotificationController.java` | 修改 | 去掉 `/api` 前缀 |
| `src/.../controller/MessageController.java` | 修改 | 去掉 `/api` 前缀 |
| `src/.../controller/LikeController.java` | 修改 | 去掉 `/api` 前缀 |
| `src/.../controller/FollowController.java` | 修改 | 去掉 `/api` 前缀 |
| `src/.../controller/FavoriteController.java` | 修改 | 去掉 `/api` 前缀 |

---

## 后续建议

### 1. 保持路径一致性

今后添加新的Controller时，统一不使用 `/api` 前缀，由Vite代理统一处理。

### 2. API文档更新

访问 `http://localhost:10086/doc.html` 查看Knife4j API文档，确认所有接口路径已更新。

### 3. 测试其他功能

建议测试以下功能确保没有遗漏：
- 注册功能
- 发帖功能
- 评论功能
- 点赞/收藏功能
- 私信功能
- 通知功能

---

## 总结

本次修复解决了前后端分离架构下的登录功能问题。根本原因是Vite代理配置和后端Controller路径不一致，通过添加代理路径重写和统一后端路径两种方式解决。

**修复效果：**
- ✅ 登录功能恢复正常
- ✅ API路径统一规范
- ✅ 前后端通信正常
- ✅ 页面功能完整可用

**关键修改：**
- Vite代理添加 `rewrite` 选项去掉 `/api` 前缀
- 5个Controller统一去掉 `/api` 前缀
- 重启后端服务加载新代码

---

*报告生成时间：2026年5月13日*
*修复人：Claude Code Assistant*
