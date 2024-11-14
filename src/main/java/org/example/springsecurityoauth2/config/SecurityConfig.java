package org.example.springsecurityoauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .oauth2Login(Customizer.withDefaults());

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
