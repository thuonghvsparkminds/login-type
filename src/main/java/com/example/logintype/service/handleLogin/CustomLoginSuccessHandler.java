package com.example.logintype.service.handleLogin;

import com.example.logintype.entity.User;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.UserService;
import com.example.logintype.service.iml.UserDetailsServiceImpl;
import com.example.logintype.service.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetailsServiceImpl userDetails =  (UserDetailsServiceImpl) authentication.getPrincipal();
        Long userId = securityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("This user not exist"));

        if (user.getCountLoginFail() > 0) {
            userService.resetCountLoginFail(user.getEmail());
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
