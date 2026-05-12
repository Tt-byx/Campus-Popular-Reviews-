package com.meategg.controller;

import com.meategg.entity.BannedWord;
import com.meategg.entity.Result;
import com.meategg.mapper.BannedWordMapper;
import com.meategg.mapper.UserMapper;
import com.meategg.Utils.JwtUtils;
import com.meategg.service.postService;
import com.meategg.service.userService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private userService userService;

    @MockBean
    private postService postService;

    @MockBean
    private BannedWordMapper bannedWordMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        when(jwtUtils.isExpire(anyString())).thenReturn(false);
        when(jwtUtils.getUsername(anyString())).thenAnswer(i -> {
            String token = i.getArgument(0);
            int idx = token.indexOf(":");
            return idx > 0 ? token.substring(0, idx) : token;
        });
        when(jwtUtils.getRole(anyString())).thenAnswer(i -> {
            String token = i.getArgument(0);
            int idx = token.indexOf(":");
            return idx > 0 ? token.substring(idx + 1) : "user";
        });
    }

    private RequestPostProcessor asAdmin() {
        return request -> {
            request.addHeader("Authorization", "Bearer admin:admin");
            return request;
        };
    }

    private RequestPostProcessor asSuperAdmin() {
        return request -> {
            request.addHeader("Authorization", "Bearer superadmin:super_admin");
            return request;
        };
    }

    private RequestPostProcessor asUser() {
        return request -> {
            request.addHeader("Authorization", "Bearer normaluser:user");
            return request;
        };
    }

    @Test
    void listUsers_shouldReturnUsers_whenAdmin() throws Exception {
        List<Map<String, Object>> users = Arrays.asList(
                Map.of("id", 1, "username", "user1", "role", "user"),
                Map.of("id", 2, "username", "user2", "role", "admin")
        );
        when(userService.listAllUsers()).thenReturn(Result.ok(users));

        mockMvc.perform(get("/admin/users").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void listUsers_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/users").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listUsers_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/admin/users").header("Accept", "application/json"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void muteUser_shouldSucceed_whenAdmin() throws Exception {
        when(userService.muteUser("targetuser")).thenReturn(Result.ok(200, "已禁言该用户", "muted"));

        mockMvc.perform(post("/admin/user/targetuser/mute").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void muteUser_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(post("/admin/user/targetuser/mute").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void deleteUser_shouldSucceed_whenAdmin() throws Exception {
        when(userService.deleteUser("targetuser")).thenReturn(Result.ok(200, "已注销该用户账号", null));

        mockMvc.perform(delete("/admin/user/targetuser").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteUser_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(delete("/admin/user/targetuser").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void promoteToAdmin_shouldSucceed_whenSuperAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("normaluser");
        user.setRole("user");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/normaluser/promote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void promoteToAdmin_shouldFail_whenNotSuperAdmin() throws Exception {
        mockMvc.perform(post("/admin/user/normaluser/promote").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void promoteToAdmin_shouldFail_whenUserNotExists() throws Exception {
        when(userMapper.selectOne(any())).thenReturn(null);

        mockMvc.perform(post("/admin/user/ghost/promote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void demoteFromAdmin_shouldSucceed_whenSuperAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("adminuser");
        user.setRole("admin");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/adminuser/demote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void demoteFromAdmin_shouldFail_whenNotSuperAdmin() throws Exception {
        mockMvc.perform(post("/admin/user/adminuser/demote").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void getRole_shouldReturnRole_whenAdmin() throws Exception {
        mockMvc.perform(get("/admin/role").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getRole_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/role").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listPosts_shouldReturnPosts_whenAdmin() throws Exception {
        List<Map<String, Object>> posts = Arrays.asList(
                Map.of("id", 1, "title", "帖子1"),
                Map.of("id", 2, "title", "帖子2")
        );
        when(postService.listAllPosts()).thenReturn(Result.ok(posts));

        mockMvc.perform(get("/admin/posts").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void adminDeletePost_shouldSucceed_whenAdmin() throws Exception {
        when(postService.adminDeletePost(1L)).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/admin/post/1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void adminDeletePost_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(delete("/admin/post/1").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void listReviewTargets_shouldReturnTargets_whenAdmin() throws Exception {
        List<Map<String, Object>> targets = Arrays.asList(
                Map.of("id", 1, "targetName", "窗口1"),
                Map.of("id", 2, "targetName", "窗口2")
        );
        when(postService.listAllReviewTargets()).thenReturn(Result.ok(targets));

        mockMvc.perform(get("/admin/review-targets").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void adminDeleteReviewTarget_shouldSucceed_whenAdmin() throws Exception {
        when(postService.adminDeleteReviewTarget(1L)).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/admin/review-target/1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listComments_shouldReturnComments_whenAdmin() throws Exception {
        List<Map<String, Object>> comments = Arrays.asList(
                Map.of("id", 1, "content", "好吃", "score", 5)
        );
        when(postService.listAllComments()).thenReturn(Result.ok(comments));

        mockMvc.perform(get("/admin/comments").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].content").value("好吃"));
    }

    @Test
    void adminDeleteComment_shouldSucceed_whenAdmin() throws Exception {
        when(postService.adminDeleteComment(1L)).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/admin/comment/1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listBannedWords_shouldReturnWords_whenAdmin() throws Exception {
        BannedWord word1 = new BannedWord();
        word1.setId(1);
        word1.setWord("敏感词1");
        word1.setCreatedAt(LocalDateTime.now());

        BannedWord word2 = new BannedWord();
        word2.setId(2);
        word2.setWord("敏感词2");
        word2.setCreatedAt(LocalDateTime.now());

        when(bannedWordMapper.selectList(any())).thenReturn(Arrays.asList(word1, word2));

        mockMvc.perform(get("/admin/banned-words").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void listBannedWords_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/banned-words").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void addBannedWord_shouldSucceed_whenAdmin() throws Exception {
        when(bannedWordMapper.selectCount(any())).thenReturn(0);

        mockMvc.perform(post("/admin/banned-words")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"新违禁词\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void addBannedWord_shouldFail_whenWordEmpty() throws Exception {
        mockMvc.perform(post("/admin/banned-words")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void addBannedWord_shouldFail_whenWordAlreadyExists() throws Exception {
        when(bannedWordMapper.selectCount(any())).thenReturn(1);

        mockMvc.perform(post("/admin/banned-words")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"敏感词\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void addBannedWord_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(post("/admin/banned-words")
                        .with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"违禁词\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void deleteBannedWord_shouldSucceed_whenAdmin() throws Exception {
        mockMvc.perform(delete("/admin/banned-words/1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteBannedWord_shouldFail_whenNotAdmin() throws Exception {
        mockMvc.perform(delete("/admin/banned-words/1").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void promoteToAdmin_shouldFail_whenAlreadyAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("existingadmin");
        user.setRole("admin");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/existingadmin/promote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void promoteToAdmin_shouldFail_whenSuperAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("super");
        user.setRole("super_admin");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/super/promote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void demoteFromAdmin_shouldFail_whenNotAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("normaluser");
        user.setRole("user");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/normaluser/demote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void demoteFromAdmin_shouldFail_whenSuperAdmin() throws Exception {
        com.meategg.entity.User user = new com.meategg.entity.User();
        user.setUsername("super");
        user.setRole("super_admin");
        when(userMapper.selectOne(any())).thenReturn(user);

        mockMvc.perform(post("/admin/user/super/demote").with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void deleteUser_withEmptyUsername_shouldFail() throws Exception {
        mockMvc.perform(delete("/admin/user/   ").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}
