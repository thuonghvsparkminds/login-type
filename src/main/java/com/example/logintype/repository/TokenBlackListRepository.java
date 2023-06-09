package com.example.logintype.repository;

import com.example.logintype.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, String> {

    Optional<TokenBlackList> findByToken(String token);;

}
