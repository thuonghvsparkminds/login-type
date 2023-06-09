package com.example.logintype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LoginTypeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginTypeApplication.class, args);
    }

}
