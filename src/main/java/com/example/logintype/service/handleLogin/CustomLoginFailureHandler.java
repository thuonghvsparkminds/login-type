package com.example.logintype.service.handleLogin;

import com.example.logintype.entity.User;
import com.example.logintype.entity.enumrated.StatusEnum;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.UserRepository;
import com.example.logintype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomLoginFailureHandler  extends SimpleUrlAuthenticationFailureHandler {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("This email not exist"));

        if (user.getStatus() != StatusEnum.BLOCKED) {
            if (user.getCountLoginFail() < 3) {
                userService.increaseLoginFail(user);
            } else {
                userService.lock(user);
                exception = new LockedException("Your account has been locked due to 3 failed attempts."
                        + " It will be unlocked after 30 minutes.");
            }
        } else {
            if (userService.unlockWhenTimeExpired(user)) {
                exception = new LockedException("Your account has been unlocked. Please try to login again.");
            }
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
