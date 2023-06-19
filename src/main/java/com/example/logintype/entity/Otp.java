package com.example.logintype.entity;

import com.example.logintype.entity.enumrated.OtpStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "otp")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opt")
    private Long otp;

    @Column(name = "status")
    private OtpStatusEnum status;

    @Column(name = "expire_time")
    private Instant expireTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Otp(Long otp, OtpStatusEnum status, Instant expireTime, User user) {
        this.otp = otp;
        this.status = status;
        this.expireTime = expireTime;
        this.user = user;
    }
}
