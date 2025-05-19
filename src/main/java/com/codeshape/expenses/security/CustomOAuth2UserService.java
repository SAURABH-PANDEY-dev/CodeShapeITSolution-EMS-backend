package com.codeshape.expenses.security;

import com.codeshape.expenses.model.User;
import com.codeshape.expenses.model.User.Role;
import com.codeshape.expenses.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public CustomOAuth2UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = super.loadUser(userRequest);
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

        // We’ll pass this token via attributes (or use cookie, later)
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_" + user.getRole().name()),
                oauthUser.getAttributes(),
                "email"
        );
    }
}