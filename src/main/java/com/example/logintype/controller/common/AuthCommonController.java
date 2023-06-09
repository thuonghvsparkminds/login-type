package com.example.logintype.controller.common;

import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/common/auth")
public class AuthCommonController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/signUp")
    public ResponseEntity<Void> signup(@RequestBody UserRequestDto authRequestDto) {
        authService.signUp(authRequestDto);
        return ResponseEntity.ok().build();
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
