package com.example.logintype.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "token_black_list")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TokenBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;
    
    @Column(name = "expire_time")
    private Instant expireTime;
    
}
