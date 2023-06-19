package com.example.logintype.service;

import com.example.logintype.service.dto.request.ChangeEmailRequestDto;
import com.example.logintype.service.dto.request.UpdatePasswordRequestDto;
import com.example.logintype.service.dto.request.UpdateUserInfoRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     *
     */
    Page<UserResponseDto> getUsers(Pageable pageable);

    UserResponseDto getUser(Long bookId);

    void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto);

    void changeEmail(ChangeEmailRequestDto requestDto);

    //sign up
    void createUser(UserRequestDto request);

    void updateCurrentUser(UpdateUserInfoRequestDto request);

    void updateUserByAdmin(Long userId, UpdateUserInfoRequestDto request);

    void deleteUser(Long userId);

}
