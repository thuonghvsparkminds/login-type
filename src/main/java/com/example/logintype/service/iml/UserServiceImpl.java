package com.example.logintype.service.iml;

import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Value("${app.jwt.blockTime}")
    private long blockTime;

    private final UserRepository userRepository;

    @Override
    public void increaseLoginFail(User user) {
        int newFailAttempts = user.getCountLoginFail() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    @Override
    public void resetCountLoginFail(String email) {

        userRepository.updateFailedAttempts(0, email);
    }

    @Override
    public void lock(User user) {

        Date time = new Date((new Date()).getTime() + blockTime * 1000);

        user.setStatus(StatusEnum.BLOCKED);
        user.setBlockTime(time.toInstant());
    }

    @Override
    public boolean unlockWhenTimeExpired(User user) {

        if (
                user.getBlockTime() != null
                && user.getBlockTime().isBefore(Instant.now())
        ) {

            user.setBlockTime(null);
            user.setStatus(StatusEnum.ACTIVE);
            user.setCountLoginFail(0);;

            return true;
        }

        return false;
    }
}
