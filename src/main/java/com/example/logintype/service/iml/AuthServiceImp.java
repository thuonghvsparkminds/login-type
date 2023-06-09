package com.example.logintype.service.iml;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.logintype.constant.Constants;
import com.example.logintype.entity.Role;
import com.example.logintype.entity.TokenBlackList;
import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.RoleEnum;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.UnauthorizedException;
import com.example.logintype.repository.RoleRepository;
import com.example.logintype.repository.TokenBlackListRepository;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.AuthService;
import com.example.logintype.service.dto.request.LoginRequestDto;
import com.example.logintype.service.dto.request.UserRequestDto;
import com.example.logintype.service.dto.response.LoginResponseDto;
import com.example.logintype.service.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateJwtTokenRefresh(authentication);

        return new LoginResponseDto(jwt, refreshToken);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, String tokenRefresh) {

        String token = request.getHeader(Constants.HEADER_TOKEN).substring(7);

        if (!tokenBlackListRepository.findByToken(token).isPresent()) {
            addTokenIntoBlackList(token);
        }

        if (!tokenBlackListRepository.findByToken(tokenRefresh).isPresent()) {
            addTokenIntoBlackList(tokenRefresh);
        }

    }

    @Override
    public LoginResponseDto refreshToken(String tokenRefresh, HttpServletResponse response) {

        if (tokenBlackListRepository.findByToken(tokenRefresh).isPresent()) {
            throw new UnauthorizedException("Your login session has expired!");
        }

        return new LoginResponseDto(jwtUtils.generateNewToken(tokenRefresh), tokenRefresh);
    }

    @Override
    @Transactional
    public void signUp(UserRequestDto authRequestDto) {

        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new BadRequestException("Email existed");
        }
        User user = new User(authRequestDto.getUsername(), authRequestDto.getEmail(),
                passwordEncoder.encode(authRequestDto.getPassword()));
        String role = authRequestDto.getRole();

        if (role == null) {

            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER);
            user.setRole(userRole);
        } else {
            switch (role) {
                case "ROLE_ADMIN":
                    Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN);
                    user.setRole(adminRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER);
                    user.setRole(userRole);
                    break;
            }
        }
        user.setStatus(StatusEnum.ACTIVE);
        userRepository.save(user);
    }

    private void addTokenIntoBlackList(String token){

        TokenBlackList tokenBlackList = new TokenBlackList();
        tokenBlackList.setToken(token);
        tokenBlackList.setExpireTime(jwtUtils.getExpireTimeFromToken(token).toInstant());

        tokenBlackListRepository.save(tokenBlackList);
    }
}
