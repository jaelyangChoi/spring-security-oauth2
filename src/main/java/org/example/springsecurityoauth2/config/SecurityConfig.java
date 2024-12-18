package org.example.springsecurityoauth2.config;

import lombok.RequiredArgsConstructor;
import org.example.springsecurityoauth2.oauth2.CustomClientRegistrationRepo;
import org.example.springsecurityoauth2.oauth2.CustomOAuth2AuthorizedClientService;
import org.example.springsecurityoauth2.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo clientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService authorizedClientService;
    private final JdbcTemplate jdbcTemplate;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .clientRegistrationRepository(clientRegistrationRepo.clientRegistrationRepository())
                        .authorizedClientService(authorizedClientService
                                .oAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepo.clientRegistrationRepository()))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                );

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated());

        http.csrf(csrf -> csrf.disable());
        http.formLogin(login -> login.disable());
        http.httpBasic(basic -> basic.disable());


        return http.build();
    }
}
