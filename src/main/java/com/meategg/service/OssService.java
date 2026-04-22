package com.meategg.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OssService {

    private static final Logger log = LoggerFactory.getLogger(OssService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DATE_FMT);
        String fileName = dir + "/" + datePath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

        log.info("开始上传文件到OSS: {}, endpoint: {}, bucket: {}", fileName, endpoint, bucketName);

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());
            ossClient.putObject(putRequest);
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            log.info("文件上传成功: {}", url);
            return url;
        } catch (Exception e) {
            log.error("OSS上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("OSS上传失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }

    public String uploadAvatar(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) return null;
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String safeUsername = sanitize(username);
        String datePath = LocalDate.now().format(DATE_FMT);
        String fileName = "avatars/" + safeUsername + "/" + datePath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

        log.info("开始上传头像到OSS: {}", fileName);

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());
            ossClient.putObject(putRequest);
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            log.info("头像上传成功: {}", url);
            return url;
        } catch (Exception e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("头像上传失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }

    public String uploadPostImage(MultipartFile file) {
        return upload(file, "posts");
    }

    private String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) return "unknown";
        return input.replaceAll("[^a-zA-Z0-9_\\u4e00-\\u9fa5]", "_").toLowerCase();
    }
}
