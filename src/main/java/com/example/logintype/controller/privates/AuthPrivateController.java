package com.example.logintype.controller.privates;

import com.example.logintype.constant.Constants;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/private/auth")
public class AuthPrivateController {

    private final AuthService authService;

    @PutMapping("/logout")
    public ResponseEntity<Void> logoutByAdmin(
            HttpServletRequest request,
            @RequestHeader(Constants.HEADER_TOKEN_REFRESH) String tokenRefresh
    ) {
        authService.logout(request, tokenRefresh);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshTokenByAdmin(
            @RequestHeader(Constants.HEADER_TOKEN_REFRESH) String tokenRefresh,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.refreshToken(tokenRefresh, response));
    }
}
