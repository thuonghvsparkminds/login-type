package com.example.logintype.service;

import com.example.logintype.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void increaseLoginFail(User user);

    void resetCountLoginFail(String email);

    void lock(User user);

    boolean unlockWhenTimeExpired(User user);
}
