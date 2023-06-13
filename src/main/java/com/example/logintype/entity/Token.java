package com.example.logintype.entity;

import com.example.logintype.entity.enumrated.TokenEnum;
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
    @Column(name = "token_type", nullable = false)
    private TokenEnum tokenType;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Token(String token, Instant expireTime, TokenEnum tokenType, User user) {

        this.token = token;
        this.expireTime = expireTime;
        this.tokenType = tokenType;
        this.user = user;
    }
    
}
