package com.meategg.controller;

import com.meategg.entity.Result;
import com.meategg.Utils.JwtUtils;
import com.meategg.service.postService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private postService postService;

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
        when(jwtUtils.getRole(anyString())).thenReturn("user");
    }

    private RequestPostProcessor withAuth(String username) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + username);
            return request;
        };
    }

    @Test
    void listPosts_shouldReturnPostList() throws Exception {
        List<Map<String, Object>> mockList = Arrays.asList(
                Map.of("id", 1, "title", "帖子1", "viewCount", 10),
                Map.of("id", 2, "title", "帖子2", "viewCount", 20)
        );
        when(postService.listPosts()).thenReturn(Result.ok(mockList));

        mockMvc.perform(get("/post/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("帖子1"));
    }

    @Test
    void listPosts_shouldReturnEmptyList_whenNoPosts() throws Exception {
        when(postService.listPosts()).thenReturn(Result.ok(new ArrayList<>()));

        mockMvc.perform(get("/post/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void getPostById_shouldReturnPost() throws Exception {
        Map<String, Object> post = Map.of("id", 1, "title", "测试帖子", "viewCount", 42);
        when(postService.getPostById(1L)).thenReturn(Result.ok(post));

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试帖子"));
    }

    @Test
    void getPostById_shouldReturn404_whenNotFound() throws Exception {
        when(postService.getPostById(999L)).thenReturn(Result.fail("帖子不存在"));

        mockMvc.perform(get("/post/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void createReviewTarget_shouldSucceed() throws Exception {
        Map<String, String> request = Map.of("targetName", "食堂窗口1");
        Map<String, Object> response = Map.of("id", 1, "targetName", "食堂窗口1", "postId", 1);

        when(postService.createReviewTarget(eq(1L), eq("食堂窗口1"), eq("testuser")))
                .thenReturn(Result.ok(response));

        mockMvc.perform(post("/post/1/review-target")
                        .with(withAuth("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetName\":\"食堂窗口1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.targetName").value("食堂窗口1"));
    }

    @Test
    void getReviewTargets_shouldReturnList() throws Exception {
        List<Map<String, Object>> targets = Arrays.asList(
                Map.of("id", 1, "targetName", "窗口1"),
                Map.of("id", 2, "targetName", "窗口2")
        );
        when(postService.getReviewTargetsByPostId(1L)).thenReturn(Result.ok(targets));

        mockMvc.perform(get("/post/1/review-targets").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getReviewTargetById_shouldReturnDetail() throws Exception {
        Map<String, Object> detail = new HashMap<>();
        detail.put("id", 1);
        detail.put("targetName", "食堂窗口1");
        detail.put("comments", Arrays.asList(Map.of("content", "好吃")));

        when(postService.getReviewTargetById(1L)).thenReturn(Result.ok(detail));

        mockMvc.perform(get("/post/review-target/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.targetName").value("食堂窗口1"));
    }

    @Test
    void addComment_shouldSucceed() throws Exception {
        Map<String, Object> request = Map.of("content", "很好吃", "score", 5);
        Map<String, Object> response = Map.of("id", 1, "content", "很好吃", "score", 5);

        when(postService.addComment(eq(1L), eq("testuser"), eq("很好吃"), eq(5)))
                .thenReturn(Result.ok(response));

        mockMvc.perform(post("/post/review-target/1/comment")
                        .with(withAuth("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"很好吃\",\"score\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("很好吃"));
    }

    @Test
    void getComments_shouldReturnList() throws Exception {
        List<Map<String, Object>> comments = Arrays.asList(
                Map.of("id", 1, "content", "好吃", "score", 5),
                Map.of("id", 2, "content", "一般", "score", 3)
        );
        when(postService.getCommentsByReviewTargetId(1L)).thenReturn(Result.ok(comments));

        mockMvc.perform(get("/post/review-target/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getUserPosts_shouldReturnUserPosts() throws Exception {
        List<Map<String, Object>> posts = Arrays.asList(
                Map.of("id", 1, "title", "我的帖子")
        );
        when(postService.listUserPosts("testuser")).thenReturn(Result.ok(posts));

        mockMvc.perform(get("/post/user/posts").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("我的帖子"));
    }

    @Test
    void getUserPosts_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/post/user/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void getUserComments_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/post/user/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void search_shouldReturnResults() throws Exception {
        Map<String, Object> searchResult = new HashMap<>();
        searchResult.put("posts", Arrays.asList(Map.of("id", 1, "title", "食堂测评")));
        searchResult.put("reviewTargets", Arrays.asList(Map.of("id", 1, "targetName", "食堂窗口")));

        when(postService.search("食堂")).thenReturn(Result.ok(searchResult));

        mockMvc.perform(get("/post/search").param("keyword", "食堂"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.posts").isArray());
    }

    @Test
    void listPostsByTag_shouldReturnFilteredPosts() throws Exception {
        List<Map<String, Object>> posts = Arrays.asList(
                Map.of("id", 1, "title", "食堂帖子", "tag", "食堂")
        );
        when(postService.listPostsByTag("食堂")).thenReturn(Result.ok(posts));

        mockMvc.perform(get("/post/list/by-tag").param("tag", "食堂"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listPostsRanking_shouldReturnRankedPosts() throws Exception {
        List<Map<String, Object>> posts = Arrays.asList(
                Map.of("id", 1, "title", "热门帖子", "viewCount", 1000)
        );
        when(postService.listPostsRanking("hot")).thenReturn(Result.ok(posts));

        mockMvc.perform(get("/post/list/ranking").param("sort", "hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deletePost_shouldSucceed() throws Exception {
        when(postService.deletePost(1L, "testuser")).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/post/1").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deletePost_shouldFail_whenNotLoggedIn() throws Exception {
        mockMvc.perform(delete("/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void deleteComment_shouldSucceed() throws Exception {
        when(postService.deleteComment(1L, "testuser")).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/post/comment/1").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteReviewTarget_shouldSucceed() throws Exception {
        when(postService.deleteReviewTarget(1L, "testuser")).thenReturn(Result.ok(200, "删除成功", null));

        mockMvc.perform(delete("/post/review-target/1").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getPostStats_shouldReturnStats() throws Exception {
        Map<String, Object> stats = Map.of("viewCount", 100, "commentCount", 10);
        when(postService.getPostStats(1L)).thenReturn(Result.ok(stats));

        mockMvc.perform(get("/post/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.viewCount").value(100));
    }

    @Test
    void getReviewTargetStats_shouldReturnStats() throws Exception {
        Map<String, Object> stats = Map.of("commentCount", 5, "avgScore", 4.5);
        when(postService.getReviewTargetStats(1L)).thenReturn(Result.ok(stats));

        mockMvc.perform(get("/post/review-target/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.avgScore").value(4.5));
    }

    @Test
    void createPost_shouldSucceed() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test-image".getBytes());

        Map<String, Object> response = Map.of("id", 1, "title", "新帖子");
        when(postService.createPost(any(), eq("testuser"), any())).thenReturn(Result.ok(response));

        mockMvc.perform(multipart("/post")
                        .file(image)
                        .param("title", "新帖子")
                        .param("content", "内容")
                        .param("tag", "食堂")
                        .with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createPost_shouldFailWithInvalidImageType() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.txt", "text/plain", "not-an-image".getBytes());

        mockMvc.perform(multipart("/post")
                        .file(image)
                        .param("title", "新帖子")
                        .param("content", "内容")
                        .param("tag", "食堂")
                        .with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void getPersonalizedRecommendation_shouldReturnPosts() throws Exception {
        List<Map<String, Object>> recommendations = Arrays.asList(
                Map.of("id", 1, "title", "推荐帖子")
        );
        when(postService.getPersonalizedRecommendation(eq("testuser"), any()))
                .thenReturn(Result.ok(recommendations));

        mockMvc.perform(get("/post/list/personalized").with(withAuth("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
