package com.example.logintype.repository;

import com.example.logintype.entity.Token;
import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.TokenEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    boolean existsByUser(User user);

    Optional<Token> findByTokenAndTokenType(String token, TokenEnum tokenEnum);

    Optional<Token> findByUser(User user);

}
