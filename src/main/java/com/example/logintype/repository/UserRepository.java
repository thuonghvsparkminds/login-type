package com.example.logintype.repository;

import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndStatus(String email, StatusEnum status);

    Boolean existsByEmail(String email);

    @Query("UPDATE User u SET u.failedAttempt = :countFail, u.block_time = null " +
            "WHERE u.email = :email")
    public void updateFailedAttempts(int countFail, String email);

    Optional<User> findByEmail(String email);
}
