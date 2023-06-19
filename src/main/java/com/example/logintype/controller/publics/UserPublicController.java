package com.example.logintype.controller.publics;

import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.request.ChangeEmailRequestDto;
import com.example.logintype.service.dto.request.UpdatePasswordRequestDto;
import com.example.logintype.service.dto.request.UpdateUserInfoRequestDto;
import com.example.logintype.service.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/public/users")
public class UserPublicController {

    /**
     *
     */
    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody UpdatePasswordRequestDto requestDto) {

        userService.updatePassword(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-email")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody ChangeEmailRequestDto requestDto) {

        userService.changeEmail(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-information")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserInfoRequestDto request) {

        userService.updateCurrentUser(request);
        return ResponseEntity.noContent().build();
    }
}
