package com.codeshape.expenses.security;

import com.codeshape.expenses.model.User;
import com.codeshape.expenses.repository.UserRepository;
import com.codeshape.expenses.security.JwtAuthenticationFilter;
import com.codeshape.expenses.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomOAuth2SuccessHandler successHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users", "/api/users/login", "/api/users/refresh-token").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .anyRequest().hasRole("EMPLOYEE")
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(googleOAuth2UserService())
//                        )
//                        .successHandler(successHandler)
//                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> googleOAuth2UserService() {
        return userRequest -> {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oauthUser = delegate.loadUser(userRequest);

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(name);
                newUser.setPassword(UUID.randomUUID().toString()); // Dummy password
                newUser.setRole(User.Role.EMPLOYEE);
                return userRepository.save(newUser);
            });

            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            System.out.println("✅ Google login successful. JWT Token: " + token);

            return oauthUser;
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}