package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.DTO.LoginResponse;
import com.meategg.Utils.JwtUtils;
import com.meategg.entity.*;
import com.meategg.mapper.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class userServiceimpl extends ServiceImpl<UserMapper, User> implements userService {
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private CommentUserMapper commentUserMapper;
    @Resource
    private PostMapper postMapper;
    @Resource
    private ReviewTargetMapper reviewTargetMapper;
    @Resource
    private CommentContentMapper commentContentMapper;


    @Override
    public Result login(String username, String password) {
        User user = query().eq("username", username).one();
        if(user == null){
            return Result.fail(401, "用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }

        String role = user.getRole() != null ? user.getRole() : "user";
        String jwt = jwtUtils.createJwt(user.getId().longValue(), username, role);
        LoginResponse response = new LoginResponse(
                jwt,
                "Bearer",
                jwtUtils.getExpireInSeconds(),
                username,
                role
        );

        return Result.ok(200, "登录成功", response);
    }

    @Override
    public Result register(String username, String password) {
        User existUser = query().eq("username", username).one();
        if (existUser != null) {
            return Result.fail("用户已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole("user");
        user.setStatus("active");
        user.setCreated_at(LocalDateTime.now());
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + username);

        save(user);

        String role = "user";
        String jwt = jwtUtils.createJwt(user.getId().longValue(), username, role);
        LoginResponse response = new LoginResponse(
                jwt,
                "Bearer",
                jwtUtils.getExpireInSeconds(),
                username,
                role
        );

        return Result.ok(200, "注册成功", response);
    }

    @Override
    public Result getProfile(String username) {
        User user = query().eq("username", username).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setPassword(null);
        return Result.ok(user);
    }

    @Override
    public Result updateProfile(String oldUsername, String newUsername, String signature, String avatar) {
        User user = query().eq("username", oldUsername).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }

        boolean usernameChanged = false;
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(oldUsername)) {
            User existUser = query().eq("username", newUsername).one();
            if (existUser != null) {
                return Result.fail("用户名已存在");
            }
            user.setUsername(newUsername);
            usernameChanged = true;
        }

        if (signature != null) {
            user.setSignature(signature);
        }

        if (avatar != null) {
            user.setAvatar(avatar);
        }

        updateById(user);

        if (usernameChanged) {
            commentUserMapper.update(null,
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>update()
                            .set("username", newUsername)
                            .eq("username", oldUsername)
            );
        }

        user.setPassword(null);

        Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("user", user);

        if (usernameChanged) {
            String role = user.getRole() != null ? user.getRole() : "user";
            String newJwt = jwtUtils.createJwt(user.getId().longValue(), newUsername, role);
            responseData.put("token", newJwt);
            responseData.put("tokenType", "Bearer");
            responseData.put("expiresIn", jwtUtils.getExpireInSeconds());
            responseData.put("username", newUsername);
        }

        return Result.ok(200, "更新成功", responseData);
    }

    @Override
    public Result listAllUsers() {
        List<User> users = list();
        List<Map<String, Object>> data = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (User u : users) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("role", u.getRole());
            item.put("status", u.getStatus());
            item.put("createdAt", u.getCreated_at() != null ? u.getCreated_at().format(fmt) : null);
            item.put("avatar", u.getAvatar());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result muteUser(String targetUsername) {
        User user = query().eq("username", targetUsername).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if ("admin".equals(user.getRole()) || "super_admin".equals(user.getRole())) {
            return Result.fail("无法禁言管理员账号");
        }
        String newStatus = "muted".equals(user.getStatus()) ? "active" : "muted";
        user.setStatus(newStatus);
        updateById(user);
        return Result.ok(200, newStatus.equals("muted") ? "已禁言该用户" : "已解除禁言", newStatus);
    }

    @Override
    public Result deleteUser(String targetUsername) {
        User user = query().eq("username", targetUsername).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if ("admin".equals(user.getRole()) || "super_admin".equals(user.getRole())) {
            return Result.fail("无法删除管理员账号");
        }
        removeById(user.getId());
        return Result.ok(200, "已注销该用户账号", null);
    }

    @Override
    public Result deleteUserAccount(String username) {
        // 查找用户
        User user = query().eq("username", username).one();
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 检查是否为管理员
        if ("admin".equals(user.getRole()) || "super_admin".equals(user.getRole())) {
            return Result.fail("管理员账号不能自行注销，请联系超级管理员");
        }

        Long userId = Long.valueOf(user.getId());

        // 删除该用户发布的所有帖子
        List<Post> userPosts = postMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Post>query()
                        .eq("user_id", userId)
        );

        for (Post post : userPosts) {
            // 删除帖子关联的评论对象
            List<ReviewTarget> reviewTargets = reviewTargetMapper.selectList(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<ReviewTarget>query()
                            .eq("post_id", post.getId())
            );

            for (ReviewTarget target : reviewTargets) {
                // 删除评论对象关联的评论
                List<CommentUser> comments = commentUserMapper.selectList(
                        com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                                .eq("review_target_id", target.getId())
                );

                for (CommentUser comment : comments) {
                    // 删除评论内容
                    commentContentMapper.delete(
                            com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentContent>query()
                                    .eq("comment_id", comment.getId())
                    );
                }

                // 删除评论用户记录
                commentUserMapper.delete(
                        com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                                .eq("review_target_id", target.getId())
                );
            }

            // 删除评论对象
            reviewTargetMapper.delete(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<ReviewTarget>query()
                            .eq("post_id", post.getId())
            );
        }

        // 删除用户的帖子
        postMapper.delete(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<Post>query()
                        .eq("user_id", userId)
        );

        // 删除用户参与的评论（作为评论者）
        List<CommentUser> userComments = commentUserMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("username", username)
        );

        for (CommentUser comment : userComments) {
            // 删除评论内容
            commentContentMapper.delete(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentContent>query()
                            .eq("comment_id", comment.getId())
            );
        }

        // 删除评论用户记录
        commentUserMapper.delete(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("username", username)
        );

        // 最后删除用户本身
        removeById(user.getId());

        return Result.ok(200, "账号已成功注销", null);
    }
}
//1

