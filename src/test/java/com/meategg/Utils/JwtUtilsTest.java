package com.meategg.Utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String testSecret = "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890";
    private final long testExpire = 3600L;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "key", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "expire", testExpire);
    }

    @Test
    @DisplayName("创建JWT - 用户角色")
    void testCreateJwt_withUserRole() {
        String token = jwtUtils.createJwt("testuser", "user");
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("创建JWT - 管理员角色")
    void testCreateJwt_withAdminRole() {
        String token = jwtUtils.createJwt("admin", "admin");
        assertNotNull(token);
    }

    @Test
    @DisplayName("创建JWT - 超级管理员角色")
    void testCreateJwt_withSuperAdminRole() {
        String token = jwtUtils.createJwt("superadmin", "super_admin");
        assertNotNull(token);
    }

    @Test
    @DisplayName("创建JWT - 角色为null时默认user")
    void testCreateJwt_withNullRole() {
        String token = jwtUtils.createJwt("testuser", null);
        assertNotNull(token);
        String role = jwtUtils.getRole(token);
        assertEquals("user", role);
    }

    @Test
    @DisplayName("解析JWT获取用户名")
    void testGetUsername() {
        String token = jwtUtils.createJwt("testuser", "user");
        String username = jwtUtils.getUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("解析JWT获取角色")
    void testGetRole() {
        String token = jwtUtils.createJwt("admin", "admin");
        String role = jwtUtils.getRole(token);
        assertEquals("admin", role);
    }

    @Test
    @DisplayName("解析JWT获取Claims主体")
    void testParseJwt() {
        String token = jwtUtils.createJwt("testuser", "user");
        Claims claims = jwtUtils.parseJwt(token);
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals("user", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("JWT未过期")
    void testIsExpire_notExpired() {
        String token = jwtUtils.createJwt("testuser", "user");
        assertFalse(jwtUtils.isExpire(token));
    }

    @Test
    @DisplayName("获取过期时间（秒）")
    void testGetExpireInSeconds() {
        assertEquals(testExpire, jwtUtils.getExpireInSeconds());
    }

    @Test
    @DisplayName("不同用户生成不同令牌")
    void testDifferentUsersDifferentTokens() {
        String token1 = jwtUtils.createJwt("user1", "user");
        String token2 = jwtUtils.createJwt("user2", "user");
        assertNotEquals(token1, token2);
        assertEquals("user1", jwtUtils.getUsername(token1));
        assertEquals("user2", jwtUtils.getUsername(token2));
    }

    @Test
    @DisplayName("相同用户两次生成的令牌不同")
    void testSameUserTokensDiffer() throws Exception {
        String token1 = jwtUtils.createJwt("testuser", "user");
        Thread.sleep(1000);
        String token2 = jwtUtils.createJwt("testuser", "user");
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("解析JWT中的角色信息准确性")
    void testRoleAccuracy() {
        String userToken = jwtUtils.createJwt("u1", "user");
        String adminToken = jwtUtils.createJwt("a1", "admin");
        String superAdminToken = jwtUtils.createJwt("s1", "super_admin");

        assertEquals("user", jwtUtils.getRole(userToken));
        assertEquals("admin", jwtUtils.getRole(adminToken));
        assertEquals("super_admin", jwtUtils.getRole(superAdminToken));
    }

    @Test
    @DisplayName("解析JWT中的用户名准确性")
    void testUsernameAccuracy() {
        String token = jwtUtils.createJwt("specific-user-name", "user");
        assertEquals("specific-user-name", jwtUtils.getUsername(token));
    }

    @Test
    @DisplayName("Claims中包含签发时间")
    void testClaimsContainsIssuedAt() {
        Date before = new Date(System.currentTimeMillis() - 1000);
        String token = jwtUtils.createJwt("testuser", "user");
        Date after = new Date(System.currentTimeMillis() + 1000);
        Claims claims = jwtUtils.parseJwt(token);
        Date issuedAt = claims.getIssuedAt();
        assertTrue(issuedAt.after(before) || issuedAt.equals(before));
        assertTrue(issuedAt.before(after) || issuedAt.equals(after));
    }

    @Test
    @DisplayName("验证JWT的Subject字段")
    void testSubjectField() {
        String token = jwtUtils.createJwt("myuser", "admin");
        Claims claims = jwtUtils.parseJwt(token);
        assertEquals("myuser", claims.getSubject());
    }

    @Test
    @DisplayName("验证JWT的role claim字段")
    void testRoleClaimField() {
        String token = jwtUtils.createJwt("myuser", "admin");
        Claims claims = jwtUtils.parseJwt(token);
        assertEquals("admin", claims.get("role"));
    }

    @Test
    @DisplayName("空用户名创建JWT")
    void testCreateJwt_withEmptyUsername() {
        String token = jwtUtils.createJwt("", "user");
        assertNotNull(token);
        assertEquals("", jwtUtils.getUsername(token));
    }

    @Test
    @DisplayName("超长用户名创建JWT")
    void testCreateJwt_withLongUsername() {
        String longName = "a".repeat(100);
        String token = jwtUtils.createJwt(longName, "user");
        assertNotNull(token);
        assertEquals(longName, jwtUtils.getUsername(token));
    }
}
