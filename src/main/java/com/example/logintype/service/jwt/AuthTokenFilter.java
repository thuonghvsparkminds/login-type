package com.example.logintype.service.jwt;

import com.example.logintype.constant.Constants;
import com.example.logintype.service.iml.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(Constants.HEADER_TOKEN);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        if (
//                request.getRequestURI().equals("/security-exam/api/common/auth/signup")
//                || request.getRequestURI().equals("/security-exam/api/common/auth/login")
//                || request.getRequestURI().equals("/security-exam/api/common/exams")
//        ) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }

        try {
            String jwt = parseJwt(request);
//            Optional<TokenBlackList> token = tokenBlackListRepository.findByToken(jwt);
//            if(token.isPresent()){
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            }

            if (jwt != null
                    && jwtUtils.validateJwtToken(jwt)
            ) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                setAuth(email, request);
                request.setAttribute(Constants.HEADER_USER_ID, jwtUtils.getUserIdFromJwtToken(jwt));

            }

            //anonymous case
            if (jwt == null){

                request.setAttribute(Constants.HEADER_ANONYMOUS, "yes");
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    public void setAuth(String email, HttpServletRequest request){
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
