package com.meategg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meategg.DTO.LoginRequest;
import com.meategg.DTO.LoginResponse;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.Utils.JwtUtils;
import com.meategg.service.OssService;
import com.meategg.service.userService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private userService userService;

    @MockBean
    private OssService ossService;

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

    private RequestPostProcessor withAuth(String username) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + username);
            return request;
        };
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse("token", "Bearer", 3600L, "testuser", "user");
        when(userService.login("testuser", "password123")).thenReturn(Result.ok(loginResponse));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void login_shouldFail_whenUsernameEmpty() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password123");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void login_shouldFail_whenPasswordEmpty() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void login_shouldFail_whenCredentialsWrong() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userService.login("testuser", "wrongpassword")).thenReturn(Result.fail(401, "用户名或密码错误"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void register_shouldCreateUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse("token", "Bearer", 3600L, "newuser", "user");
        when(userService.register("newuser", "password123")).thenReturn(Result.ok(loginResponse));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void register_shouldFail_whenPasswordTooShort() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("newuser");
        request.setPassword("12345");

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void register_shouldFail_whenUsernameExists() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("existing");
        request.setPassword("password123");

        when(userService.register("existing", "password123")).thenReturn(Result.fail("用户已存在"));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void getProfile_shouldReturnUser_whenLoggedIn() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setRole("user");

        when(userService.getProfile("testuser")).thenReturn(Result.ok(user));

        mockMvc.perform(get("/user/profile").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void getProfile_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void updateProfile_shouldSucceed() throws Exception {
        Map<String, String> params = Map.of("username", "newname", "signature", "新签名");
        Map<String, Object> responseData = Map.of("user", Map.of("username", "newname"));

        when(userService.updateProfile("testuser", "newname", "新签名", null))
                .thenReturn(Result.ok(responseData));

        mockMvc.perform(post("/user/profile/update")
                        .with(withAuth("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateProfile_shouldFail_whenNotLoggedIn() throws Exception {
        Map<String, String> params = Map.of("username", "newname");

        mockMvc.perform(post("/user/profile/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void uploadAvatar_shouldReturnUrl() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "avatar.jpg", "image/jpeg", "test-image-content".getBytes());

        when(ossService.uploadAvatar(any(), eq("testuser"))).thenReturn("https://oss.com/avatar.jpg");

        mockMvc.perform(multipart("/user/profile/avatar")
                        .file(image)
                        .with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("https://oss.com/avatar.jpg"));
    }

    @Test
    void uploadAvatar_shouldFail_whenImageEmpty() throws Exception {
        MockMultipartFile emptyImage = new MockMultipartFile(
                "image", "empty.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/user/profile/avatar")
                        .file(emptyImage)
                        .with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void deleteAccount_shouldSucceed() throws Exception {
        when(userService.deleteUserAccount("testuser")).thenReturn(Result.ok(200, "账号已成功注销", null));

        mockMvc.perform(delete("/user/account").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteAccount_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(delete("/user/account"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }
}
