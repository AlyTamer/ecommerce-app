package com.aly.ecomapp.security;

import com.aly.ecomapp.exceptions.UserException;
import com.aly.ecomapp.exceptions.UserExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {
    private final CustomUserDetailService userDetailsService;
    private final JwtConfig jwtFilter;


    @Autowired
    public WebSecurityConfig(CustomUserDetailService userDetailsService, JwtConfig jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;

    }

    @Bean
    public AuthenticationManager authManager(PasswordEncoder passwordEncoder) throws RuntimeException {
        return authentication -> {
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
            } catch (Exception e) {
                throw new UserException(UserExceptionMessages.invalidCredentials);
            }
            if (userDetails == null) throw new UserException(UserExceptionMessages.userNotFound);
            if (!passwordEncoder.matches(authentication.getCredentials().toString(),
                    userDetails.getPassword())) {
                throw new UserException(UserExceptionMessages.invalidCredentials);
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        };


    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeHTTPRequests -> authorizeHTTPRequests
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/swagger-ui/index.html",
                                "/swagger-ui/index.html#/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
