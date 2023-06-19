package com.example.logintype.service.iml;

import com.example.logintype.entity.User;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.UserService;
import com.example.logintype.service.dto.response.UserResponseDto;
import com.example.logintype.service.mapper.UserMapper;
import com.example.logintype.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    /**
     *
     */
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserSpecification userSpecification;

    @Override
    public Page<UserResponseDto> getUsers(Pageable pageable) {

        Specification<User> userCondition = userSpecification.getUsersList();

        return userRepository.findAll(userCondition, pageable)
                .map(book -> userMapper.toDto(book));
    }

    @Override
    public UserResponseDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("This user is not exist"));
        return userMapper.toDto(user);
    }
}
