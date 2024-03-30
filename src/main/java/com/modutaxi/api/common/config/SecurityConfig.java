package com.modutaxi.api.common.config;

import com.modutaxi.api.common.auth.CustomAccessDeniedHandler;
import com.modutaxi.api.common.auth.jwt.JwtAuthenticationFilter;
import com.modutaxi.api.common.auth.jwt.JwtExceptionFilter;
import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                    .requestMatchers("/api/members/KAKAO/login",
                            "api/members/APPLE/login",
                            "/api/members/sign-up",
                            "/swagger-ui/**").permitAll() // 허용된 주소
                    .anyRequest().permitAll() // Authentication 필요한 주소
            )
            .exceptionHandling((exceptionConfig) ->
                exceptionConfig.accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(new JwtExceptionFilter(),
                JwtAuthenticationFilter.class);
        return http.build();
    }
}
