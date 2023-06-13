package com.example.logintype.controller.common;

import com.example.logintype.constant.Constants;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/common/auth")
public class AuthCommonController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        HttpStatus httpStatus = authService.login(loginRequestDto, response);

        switch (httpStatus){
            case NOT_FOUND:
                return ResponseEntity.status(httpStatus).body("Wrong email or password");
            case LOCKED:
                return ResponseEntity.status(httpStatus).body("Your account has been locked due to 3 failed attempts." +
                        " It will be unlocked after 30 minutes.");
            case ACCEPTED:
                return ResponseEntity.status(httpStatus).body("Your account has been unlocked. Please try to login again.");
        }
        return ResponseEntity.ok(new LoginResponseDto(response.getHeader("X-Access-Token")
                , response.getHeader("X-Refresh-Token")));
    }

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@RequestBody UserRequestDto authRequestDto) {
        authService.signUp(authRequestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/verify")
    public ResponseEntity<Void> verifyUser(@RequestHeader(Constants.HEADER_TOKEN_VERIFY) String tokenVerify) {
        authService.verifyUser(tokenVerify);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/resent-verify")
    public ResponseEntity<Void> resendVerifyUser(@RequestBody String email) {
        authService.resendVerifyUser(email);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/forgot-password")
//    public ResponseEntity<Void> forgotPassword(
//            HttpServletRequest httpServletRequest,
//            @RequestBody EmailForgotRequestDto request
//    ) {
//        authService.forgotPassword(httpServletRequest, request);
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/update-password")
//    public ResponseEntity<Void> resetPassword(
//            @RequestHeader(Constants.HEADER_RESET_PASSWORD_TOKEN) String tokenResetPassword,
//            @RequestBody NewPasswordRequestDto request
//    ) {
//
//        authService.updatePassword(tokenResetPassword, request);
//        return ResponseEntity.noContent().build();
//    }
}
