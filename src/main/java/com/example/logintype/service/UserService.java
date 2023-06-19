package com.example.logintype.service;

import com.example.logintype.entity.User;
import com.example.logintype.service.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     *
     */
    Page<UserResponseDto> getUsers(Pageable pageable);

    UserResponseDto getUser(Long bookId);

    //sign up
//    UserResponseDto createUser(UserRequestDto request);

//    void updateUser(Long userId, UserRequestDto request);
//
//    void deleteUser(Long userId);

}
