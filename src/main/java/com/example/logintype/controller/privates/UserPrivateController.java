package com.example.logintype.controller.privates;

import com.example.logintype.entity.User;
import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.response.UserResponseDto;
import com.example.logintype.service.util.PaginationUtil;
import com.example.logintype.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
}
