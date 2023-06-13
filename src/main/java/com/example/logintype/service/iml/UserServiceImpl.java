package com.example.logintype.service.iml;

import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

}
