package com.meategg.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.DTO.PostCreateRequest;
import com.meategg.entity.CommentContent;
import com.meategg.entity.CommentUser;
import com.meategg.entity.Post;
import com.meategg.entity.Result;
import com.meategg.entity.ReviewTarget;
import com.meategg.entity.User;
import com.meategg.mapper.CommentContentMapper;
import com.meategg.mapper.CommentUserMapper;
import com.meategg.mapper.PostMapper;
import com.meategg.mapper.ReviewTargetMapper;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class postServiceimpl extends ServiceImpl<PostMapper, Post> implements postService {
    @Resource
    private PostMapper postMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ReviewTargetMapper reviewTargetMapper;
    @Resource
    private CommentUserMapper commentUserMapper;
    @Resource
    private CommentContentMapper commentContentMapper;
    @Resource
    private OssService ossService;

    @Override
    public Result createPost(PostCreateRequest request, String username, org.springframework.web.multipart.MultipartFile image) {
        if (request == null) {
            return Result.fail("请求体不能为空");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return Result.fail("标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("内容不能为空");
        }
        if (request.getTitle().trim().length() > 100) {
            return Result.fail("标题长度不能超过 100 字");
        }
        if (username == null || username.trim().isEmpty()) {
            return Result.fail(401, "请先登录");
        }

        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query()
                .eq("username", username.trim())
                .last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = ossService.uploadPostImage(image);
            } catch (Exception e) {
                return Result.fail("文件上传失败: " + e.getMessage());
            }
        }

        Post post = new Post();
        post.setUserId(Long.valueOf(user.getId()));
        post.setTitle(request.getTitle().trim());
        post.setContent(request.getContent().trim());
        post.setTag(request.getTag());
        post.setImageUrl(imageUrl);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.insert(post);

        Map<String, Object> data = new HashMap<>();
        data.put("id", post.getId());
        data.put("title", post.getTitle());
        data.put("content", post.getContent());
        data.put("tag", post.getTag());
        data.put("imageUrl", post.getImageUrl());
        data.put("username", username.trim());
        return Result.ok(200, "发布成功", data);
    }

    @Override
    public Result listPosts() {
        List<Post> posts = postMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<Post>query().orderByDesc("id"));
        if (posts == null || posts.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        Set<Integer> userIds = new HashSet<>();
        for (Post p : posts) {
            if (p.getUserId() != null) {
                userIds.add(p.getUserId().intValue());
            }
        }
        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) {
                if (u != null && u.getId() != null) {
                    usernameMap.put(Long.valueOf(u.getId()), u.getUsername());
                }
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Post p : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("content", p.getContent());
            item.put("tag", p.getTag());
            item.put("imageUrl", p.getImageUrl());
            item.put("userId", p.getUserId());
            item.put("username", usernameMap.getOrDefault(p.getUserId(), "未知用户"));
            item.put("createdAt", p.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result createReviewTarget(Long postId, String targetName, String username) {
        if (postId == null) {
            return Result.fail("帖子ID不能为空");
        }
        if (targetName == null || targetName.trim().isEmpty()) {
            return Result.fail("评论对象名称不能为空");
        }
        if (targetName.trim().length() > 100) {
            return Result.fail("评论对象名称长度不能超过100字");
        }
        if (username == null || username.trim().isEmpty()) {
            return Result.fail(401, "请先登录");
        }

        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }

        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query()
                .eq("username", username.trim())
                .last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }

        ReviewTarget reviewTarget = new ReviewTarget();
        reviewTarget.setPostId(postId);
        reviewTarget.setTargetName(targetName.trim());
        reviewTarget.setCreatedAt(LocalDateTime.now());
        reviewTarget.setUpdatedAt(LocalDateTime.now());
        reviewTargetMapper.insert(reviewTarget);

        Map<String, Object> data = new HashMap<>();
        data.put("id", reviewTarget.getId());
        data.put("postId", reviewTarget.getPostId());
        data.put("targetName", reviewTarget.getTargetName());
        data.put("createdAt", reviewTarget.getCreatedAt());
        return Result.ok(200, "创建成功", data);
    }

    @Override
    public Result getReviewTargetsByPostId(Long postId) {
        if (postId == null) {
            return Result.fail("帖子ID不能为空");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }

        List<ReviewTarget> reviewTargets = reviewTargetMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<ReviewTarget>query()
                        .eq("post_id", postId)
                        .orderByDesc("created_at")
        );

        if (reviewTargets == null || reviewTargets.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (ReviewTarget rt : reviewTargets) {
            List<CommentUser> comments = commentUserMapper.selectList(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                            .eq("review_target_id", rt.getId())
            );

            List<CommentContent> contents = commentContentMapper.selectList(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentContent>query()
                            .eq("review_target_id", rt.getId())
            );

            double avgScore = 0;
            int scoreCount = 0;
            for (CommentContent cc : contents) {
                if (cc.getScore() != null) {
                    avgScore += cc.getScore();
                    scoreCount++;
                }
            }
            if (scoreCount > 0) {
                avgScore = avgScore / scoreCount;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", rt.getId());
            item.put("postId", rt.getPostId());
            item.put("targetName", rt.getTargetName());
            item.put("commentCount", comments.size());
            item.put("avgScore", scoreCount > 0 ? avgScore : null);
            item.put("createdAt", rt.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result getReviewTargetById(Long id) {
        if (id == null) {
            return Result.fail("评论对象ID不能为空");
        }
        ReviewTarget reviewTarget = reviewTargetMapper.selectById(id);
        if (reviewTarget == null) {
            return Result.fail("评论对象不存在");
        }

        Post post = postMapper.selectById(reviewTarget.getPostId());

        List<CommentUser> comments = commentUserMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("review_target_id", id)
                        .orderByDesc("created_at")
        );

        List<Map<String, Object>> commentList = new ArrayList<>();
        if (comments != null && !comments.isEmpty()) {
            List<Long> commentIds = new ArrayList<>();
            for (CommentUser cu : comments) {
                if (cu.getId() != null) {
                    commentIds.add(cu.getId());
                }
            }

            Map<Long, CommentContent> contentMap = new HashMap<>();
            if (!commentIds.isEmpty()) {
                List<CommentContent> contents = commentContentMapper.listByReviewTargetIdAndCommentIds(id, commentIds);
                for (CommentContent cc : contents) {
                    if (cc != null && cc.getCommentId() != null) {
                        contentMap.put(cc.getCommentId(), cc);
                    }
                }
            }

            for (CommentUser comment : comments) {
                CommentContent cc = contentMap.get(comment.getId());
                Map<String, Object> item = new HashMap<>();
                item.put("commentId", comment.getId());
                item.put("username", comment.getUsername());
                item.put("content", cc != null ? cc.getContent() : "");
                item.put("score", cc != null ? cc.getScore() : null);
                item.put("createdAt", comment.getCreatedAt());
                commentList.add(item);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", reviewTarget.getId());
        data.put("postId", reviewTarget.getPostId());
        data.put("postTitle", post != null ? post.getTitle() : "");
        data.put("targetName", reviewTarget.getTargetName());
        data.put("comments", commentList);
        data.put("createdAt", reviewTarget.getCreatedAt());
        return Result.ok(data);
    }

    @Override
    public Result getPostById(Long id) {
        if (id == null) {
            return Result.fail("帖子ID不能为空");
        }
        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        User user = userMapper.selectById(post.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", post.getId());
        data.put("title", post.getTitle());
        data.put("content", post.getContent());
        data.put("tag", post.getTag());
        data.put("imageUrl", post.getImageUrl());
        data.put("userId", post.getUserId());
        data.put("username", user != null ? user.getUsername() : "未知用户");
        data.put("createdAt", post.getCreatedAt());
        return Result.ok(data);
    }

    @Override
    public Result addComment(Long reviewTargetId, String username, String content, Integer score) {
        if (reviewTargetId == null) {
            return Result.fail("评论对象ID不能为空");
        }
        if (username == null || username.trim().isEmpty()) {
            return Result.fail(401, "请先登录");
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("评论内容不能为空");
        }
        if (content.trim().length() > 500) {
            return Result.fail("评论长度不能超过500字");
        }
        if (score != null && (score < 0 || score > 5)) {
            return Result.fail("评分范围必须在 0 到 5");
        }

        ReviewTarget reviewTarget = reviewTargetMapper.selectById(reviewTargetId);
        if (reviewTarget == null) {
            return Result.fail("评论对象不存在");
        }

        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query()
                .eq("username", username.trim())
                .last("limit 1"));
        if (user == null) {
            return Result.fail(401, "当前登录用户不存在");
        }

        CommentUser commentUser = new CommentUser();
        commentUser.setReviewTargetId(reviewTargetId);
        commentUser.setUsername(username.trim());
        commentUser.setCreatedAt(LocalDateTime.now());
        commentUserMapper.insert(commentUser);

        CommentContent commentContent = new CommentContent();
        commentContent.setReviewTargetId(reviewTargetId);
        commentContent.setCommentId(commentUser.getId());
        commentContent.setContent(content.trim());
        commentContent.setScore(score);
        commentContentMapper.insert(commentContent);

        Map<String, Object> data = new HashMap<>();
        data.put("commentId", commentUser.getId());
        data.put("reviewTargetId", reviewTargetId);
        data.put("username", commentUser.getUsername());
        data.put("content", commentContent.getContent());
        data.put("score", commentContent.getScore());
        data.put("createdAt", commentUser.getCreatedAt());
        return Result.ok(200, "评论发表成功", data);
    }

    @Override
    public Result getCommentsByReviewTargetId(Long reviewTargetId) {
        if (reviewTargetId == null) {
            return Result.fail("评论对象ID不能为空");
        }
        ReviewTarget reviewTarget = reviewTargetMapper.selectById(reviewTargetId);
        if (reviewTarget == null) {
            return Result.fail("评论对象不存在");
        }

        List<CommentUser> commentUsers = commentUserMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("review_target_id", reviewTargetId)
                        .orderByDesc("created_at")
        );

        List<Map<String, Object>> data = new ArrayList<>();
        if (commentUsers == null || commentUsers.isEmpty()) {
            return Result.ok(data);
        }

        List<Long> commentIds = new ArrayList<>();
        for (CommentUser cu : commentUsers) {
            if (cu.getId() != null) {
                commentIds.add(cu.getId());
            }
        }

        Map<Long, CommentContent> contentMap = new HashMap<>();
        if (!commentIds.isEmpty()) {
            List<CommentContent> contents = commentContentMapper.listByReviewTargetIdAndCommentIds(reviewTargetId, commentIds);
            for (CommentContent cc : contents) {
                if (cc != null && cc.getCommentId() != null) {
                    contentMap.put(cc.getCommentId(), cc);
                }
            }
        }

        for (CommentUser comment : commentUsers) {
            CommentContent cc = contentMap.get(comment.getId());
            Map<String, Object> item = new HashMap<>();
            item.put("commentId", comment.getId());
            item.put("reviewTargetId", comment.getReviewTargetId());
            item.put("username", comment.getUsername());
            item.put("content", cc != null ? cc.getContent() : "");
            item.put("score", cc != null ? cc.getScore() : null);
            item.put("createdAt", comment.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result listUserPosts(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query().eq("username", username.trim()).last("limit 1"));
        if (user == null) {
            return Result.fail("用户不存在");
        }

        List<Post> posts = postMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<Post>query()
                .eq("user_id", user.getId())
                .orderByDesc("created_at"));

        List<Map<String, Object>> data = new ArrayList<>();
        for (Post p : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("content", p.getContent());
            item.put("tag", p.getTag());
            item.put("imageUrl", p.getImageUrl());
            item.put("username", username.trim());
            item.put("createdAt", p.getCreatedAt());
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result listUserComments(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        User user = userMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<User>query().eq("username", username.trim()).last("limit 1"));
        if (user == null) {
            return Result.fail("用户不存在");
        }

        List<CommentUser> commentUsers = commentUserMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                .eq("username", username.trim())
                .orderByDesc("created_at"));

        if (commentUsers.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        List<Long> commentIds = new ArrayList<>();
        Set<Long> targetIds = new HashSet<>();
        for (CommentUser cu : commentUsers) {
            if (cu.getId() != null) {
                commentIds.add(cu.getId());
            }
            if (cu.getReviewTargetId() != null) {
                targetIds.add(cu.getReviewTargetId());
            }
        }

        Map<Long, CommentContent> contentMap = new HashMap<>();
        if (!commentIds.isEmpty()) {
            List<CommentContent> contents = commentContentMapper.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentContent>query().in("comment_id", commentIds));
            for (CommentContent cc : contents) {
                contentMap.put(cc.getCommentId(), cc);
            }
        }

        Map<Long, ReviewTarget> targetMap = new HashMap<>();
        Set<Long> postIds = new HashSet<>();
        if (!targetIds.isEmpty()) {
            List<ReviewTarget> targets = reviewTargetMapper.selectBatchIds(targetIds);
            for (ReviewTarget rt : targets) {
                targetMap.put(rt.getId(), rt);
                postIds.add(rt.getPostId());
            }
        }

        Map<Long, String> postTitleMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<Post> posts = postMapper.selectBatchIds(postIds);
            for (Post p : posts) {
                postTitleMap.put(p.getId(), p.getTitle());
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (CommentUser cu : commentUsers) {
            CommentContent cc = contentMap.get(cu.getId());
            Map<String, Object> item = new HashMap<>();
            item.put("commentId", cu.getId());
            item.put("content", cc != null ? cc.getContent() : "");
            item.put("score", cc != null ? cc.getScore() : null);
            item.put("createdAt", cu.getCreatedAt());
            
            ReviewTarget target = targetMap.get(cu.getReviewTargetId());
            if (target != null) {
                item.put("targetName", target.getTargetName());
                item.put("postId", target.getPostId());
                item.put("postTitle", postTitleMap.getOrDefault(target.getPostId(), "未知帖子"));
            }
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result listUserReviewTargets(String username) {
        List<CommentUser> userComments = commentUserMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentUser>query()
                        .eq("username", username)
        );

        if (userComments == null || userComments.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        Set<Long> targetIds = new HashSet<>();
        for (CommentUser cu : userComments) {
            if (cu.getReviewTargetId() != null) {
                targetIds.add(cu.getReviewTargetId());
            }
        }

        if (targetIds.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        List<ReviewTarget> targets = reviewTargetMapper.selectBatchIds(targetIds);
        Map<Long, ReviewTarget> targetMap = new HashMap<>();
        Set<Long> postIds = new HashSet<>();
        for (ReviewTarget rt : targets) {
            targetMap.put(rt.getId(), rt);
            postIds.add(rt.getPostId());
        }

        Map<Long, String> postTitleMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<Post> posts = postMapper.selectBatchIds(postIds);
            for (Post p : posts) {
                postTitleMap.put(p.getId(), p.getTitle());
            }
        }

        Map<Long, Integer> targetCommentCount = new HashMap<>();
        Map<Long, Double> targetScoreSum = new HashMap<>();
        Map<Long, Integer> targetScoreCount = new HashMap<>();

        List<Long> commentIds = new ArrayList<>();
        for (CommentUser cu : userComments) {
            if (cu.getId() != null) commentIds.add(cu.getId());
            Long tid = cu.getReviewTargetId();
            if (tid != null) {
                targetCommentCount.merge(tid, 1, Integer::sum);
            }
        }

        if (!commentIds.isEmpty()) {
            List<CommentContent> contents = commentContentMapper.listByReviewTargetIdAndCommentIds(null, commentIds);
            if (contents != null) {
                for (CommentContent cc : contents) {
                    CommentUser matchingCu = null;
                    for (CommentUser cu : userComments) {
                        if (cu.getId() != null && cu.getId().equals(cc.getCommentId())) {
                            matchingCu = cu;
                            break;
                        }
                    }
                    if (matchingCu != null && matchingCu.getReviewTargetId() != null && cc.getScore() != null) {
                        Long tid = matchingCu.getReviewTargetId();
                        targetScoreSum.merge(tid, (double) cc.getScore(), Double::sum);
                        targetScoreCount.merge(tid, 1, Integer::sum);
                    }
                }
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (ReviewTarget rt : targets) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", rt.getId());
            item.put("targetName", rt.getTargetName());
            item.put("postId", rt.getPostId());
            item.put("postTitle", postTitleMap.getOrDefault(rt.getPostId(), "未知帖子"));
            item.put("commentCount", targetCommentCount.getOrDefault(rt.getId(), 0));
            
            Integer sc = targetScoreCount.get(rt.getId());
            if (sc != null && sc > 0) {
                double avg = targetScoreSum.get(rt.getId()) / sc;
                item.put("avgScore", Math.round(avg * 10.0) / 10.0);
            } else {
                item.put("avgScore", null);
            }
            data.add(item);
        }

        data.sort((a, b) -> {
            Integer ca = (Integer) a.get("commentCount");
            Integer cb = (Integer) b.get("commentCount");
            return cb.compareTo(ca);
        });

        return Result.ok(data);
    }

    @Override
    public Result deletePost(Long postId) {
        Post post = getById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        removeById(postId);
        return Result.ok(200, "帖子已删除", null);
    }

    @Override
    public Result listAllPosts() {
        List<Post> posts = list(Wrappers.<Post>query().orderByDesc("created_at"));
        List<Map<String, Object>> data = new ArrayList<>();
        for (Post p : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("content", p.getContent().length() > 80 ? p.getContent().substring(0, 80) + "..." : p.getContent());
            item.put("tag", p.getTag());
            item.put("imageUrl", p.getImageUrl());
            item.put("userId", p.getUserId());
            item.put("createdAt", p.getCreatedAt());
            
            User user = userMapper.selectById(p.getUserId());
            item.put("username", user != null ? user.getUsername() : "未知");
            data.add(item);
        }
        return Result.ok(data);
    }

    @Override
    public Result getPostStats(Long postId) {
        if (postId == null) return Result.fail("帖子ID不能为空");
        
        List<ReviewTarget> targets = reviewTargetMapper.selectList(
            Wrappers.<ReviewTarget>query().eq("post_id", postId)
        );
        if (targets == null || targets.isEmpty()) {
            return Result.ok(java.util.Collections.emptyMap());
        }
        
        int[] scoreDist = new int[6];
        java.util.List<java.util.Map<String, Object>> ranking = new java.util.ArrayList<>();
        
        for (ReviewTarget rt : targets) {
            List<CommentContent> contents = commentContentMapper.selectList(
                Wrappers.<CommentContent>query().eq("review_target_id", rt.getId())
            );
            
            double totalScore = 0;
            int scoreCnt = 0;
            for (CommentContent cc : contents) {
                if (cc.getScore() != null && cc.getScore() >= 1 && cc.getScore() <= 5) {
                    scoreDist[cc.getScore()]++;
                    totalScore += cc.getScore();
                    scoreCnt++;
                }
            }
            
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("targetId", rt.getId());
            item.put("targetName", rt.getTargetName());
            item.put("avgScore", scoreCnt > 0 ? totalScore / scoreCnt : null);
            item.put("commentCount", contents != null ? contents.size() : 0);
            ranking.add(item);
        }
        
        ranking.sort((a, b) -> {
            Double sa = a.get("avgScore") != null ? ((Number)a.get("avgScore")).doubleValue() : -1;
            Double sb = b.get("avgScore") != null ? ((Number)b.get("avgScore")).doubleValue() : -1;
            return sb.compareTo(sa);
        });
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        java.util.Map<String, Integer> distribution = new java.util.LinkedHashMap<>();
        distribution.put("拉完了", scoreDist[1]);
        distribution.put("NPC", scoreDist[2]);
        distribution.put("人上人", scoreDist[3]);
        distribution.put("顶级", scoreDist[4]);
        distribution.put("夯", scoreDist[5]);
        stats.put("distribution", distribution);
        stats.put("ranking", ranking);
        stats.put("totalRatings", scoreDist[1] + scoreDist[2] + scoreDist[3] + scoreDist[4] + scoreDist[5]);
        
        return Result.ok(stats);
    }

    @Override
    public Result getReviewTargetStats(Long targetId) {
        if (targetId == null) return Result.fail("评论对象ID不能为空");
        List<CommentContent> contents = commentContentMapper.selectList(
            Wrappers.<CommentContent>query().eq("review_target_id", targetId)
        );
        if (contents == null || contents.isEmpty()) {
            java.util.Map<String, Object> empty = new java.util.LinkedHashMap<>();
            java.util.Map<String, Integer> d = new java.util.LinkedHashMap<>();
            d.put("拉完了", 0); d.put("NPC", 0); d.put("人上人", 0); d.put("顶级", 0); d.put("夯", 0);
            empty.put("distribution", d);
            empty.put("totalRatings", 0);
            return Result.ok(empty);
        }
        int[] scoreDist = new int[6];
        double total = 0;
        int cnt = 0;
        for (CommentContent cc : contents) {
            if (cc.getScore() != null && cc.getScore() >= 1 && cc.getScore() <= 5) {
                scoreDist[cc.getScore()]++;
                total += cc.getScore();
                cnt++;
            }
        }
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> distribution = new java.util.LinkedHashMap<>();
        distribution.put("拉完了", scoreDist[1]);
        distribution.put("NPC", scoreDist[2]);
        distribution.put("人上人", scoreDist[3]);
        distribution.put("顶级", scoreDist[4]);
        distribution.put("夯", scoreDist[5]);
        stats.put("distribution", distribution);
        stats.put("totalRatings", cnt);
        stats.put("avgScore", cnt > 0 ? total / cnt : null);
        return Result.ok(stats);
    }
}
//1