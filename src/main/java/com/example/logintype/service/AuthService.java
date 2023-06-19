package com.example.logintype.service;

import com.example.logintype.service.dto.request.EmailForgotRequestDto;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.ResetPasswordRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import com.example.logintype.service.dto.response.LoginSuccessResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    void logout(HttpServletRequest request, @RequestHeader String tokenReFresh);

    LoginSuccessResponseDto refreshToken(@RequestHeader String tokenRefresh, HttpServletResponse response);

    void signUpWithToken(UserRequestDto authRequestDto);

    void signUpWithOtp(UserRequestDto authRequestDto);

    void verifyUserByToken(@RequestHeader String tokenVerify);

    void verifyUserByOtp(@RequestBody Long otpToken);

    void resendTokenVerifyUser(@RequestBody String email);

    void resendOtpVerifyUser(@RequestBody String email);

    void resetPassword(HttpServletRequest request, EmailForgotRequestDto emailForgotRequestDto);

    void updatePasswordByToken(String token, ResetPasswordRequestDto request);
}
