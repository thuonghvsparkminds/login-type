package com.example.logintype.service.iml;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.logintype.constant.Constants;
import com.example.logintype.entity.*;
import com.example.logintype.entity.enumrated.*;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.exception.UnauthorizedException;
import com.example.logintype.repository.*;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.request.EmailForgotRequestDto;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.ResetPasswordRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import com.example.logintype.service.dto.response.LoginSuccessResponseDto;
import com.example.logintype.service.jwt.JwtUtils;
import com.example.logintype.service.util.EmailUtil;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{

    /**
     *
     */
    @Value("${app.jwt.blockTime}")
    private long blockTime;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final TokenRepository tokenRepository;
    private final OtpRepository otpRepository;
    private final EmailUtil emailUtil;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateJwtTokenRefresh(authentication);
            resetCountLoginFail(loginRequestDto.getEmail());

            return new LoginResponseDto(HttpStatus.OK, jwt, refreshToken);
        } catch (AuthenticationException e) {

            if (userRepository.findByEmailAndStatus(loginRequestDto.getEmail(), StatusEnum.UNVERIFIED).isPresent()) {
                return new LoginResponseDto(HttpStatus.NOT_ACCEPTABLE, null, null);
            }

            if (!userRepository.findByEmail(loginRequestDto.getEmail()).isPresent()) {
                return new LoginResponseDto(HttpStatus.NOT_FOUND, null, null);
            }

            User user = userRepository.findByEmail(loginRequestDto.getEmail()).get();

            if (user.getStatus() != StatusEnum.BLOCKED) {

                if (user.getCountLoginFail() < 3) {
                    increaseLoginFail(user);
                    return new LoginResponseDto(HttpStatus.NOT_FOUND, null, null);
                } else {
                    lock(user);
                    return new LoginResponseDto(HttpStatus.LOCKED, null, null);
                }
            } else {

                if (unlockWhenTimeExpired(user)) {
                    return new LoginResponseDto(HttpStatus.ACCEPTED, null, null);
                }

                return new LoginResponseDto(HttpStatus.LOCKED, null, null);
            }
        }
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, String tokenRefresh) {

        String token = request.getHeader(Constants.HEADER_TOKEN).substring(7);

        if (!tokenBlackListRepository.findByToken(token).isPresent()) {
            addTokenIntoBlackList(token);
        }

        if (!tokenBlackListRepository.findByToken(tokenRefresh).isPresent()) {
            addTokenIntoBlackList(tokenRefresh);
        }
    }

    @Override
    public LoginSuccessResponseDto refreshToken(String tokenRefresh, HttpServletResponse response) {

        if (tokenBlackListRepository.findByToken(tokenRefresh).isPresent()) {
            throw new UnauthorizedException("Your login session has expired!");
        }

        return new LoginSuccessResponseDto(jwtUtils.generateNewToken(tokenRefresh), tokenRefresh);
    }

    @Override
    @Transactional
    public void signUpWithToken(UserRequestDto authRequestDto) {

        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new BadRequestException("Email existed");
        }

        User user = new User(authRequestDto.getUsername(), authRequestDto.getEmail(),
                passwordEncoder.encode(authRequestDto.getPassword()));
        signUp(user, authRequestDto);

        String token = RandomString.make(45);
        Date time = new Date((new Date()).getTime() + 24 * 60 * 60 * 1000);
        Token tokenVerify = new Token(token, time.toInstant(), TokenEnum.NON_VERIFY, TokenTypeEnum.CREATE_USER, user);
        tokenRepository.save(tokenVerify);
        emailUtil.sendMailToken(user.getEmail(), token, "verify email");
    }

    @Override
    @Transactional
    public void signUpWithOtp(UserRequestDto authRequestDto) {

        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new BadRequestException("Email existed");
        }

        User user = new User(authRequestDto.getUsername(), authRequestDto.getEmail(),
                passwordEncoder.encode(authRequestDto.getPassword()));
        signUp(user, authRequestDto);

        Random rng = new java.util.Random();
        long otpToken = (Long)(rng.nextLong() % 100000) + 5200000L;
        Date time = new Date((new Date()).getTime() + 6 * 60 * 60 * 1000);
        Otp otp = new Otp(otpToken, OtpStatusEnum.NON_USE, time.toInstant(), user);

        otpRepository.save(otp);
        emailUtil.sendMailOtp(user.getEmail(), otpToken);
    }

    @Override
    @Transactional
    public void verifyUserByToken(@RequestHeader String tokenVerify){

        Token token = tokenRepository.findByTokenAndStatusAndType(tokenVerify, TokenEnum.NON_VERIFY, TokenTypeEnum.CREATE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Your token is not found"));

        if (token.getExpireTime().isAfter(Instant.now())) {

            User user = token.getUser();
            user.setStatus(StatusEnum.ACTIVE);
            token.setStatus(TokenEnum.VERIFIED);
        } else {

            throw new BadRequestException("Your token is expired");
        }
    }

    @Override
    @Transactional
    public void verifyUserByOtp(Long otpToken){

        Otp otp = otpRepository.findByOtpAndAndStatus(otpToken, OtpStatusEnum.NON_USE)
                .orElseThrow(() -> new ResourceNotFoundException("Your token is not found"));

        if (otp.getExpireTime().isAfter(Instant.now())) {

            User user = otp.getUser();
            user.setStatus(StatusEnum.ACTIVE);
            otp.setStatus(OtpStatusEnum.USED);
        } else {
            throw new BadRequestException("Your Otp is expired");
        }
    }

    @Override
    public void resendOtpVerifyUser(@RequestBody String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This user is not found"));

        if (otpRepository.existsByUser(user)) {

            Otp otp = otpRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("This user hasn't been sent verify email"));

            if (otp.getExpireTime().isBefore(Instant.now())) {
                throw new BadRequestException("Verify email has been sent to you, please check your email");
            } else {

                Random rng = new java.util.Random();
                long otpToken = (Long)(rng.nextLong() % 100000) + 5200000L;

                Date time = new Date((new Date()).getTime() + 6 * 60 * 60 * 1000);
                otp.setOtp(otpToken);
                otp.setExpireTime(time.toInstant());
                emailUtil.sendMailOtp(email, otpToken);
            }
        }
    }

    @Override
    @Transactional
    public void resendTokenVerifyUser(@RequestBody String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This user is not found"));

        if (tokenRepository.existsByUser(user)) {

            Token token = tokenRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("This user hasn't been sent verify email"));

            if (token.getExpireTime().isBefore(Instant.now())) {
                throw new BadRequestException("Verify email has been sent to you, please check your email");
            } else {

                String tokenRandom = RandomString.make(45);
                Date time = new Date((new Date()).getTime() + 24 * 60 * 60 * 1000);
                Token tokenVerify = new Token(tokenRandom, time.toInstant(), TokenEnum.NON_VERIFY,
                        TokenTypeEnum.CREATE_USER, user);

                tokenRepository.save(tokenVerify);
                emailUtil.sendMailToken(email, tokenRandom, "verify email");
            }
        }
    }

    @Override
    @Transactional
    public void resetPassword(HttpServletRequest request, EmailForgotRequestDto emailForgotRequestDto) {

        String email = emailForgotRequestDto.getEmail();
        User userRequest = userRepository.findByEmailAndStatus(email, StatusEnum.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find this user"));
        String token = RandomString.make(45);
        Date time = new Date((new Date()).getTime() + 60 * 60 * 1000);
        Token tokenVerify = new Token(token, time.toInstant(), TokenEnum.NON_VERIFY,
                TokenTypeEnum.FORGOT_PASSWORD, userRequest);

        tokenRepository.save(tokenVerify);
        emailUtil.sendMailToken(userRequest.getEmail(), token, "reset password");
    }

    @Override
    @Transactional
    public void updatePasswordByToken(String token, ResetPasswordRequestDto request) {

        if (token == null || token.trim().isEmpty()) {
            throw new UnauthorizedException("Your reset password token is invalid!");
        }

        Token tokenRequest = tokenRepository.findByTokenAndStatusAndType(token, TokenEnum.NON_VERIFY,
                TokenTypeEnum.FORGOT_PASSWORD)
                .orElseThrow(() -> new BadRequestException("This token is wrong"));
        User userRequest = tokenRequest.getUser();

        tokenRequest.setStatus(TokenEnum.VERIFIED);
        userRequest.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private void signUp(User user, UserRequestDto authRequestDto) {

        String role = authRequestDto.getRole();

        if (role == null) {

            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER);
            user.setRole(userRole);
        } else {
            switch (role) {
                case "ROLE_ADMIN":
                    Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN);
                    user.setRole(adminRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER);
                    user.setRole(userRole);
                    break;
            }
        }

//        user.setStatus(StatusEnum.UNVERIFIED);
        user.setStatus(StatusEnum.ACTIVE);
        user.setCountLoginFail(0);
        userRepository.save(user);
    }

    private void addTokenIntoBlackList(String token){

        TokenBlackList tokenBlackList = new TokenBlackList();
        tokenBlackList.setToken(token);
        tokenBlackList.setExpireTime(jwtUtils.getExpireTimeFromToken(token).toInstant());

        tokenBlackListRepository.save(tokenBlackList);
    }

    private void increaseLoginFail(User user) {
        int newCountFail = user.getCountLoginFail() + 1;
        user.setCountLoginFail(newCountFail);
        userRepository.save(user);
    }

    private void resetCountLoginFail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This user not exist"));

        user.setCountLoginFail(0);
        user.setBlockTime(null);
    }

    private void lock(User user) {

        Date time = new Date((new Date()).getTime() + blockTime * 1000);

        user.setStatus(StatusEnum.BLOCKED);
        user.setBlockTime(time.toInstant());
    }

    private boolean unlockWhenTimeExpired(User user) {

        if (
                user.getBlockTime() != null
                && user.getBlockTime().isBefore(Instant.now())
        ) {

            user.setBlockTime(null);
            user.setStatus(StatusEnum.ACTIVE);

            return true;
        }

        return false;
    }
}
