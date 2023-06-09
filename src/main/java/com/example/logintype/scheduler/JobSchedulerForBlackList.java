package com.example.logintype.scheduler;

import com.example.logintype.repository.TokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JobSchedulerForBlackList {

    @Autowired
    private final TokenBlackListRepository tokenBlackListRepository;

    @Scheduled(fixedRate = 1200000)
    public void removeTokenExpired() {
        tokenBlackListRepository.findAll().forEach(token->{
            if(token.getExpireTime().isAfter(Instant.now())){
                tokenBlackListRepository.delete(token);
            }
        });
    }
}
