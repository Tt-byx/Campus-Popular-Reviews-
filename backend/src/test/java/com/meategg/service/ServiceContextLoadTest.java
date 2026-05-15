package com.meategg.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ServiceContextLoadTest {

    @Autowired(required = false)
    private userService userService;

    @Autowired(required = false)
    private postService postService;

    @Test
    void userServiceBean_shouldBeLoadable() {
        assertNotNull(userService, "userService bean should be loadable in Spring context");
    }

    @Test
    void postServiceBean_shouldBeLoadable() {
        assertNotNull(postService, "postService bean should be loadable in Spring context");
    }
}
