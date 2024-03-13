package com.modutaxi.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // TODO: JWTFilter 추가
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                    .requestMatchers("/visitor/**").hasAnyRole("VISITOR", "MEMBER", "MANAGER")
                    .requestMatchers("/member/**").hasAnyRole("MEMBER", "MANAGER")
                    .requestMatchers("/manager/**").hasRole("MANAGER")
                    .anyRequest().permitAll()
            );
            // TODO: Exception 추가
            // TODO: JwtFilter 추가
        return http.build();
    }
}
