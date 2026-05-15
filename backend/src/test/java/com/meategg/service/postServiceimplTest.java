package com.meategg.service;

import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.entity.ReviewTarget;
import com.meategg.entity.User;
import com.meategg.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class postServiceimplTest {

    @Mock
    private PostMapper postMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ReviewTargetMapper reviewTargetMapper;
    @Mock
    private CommentUserMapper commentUserMapper;
    @Mock
    private CommentContentMapper commentContentMapper;
    @Mock
    private OssService ossService;
    @Mock
    private BannedWordMapper bannedWordMapper;

    @InjectMocks
    private postServiceimpl postService;

    private Post testPost;
    private User testUser;
    private ReviewTarget testTarget;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "baseMapper", postMapper);

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setRole("user");
        testUser.setAvatar("https://example.com/avatar.png");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("测试帖子");
        testPost.setContent("这是测试内容");
        testPost.setTag("食堂");
        testPost.setUserId(1L);
        testPost.setViewCount(10);
        testPost.setCreatedAt(LocalDateTime.now());

        testTarget = new ReviewTarget();
        testTarget.setId(1L);
        testTarget.setPostId(1L);
        testTarget.setTargetName("食堂窗口1");
        testTarget.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getPostById_shouldReturnPost() {
        when(postMapper.selectById(1L)).thenReturn(testPost);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        Result<?> result = postService.getPostById(1L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void getPostById_shouldIncrementViewCount() {
        when(postMapper.selectById(1L)).thenReturn(testPost);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        postService.getPostById(1L);

        verify(postMapper).updateById(argThat(post ->
                post.getViewCount() == 11
        ));
    }

    @Test
    void getPostById_shouldFailForNullId() {
        Result<?> result = postService.getPostById(null);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void getPostById_shouldFailForNonExistentPost() {
        when(postMapper.selectById(999L)).thenReturn(null);

        Result<?> result = postService.getPostById(999L);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void getReviewTargetById_shouldReturnTarget() {
        when(reviewTargetMapper.selectById(1L)).thenReturn(testTarget);
        when(postMapper.selectById(1L)).thenReturn(testPost);
        when(commentUserMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.getReviewTargetById(1L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void getReviewTargetById_shouldFailForNonExistentTarget() {
        when(reviewTargetMapper.selectById(999L)).thenReturn(null);

        Result<?> result = postService.getReviewTargetById(999L);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void getCommentsByReviewTargetId_shouldReturnComments() {
        List<com.meategg.entity.CommentUser> mockComments = new ArrayList<>();
        com.meategg.entity.CommentUser comment = new com.meategg.entity.CommentUser();
        comment.setId(1L);
        comment.setReviewTargetId(1L);
        comment.setUsername("commenter");
        comment.setCreatedAt(LocalDateTime.now());
        mockComments.add(comment);

        when(reviewTargetMapper.selectById(1L)).thenReturn(testTarget);
        when(commentUserMapper.selectList(any())).thenReturn(mockComments);

        Result<?> result = postService.getCommentsByReviewTargetId(1L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void adminDeletePost_shouldSucceed() {
        when(postMapper.selectById(1L)).thenReturn(testPost);
        when(reviewTargetMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.adminDeletePost(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    void adminDeletePost_shouldFailForNonExistentPost() {
        when(postMapper.selectById(999L)).thenReturn(null);

        Result<?> result = postService.adminDeletePost(999L);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void adminDeleteComment_shouldSucceed() {
        com.meategg.entity.CommentUser comment = new com.meategg.entity.CommentUser();
        comment.setId(1L);
        comment.setReviewTargetId(1L);
        comment.setUsername("someone");

        when(commentUserMapper.selectById(1L)).thenReturn(comment);

        Result<?> result = postService.adminDeleteComment(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    void adminDeleteComment_shouldFailForNonExistentComment() {
        Result<?> result = postService.adminDeleteComment(999L);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void adminDeleteReviewTarget_shouldSucceed() {
        when(reviewTargetMapper.selectById(1L)).thenReturn(testTarget);
        when(commentUserMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.adminDeleteReviewTarget(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    void adminDeleteReviewTarget_shouldFailForNonExistentTarget() {
        Result<?> result = postService.adminDeleteReviewTarget(999L);

        assertTrue(result.getCode() != 200);
    }

    @Test
    void listPosts_shouldReturnPosts() {
        when(postMapper.selectList(any())).thenReturn(Arrays.asList(testPost));
        when(userMapper.selectBatchIds(any())).thenReturn(Arrays.asList(testUser));

        Result<?> result = postService.listPosts();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void listPosts_shouldReturnEmptyList_whenNoPosts() {
        when(postMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.listPosts();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void listAllPosts_shouldReturnPosts() {
        when(postMapper.selectList(any())).thenReturn(Arrays.asList(testPost));
        when(userMapper.selectById(1L)).thenReturn(testUser);

        Result<?> result = postService.listAllPosts();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void listAllReviewTargets_shouldReturnTargets() {
        when(reviewTargetMapper.selectList(any())).thenReturn(Arrays.asList(testTarget));
        when(postMapper.selectBatchIds(any())).thenReturn(Arrays.asList(testPost));
        when(userMapper.selectBatchIds(any())).thenReturn(Arrays.asList(testUser));

        Result<?> result = postService.listAllReviewTargets();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void getPostStats_shouldReturnStats() {
        when(reviewTargetMapper.selectList(any())).thenReturn(Arrays.asList(testTarget));
        when(commentContentMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.getPostStats(1L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void getPostStats_shouldReturnEmpty_whenNoTargets() {
        when(reviewTargetMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.getPostStats(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    void getReviewTargetStats_shouldReturnStats() {
        when(commentContentMapper.selectList(any())).thenReturn(new ArrayList<>());

        Result<?> result = postService.getReviewTargetStats(1L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void getReviewTargetStats_shouldReturnEmpty_whenNoContents() {
        when(commentContentMapper.selectList(any())).thenReturn(null);

        Result<?> result = postService.getReviewTargetStats(1L);

        assertEquals(200, result.getCode());
    }

    @Test
    void search_shouldReturnResults() {
        when(postMapper.searchByTitle(anyString())).thenReturn(Arrays.asList(testPost));
        when(reviewTargetMapper.searchByTargetName(anyString())).thenReturn(new ArrayList<>());
        when(userMapper.selectBatchIds(any())).thenReturn(Arrays.asList(testUser));

        Result<?> result = postService.search("测试");

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    void search_shouldHandleEmptyKeyword() {
        Result<?> result = postService.search("");
        assertTrue(result.getCode() != 200);
    }
}
