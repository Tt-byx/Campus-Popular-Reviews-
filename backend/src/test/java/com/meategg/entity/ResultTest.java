package com.meategg.entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void ok_shouldCreateSuccessResultWithDefaultMessage() {
        Result<String> result = Result.ok("test data");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("test data", result.getData());
    }

    @Test
    void ok_shouldCreateSuccessResultWithoutData() {
        Result<String> result = Result.ok();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void ok_shouldCreateSuccessResultWithCustomCodeAndMessage() {
        Result<Integer> result = Result.ok(201, "自定义成功", 100);
        assertEquals(201, result.getCode());
        assertEquals("自定义成功", result.getMessage());
        assertEquals(100, result.getData());
    }

    @Test
    void fail_shouldCreateErrorResultWithDefaultCode() {
        Result<String> result = Result.fail("出错了");
        assertEquals(400, result.getCode());
        assertEquals("出错了", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void fail_shouldCreateErrorResultWithCustomCode() {
        Result<String> result = Result.fail(401, "未登录");
        assertEquals(401, result.getCode());
        assertEquals("未登录", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void ok_shouldSupportGenericStringType() {
        Result<String> result = Result.ok("hello");
        assertInstanceOf(String.class, result.getData());
        assertEquals("hello", result.getData());
    }

    @Test
    void ok_shouldSupportGenericIntegerType() {
        Result<Integer> result = Result.ok(42);
        assertInstanceOf(Integer.class, result.getData());
        assertEquals(42, result.getData());
    }

    @Test
    void ok_shouldSupportGenericListType() {
        List<String> list = Arrays.asList("a", "b", "c");
        Result<List<String>> result = Result.ok(list);
        assertInstanceOf(List.class, result.getData());
        assertEquals(3, result.getData().size());
        assertEquals("a", result.getData().get(0));
    }

    @Test
    void ok_shouldSupportGenericObjectType() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        Result<User> result = Result.ok(user);
        assertInstanceOf(User.class, result.getData());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    void constructor_shouldSetAllFields() {
        Result<String> result = new Result<>(200, "测试", "data");
        assertEquals(200, result.getCode());
        assertEquals("测试", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    void data_canBeNull() {
        Result<String> result = Result.ok();
        assertNull(result.getData());
    }

    @Test
    void message_canBeCustomizedInFail() {
        Result<String> result = Result.fail("邮箱已被注册");
        assertEquals("邮箱已被注册", result.getMessage());
    }

    @Test
    void code_shouldBe400ForDefaultFail() {
        Result<String> result = Result.fail("错误");
        assertEquals(400, (int) result.getCode());
    }

    @Test
    void ok_shouldSupportNullData() {
        Result<String> result = Result.ok(null);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void multipleInstances_shouldNotShareState() {
        Result<String> r1 = Result.ok("first");
        Result<String> r2 = Result.ok("second");
        assertEquals("first", r1.getData());
        assertEquals("second", r2.getData());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        Result<String> r1 = new Result<>(200, "OK", "data");
        Result<String> r2 = new Result<>(200, "OK", "data");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void fail_with401_shouldRepresentUnauthorized() {
        Result<String> result = Result.fail(401, "未登录，请先登录");
        assertEquals(401, (int) result.getCode());
        assertNull(result.getData());
    }

    @Test
    void fail_with403_shouldRepresentForbidden() {
        Result<String> result = Result.fail(403, "无权限访问");
        assertEquals(403, (int) result.getCode());
    }

    @Test
    void ok_chainCalling_shouldWork() {
        Result<List<Integer>> result = Result.ok(Arrays.asList(1, 2, 3));
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(3, result.getData().size());
    }

    @Test
    void genericType_shouldBePreservedAtRuntime() {
        Result<String> stringResult = Result.ok("string");
        Result<Integer> intResult = Result.ok(123);
        assertTrue(stringResult.getData() instanceof String);
        assertTrue(intResult.getData() instanceof Integer);
    }
}
