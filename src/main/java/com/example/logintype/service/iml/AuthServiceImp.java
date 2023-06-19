package com.example.logintype.service.iml;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.logintype.constant.Constants;
import com.example.logintype.entity.Role;
import com.example.logintype.entity.Token;
import com.example.logintype.entity.TokenBlackList;
import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.RoleEnum;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.entity.enumrated.TokenEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.exception.UnauthorizedException;
import com.example.logintype.repository.RoleRepository;
import com.example.logintype.repository.TokenBlackListRepository;
import com.example.logintype.repository.TokenRepository;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import com.example.logintype.service.jwt.JwtUtils;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{

    /**
     *
     */
    @Value("${spring.mail.username}")
    private String username;

    @Value("${app.jwt.blockTime}")
    private long blockTime;

    @Value("${app.2fa.enabled}")
    private boolean isTwoFaEnabled;

    private static final int SECRET_SIZE = 10;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final TokenBlackListRepository tokenBlackListRepository;
    private final TokenRepository tokenRepository;
    private final JavaMailSender javaMailSender;

    @Override
    @Transactional
    public HttpStatus login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateJwtTokenRefresh(authentication);

            response.addHeader("X-Access-Token", jwt);
            response.addHeader("X-Refresh-Token", jwt);
            resetCountLoginFail(loginRequestDto.getEmail());
        } catch (AuthenticationException e) {

            if (userRepository.findByEmailAndStatus(loginRequestDto.getEmail(), StatusEnum.UNVERIFIED).isPresent()) {
                return HttpStatus.NOT_ACCEPTABLE;
            }

            if (!userRepository.findByEmail(loginRequestDto.getEmail()).isPresent()) {
                return HttpStatus.NOT_FOUND;
            }

            User user = userRepository.findByEmail(loginRequestDto.getEmail()).get();

            if (user.getStatus() != StatusEnum.BLOCKED) {

                if (user.getCountLoginFail() < 3) {
                    increaseLoginFail(user);
                    return HttpStatus.NOT_FOUND;
                } else {
                    lock(user);
                    return HttpStatus.LOCKED;
                }
            } else {

                if (unlockWhenTimeExpired(user)) {
                    return HttpStatus.ACCEPTED;
                }

                return HttpStatus.LOCKED;
            }
        }
        return HttpStatus.OK;
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
    public LoginResponseDto refreshToken(String tokenRefresh, HttpServletResponse response) {

        if (tokenBlackListRepository.findByToken(tokenRefresh).isPresent()) {
            throw new UnauthorizedException("Your login session has expired!");
        }

        return new LoginResponseDto(jwtUtils.generateNewToken(tokenRefresh), tokenRefresh);
    }

    @Override
    @Transactional
    public void signUp(UserRequestDto authRequestDto) {

        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new BadRequestException("Email existed");
        }

        User user = new User(authRequestDto.getUsername(), authRequestDto.getEmail(),
                passwordEncoder.encode(authRequestDto.getPassword()));
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
        user.setSecret(generateSecret());
        userRepository.save(user);

//        String token = RandomString.make(45);
//        Date time = new Date((new Date()).getTime() + 24 * 60 * 60 * 1000);
//        Token tokenVerify = new Token(token, time.toInstant(), TokenEnum.VERIFY, user);
//        tokenRepository.save(tokenVerify);
//
//        sendMail(user, token);
    }

    @Override
    @Transactional
    public void verifyUser(@RequestHeader String tokenVerify){

        Token token = tokenRepository.findByTokenAndTokenType(tokenVerify, TokenEnum.VERIFY)
                .orElseThrow(() -> new ResourceNotFoundException("Your token is not found"));

        if (token.getExpireTime().isAfter(Instant.now())) {

            User user = token.getUser();
            user.setStatus(StatusEnum.ACTIVE);
            tokenRepository.delete(token);
        } else {

            throw new BadRequestException("you token is expired");
        }
    }

    @Override
    @Transactional
    public void resendVerifyUser(@RequestBody String email) {

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
                token.setToken(tokenRandom);
                token.setExpireTime(time.toInstant());
                sendMail(user, tokenRandom);
            }
        }
    }

    private void addTokenIntoBlackList(String token){

        TokenBlackList tokenBlackList = new TokenBlackList();
        tokenBlackList.setToken(token);
        tokenBlackList.setExpireTime(jwtUtils.getExpireTimeFromToken(token).toInstant());

        tokenBlackListRepository.save(tokenBlackList);
    }

    private void mailSetup(String email, String contents, String link)
            throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(username, "Manage Book");
        helper.setTo(email);

        String subject = "Link reset password";
        String content = "<p> Hello " + email + ", </p>"
                + "<p> You have requested to" + contents + ", </p>"
                + "<p> Click the link below to " + contents + " </p>"
                + "<p> <a href = " + link + " > " + contents + " </a></p>"
                + "<p> Ignore this email if you do remember your password </p>";

        helper.setSubject(subject);
        helper.setText(content,true);

        javaMailSender.send(message);
    }

    private void sendMail(User user, String token) {

        try {

            String link= "http://localhost:3000/verify-email?token=" + token;
            mailSetup(user.getEmail(), "verify email", link);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(MessagingException e) {
            e.printStackTrace();
        }
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

    private String generateSecret() {
        return RandomStringUtils.random(SECRET_SIZE, true, true).toUpperCase();
    }
}
