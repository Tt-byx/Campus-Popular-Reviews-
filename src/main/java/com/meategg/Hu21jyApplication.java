package com.meategg;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class Hu21jyApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hu21jyApplication.class, args);
    }

}
