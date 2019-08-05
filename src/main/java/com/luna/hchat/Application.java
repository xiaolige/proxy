package com.luna.hchat;

import com.luna.hchat.utils.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by Administrator on 2019/5/29.
 */
@SpringBootApplication
@MapperScan(basePackages = "com.luna.hchat.mapper")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

    @Bean
    public IdWorker idWorker(){
        return  new IdWorker(0,0);
    }
}
