package com.meategg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校圈社区 API 接口文档")
                        .version("1.0.0")
                        .description("校园热门点评系统后端接口文档，包含用户、帖子、评论对象、评论、管理等功能模块")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("admin@campus.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
