package com.example.logintype.service;

import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public interface AuthService {

    HttpStatus login(LoginRequestDto loginRequestDto, HttpServletResponse response);

    void logout(HttpServletRequest request, @RequestHeader String tokenReFresh);

    LoginResponseDto refreshToken(@RequestHeader String tokenRefresh, HttpServletResponse response);

    void signUp(UserRequestDto authRequestDto);

    void verifyUser(@RequestHeader String tokenVerify);

    void resendVerifyUser(@RequestBody String email);

//
//    void forgotPassword(HttpServletRequest request, EmailForgotRequestDto emailForgotRequestDto);
//
//    void updatePassword(String token, NewPasswordRequestDto request);
}
