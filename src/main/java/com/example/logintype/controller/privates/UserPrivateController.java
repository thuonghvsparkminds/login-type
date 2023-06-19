package com.example.logintype.controller.privates;

import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.request.ChangeEmailRequestDto;
import com.example.logintype.service.dto.request.UpdatePasswordRequestDto;
import com.example.logintype.service.dto.request.UpdateUserInfoRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.UserResponseDto;
import com.example.logintype.service.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/private/users")
public class UserPrivateController {

    /**
     *
     */
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@PageableDefault Pageable pageable) {

        Page<UserResponseDto> page = userService.getUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                page
        );

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("bookId") Long bookId) {

        return ResponseEntity.ok(userService.getUser(bookId));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody UpdatePasswordRequestDto requestDto) {

        userService.updatePassword(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto requestDto) {

        userService.createUser(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-information")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserInfoRequestDto request) {

        userService.updateCurrentUser(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserInfoRequestDto request
    ) {

        userService.updateUserByAdmin(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-email")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody ChangeEmailRequestDto requestDto) {

        userService.changeEmail(requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
