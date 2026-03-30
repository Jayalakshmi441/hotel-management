package com.hotel.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

        private final CustomAuthenticationSuccessHandler successHandler;

        public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
                this.successHandler = successHandler;
        }

        @Bean
        @Order(1)
        public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/dashboard", "/dashboard/**",
                                                "/guests", "/guests/**",
                                                "/rooms", "/rooms/**",
                                                "/staff", "/staff/**",
                                                "/reports", "/reports/**",
                                                "/reservations", "/reservations/**",
                                                "/admin/**")
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/login", "/css/**", "/js/**", "/img/**")
                                                .permitAll()
                                                .anyRequest().hasAuthority("ROLE_ADMIN"))
                                .formLogin(login -> login
                                                .loginPage("/admin/login")
                                                .loginProcessingUrl("/admin/login")
                                                .successHandler(successHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
                                                .logoutSuccessUrl("/admin/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/img/**")
                                                .permitAll()
                                                .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler(successHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}