package com.example.logintype.repository;

import com.example.logintype.entity.Otp;
import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.OtpStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    boolean existsByUser(User user);

    Optional<Otp> findByOtpAndAndStatus(Long otp, OtpStatusEnum status);

    Optional<Otp> findByUser(User user);

}
