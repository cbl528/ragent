package com.caobolun.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.caobolun")
@EnableScheduling
@MapperScan(basePackages = {
        "com.caobolun.bootstrap.rag.mapper",
        "com.caobolun.bootstrap.user.mapper"
})
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
    }

}
