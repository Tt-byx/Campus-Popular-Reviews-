package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.DTO.LoginResponse;
import com.meategg.Utils.JwtUtils;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.UserMapper;
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

    @Override
    public Result login(String username, String password) {
        User user = query().eq("username", username).one();
        if(user == null){
            return Result.fail(401, "用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        if ("muted".equals(user.getStatus())) {
            return Result.fail(403, "该账号已被禁言，无法登录");
        }
        
        String role = user.getRole() != null ? user.getRole() : "user";
        String jwt = jwtUtils.createJwt(username, role);
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
        
        save(user);
        return Result.ok("注册成功");
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
        user.setPassword(null);

        Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("user", user);

        if (usernameChanged) {
            String role = user.getRole() != null ? user.getRole() : "user";
            String newJwt = jwtUtils.createJwt(newUsername, role);
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
        for (User u : users) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("role", u.getRole());
            item.put("status", u.getStatus());
            item.put("createdAt", u.getCreated_at());
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
        if ("admin".equals(user.getRole())) {
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
        if ("admin".equals(user.getRole())) {
            return Result.fail("无法删除管理员账号");
        }
        removeById(user.getId());
        return Result.ok(200, "已注销该用户账号", null);
    }
}
//1
