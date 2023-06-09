package com.example.logintype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class HelloController {

    @GetMapping
    public ResponseEntity<?> sayHello() {
        return ResponseEntity.ok("Welcome");
    }

    @GetMapping("say-bye")
    public ResponseEntity<?> sayBye() {
        return ResponseEntity.ok("Goodbye");
    }
}
