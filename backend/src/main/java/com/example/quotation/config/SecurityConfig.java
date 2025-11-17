package com.example.quotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ 允许跨域
                .cors().and()
                // ✅ 关闭CSRF防护，否则POST会被拦截
                .csrf().disable()
                // ✅ 放行登录、注册等接口
                .authorizeRequests()
                .antMatchers("/api/auth/login", "/api/auth/register").
                permitAll()
                .anyRequest()
                .permitAll();

        return http.build();
    }
}