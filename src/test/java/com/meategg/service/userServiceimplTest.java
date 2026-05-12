package com.meategg.service;

import com.meategg.DTO.LoginResponse;
import com.meategg.Utils.JwtUtils;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.CommentContentMapper;
import com.meategg.mapper.CommentUserMapper;
import com.meategg.mapper.PostMapper;
import com.meategg.mapper.ReviewTargetMapper;
import com.meategg.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class userServiceimplTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private CommentUserMapper commentUserMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private ReviewTargetMapper reviewTargetMapper;
    @Mock
    private CommentContentMapper commentContentMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private userServiceimpl userService;

    private User testUser;
    private final String testUsername = "testuser";
    private final String testPassword = "password123";
    private final String hashedPassword = BCrypt.hashpw(testPassword, BCrypt.gensalt());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername(testUsername);
        testUser.setPassword(hashedPassword);
        testUser.setRole("user");
        testUser.setStatus("active");
        testUser.setCreated_at(LocalDateTime.now());
        testUser.setAvatar("https://example.com/avatar.png");
    }

    @Test
    void login_shouldSucceedWithValidCredentials() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.createJwt(testUsername, "user")).thenReturn("mock-jwt-token");
        when(jwtUtils.getExpireInSeconds()).thenReturn(3600L);

        Result<LoginResponse> result = userService.login(testUsername, testPassword);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("mock-jwt-token", result.getData().getToken());
        assertEquals("Bearer", result.getData().getTokenType());
        assertEquals(testUsername, result.getData().getUsername());
        assertEquals("user", result.getData().getRole());
    }

    @Test
    void login_shouldFailWithWrongPassword() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<LoginResponse> result = userService.login(testUsername, "wrongpassword");

        assertEquals(401, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void register_shouldCreateNewUser() {
        when(jwtUtils.createJwt(eq("newuser"), anyString())).thenReturn("mock-jwt-token");
        when(jwtUtils.getExpireInSeconds()).thenReturn(3600L);
        when(userMapper.insert(any())).thenReturn(1);

        Result<LoginResponse> result = userService.register("newuser", "newpassword");

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("newuser", result.getData().getUsername());
    }

    @Test
    void register_shouldFailWithExistingUsername() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<LoginResponse> result = userService.register(testUsername, "anypassword");

        assertTrue(result.getCode() != 200);
    }

    @Test
    void getProfile_shouldReturnUserData() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<User> result = userService.getProfile(testUsername);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(testUsername, result.getData().getUsername());
    }

    @Test
    void getProfile_shouldHidePassword() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<User> result = userService.getProfile(testUsername);

        assertNotNull(result.getData());
        assertNull(result.getData().getPassword());
    }

    @Test
    void getProfile_shouldFailWithNonExistentUser() {
        Result<User> result = userService.getProfile("nonexistent");

        assertNotNull(result);
        assertTrue(result.getCode() != 200);
    }

    @Test
    void updateProfile_shouldUpdateSignatureOnly() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<Map<String, Object>> result = userService.updateProfile(testUsername, null, "新签名", null);

        assertEquals(200, result.getCode());
    }

    @Test
    void updateProfile_shouldFailForNonExistentUser() {
        Result<Map<String, Object>> result = userService.updateProfile("ghost", "new", "sig", null);
        assertTrue(result.getCode() != 200);
    }

    @Test
    void listAllUsers_shouldReturnUserList() {
        when(userMapper.selectList(any())).thenReturn(Arrays.asList(testUser));

        Result<List<Map<String, Object>>> result = userService.listAllUsers();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void listAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userMapper.selectList(any())).thenReturn(Collections.emptyList());

        Result<List<Map<String, Object>>> result = userService.listAllUsers();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().size());
    }

    @Test
    void muteUser_shouldToggleMuteStatus() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<Object> result = userService.muteUser(testUsername);

        assertEquals(200, result.getCode());
    }

    @Test
    void muteUser_shouldFailForAdminUser() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole("admin");
        when(userMapper.selectOne(any())).thenReturn(adminUser);

        Result<Object> result = userService.muteUser("admin");

        assertTrue(result.getCode() != 200);
    }

    @Test
    void muteUser_shouldFailForSuperAdmin() {
        User superAdmin = new User();
        superAdmin.setUsername("superadmin");
        superAdmin.setRole("super_admin");
        when(userMapper.selectOne(any())).thenReturn(superAdmin);

        Result<Object> result = userService.muteUser("superadmin");

        assertTrue(result.getCode() != 200);
    }

    @Test
    void muteUser_shouldFailForNonExistentUser() {
        Result<Object> result = userService.muteUser("ghost");
        assertTrue(result.getCode() != 200);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        Result<Object> result = userService.deleteUser(testUsername);

        assertEquals(200, result.getCode());
    }

    @Test
    void deleteUser_shouldFailForNonExistentUser() {
        Result<Object> result = userService.deleteUser("ghost");
        assertTrue(result.getCode() != 200);
    }

    @Test
    void deleteUser_shouldFailForAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole("admin");
        when(userMapper.selectOne(any())).thenReturn(admin);

        Result<Object> result = userService.deleteUser("admin");
        assertTrue(result.getCode() != 200);
    }

    @Test
    void deleteUserAccount_shouldFailForNonExistentUser() {
        Result<Object> result = userService.deleteUserAccount("ghost");
        assertTrue(result.getCode() != 200);
    }
}
