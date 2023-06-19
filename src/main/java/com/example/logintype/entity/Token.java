package com.example.logintype.entity;

import com.example.logintype.entity.enumrated.TokenEnum;
import com.example.logintype.entity.enumrated.TokenTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "token")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;
    
    @Column(name = "expire_time")
    private Instant expireTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TokenEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenTypeEnum type;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Token(String token, Instant expireTime, TokenEnum status, TokenTypeEnum type, User user) {

        this.token = token;
        this.expireTime = expireTime;
        this.status = status;
        this.type = type;
        this.user = user;
    }
    
}
