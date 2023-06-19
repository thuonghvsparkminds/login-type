package com.example.logintype.controller.common;

import com.example.logintype.constant.Constants;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.request.EmailForgotRequestDto;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.ResetPasswordRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import com.example.logintype.service.dto.response.LoginSuccessResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/common/auth")
public class AuthCommonController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {

        LoginResponseDto loginObjectResponse = authService.login(loginRequestDto);
        HttpStatus status = loginObjectResponse.getStatus();

        switch (status){
            case NOT_FOUND:
                return ResponseEntity.status(status).body("Wrong email or password");
            case LOCKED:
                return ResponseEntity.status(status).body("Your account has been locked due to 3 failed attempts." +
                        " It will be unlocked after 30 minutes.");
            case ACCEPTED:
                return ResponseEntity.status(status).body("Your account has been unlocked. Please try to login again.");
        }
        return ResponseEntity.ok(new LoginSuccessResponseDto(loginObjectResponse.getJwt(),
                loginObjectResponse.getJwtRefreshToken()));
    }

    @PostMapping("/sign-up-with-token")
    public ResponseEntity<Void> signUpWithToken(@RequestBody UserRequestDto authRequestDto) {
        authService.signUpWithToken(authRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up-with-otp")
    public ResponseEntity<Void> signUpWithOtp(@RequestBody UserRequestDto authRequestDto) {
        authService.signUpWithOtp(authRequestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/verify-by-token")
    public ResponseEntity<Void> verifyUserByToken(@RequestHeader(Constants.HEADER_TOKEN_VERIFY) String tokenVerify) {
        authService.verifyUserByToken(tokenVerify);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/verify-by-otp")
    public ResponseEntity<Void> verifyUserByOtp(@RequestBody Long tokenVerify) {
        authService.verifyUserByOtp(tokenVerify);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/resend-token-to-verify")
    public ResponseEntity<Void> resendVerifyUserByToken(@RequestBody String email) {
        authService.resendTokenVerifyUser(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/resend-otp-to-verify")
    public ResponseEntity<Void> resendVerifyUserByOtp(@RequestBody String email) {
        authService.resendOtpVerifyUser(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            HttpServletRequest httpServletRequest,
            @RequestBody EmailForgotRequestDto request
    ) {
        authService.resetPassword(httpServletRequest, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> resetPassword(
            @RequestHeader(Constants.HEADER_RESET_PASSWORD_TOKEN) String tokenResetPassword,
            @RequestBody ResetPasswordRequestDto request
    ) {

        authService.updatePasswordByToken(tokenResetPassword, request);
        return ResponseEntity.noContent().build();
    }
}
