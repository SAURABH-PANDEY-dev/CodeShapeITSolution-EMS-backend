package com.codeshape.expenses.security;

import com.codeshape.expenses.model.User;
import com.codeshape.expenses.model.User.Role;
import com.codeshape.expenses.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public CustomOAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setRole(Role.EMPLOYEE);
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        response.setContentType("text/html");
        response.getWriter().write("<h3>✅ Login successful!</h3><p>Your JWT token:</p><code>" + token + "</code>");
        response.getWriter().flush();
    }
}