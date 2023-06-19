package com.example.logintype.service.iml;

import com.example.logintype.entity.Token;
import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.entity.enumrated.TokenEnum;
import com.example.logintype.entity.enumrated.TokenTypeEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.TokenRepository;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.request.ChangeEmailRequestDto;
import com.example.logintype.service.dto.request.UpdatePasswordRequestDto;
import com.example.logintype.service.dto.request.UpdateUserInfoRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.UserResponseDto;
import com.example.logintype.service.mapper.UserMapper;
import com.example.logintype.service.util.EmailUtil;
import com.example.logintype.service.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    /**
     *
     */
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;
    private final TokenRepository tokenRepository;
    private final AuthService authService;

    @Override
    public Page<UserResponseDto> getUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(book -> userMapper.toDto(book));
    }

    @Override
    public UserResponseDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("This user is not exist"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequestDto requestDto) {

        long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("This user is not exist"));

        if (user.getPassword() != passwordEncoder.encode(requestDto.getOldPassword())) {
            throw new BadRequestException("Your old password is not match");
        }

        if (requestDto.getNewPassword().trim().isEmpty()) {
            throw new BadRequestException("Your new password is only contain space. Please choose another password");
        }

        if (
                user.getPassword() == passwordEncoder.encode(requestDto.getOldPassword())
                && !requestDto.getNewPassword().trim().isEmpty()
        ) {

            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            log.info("This userId " + userId + "changed password success");
        }
    }

    @Override
    @Transactional
    public void changeEmail(ChangeEmailRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getOldEmail())
                .orElseThrow((() -> new BadRequestException("This email is not exist")));

        String token = RandomString.make(45);
        Date time = new Date((new Date()).getTime() + 60 * 60 * 1000);
        Token tokenVerify = new Token(token, time.toInstant(), TokenEnum.NON_VERIFY,
                TokenTypeEnum.FORGOT_PASSWORD, user);

        tokenRepository.save(tokenVerify);
        emailUtil.sendMailToken(user.getEmail(), token, "change mail");
    }

    @Override
    @Transactional
    public void createUser(UserRequestDto request) {

        authService.signUpWithToken(request);
    }

    @Override
    @Transactional
    public void updateCurrentUser(UpdateUserInfoRequestDto request) {

        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow((() -> new BadRequestException("This email is not exist")));

        user.setUsername(request.getNewUsername());
    }

    @Override
    @Transactional
    public void updateUserByAdmin(Long userId, UpdateUserInfoRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow((() -> new BadRequestException("This email is not exist")));

        user.setUsername(request.getNewUsername());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow((() -> new BadRequestException("This email is not exist")));

        user.setStatus(StatusEnum.DELETED);
    }
}
