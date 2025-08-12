/*package com.aly.ecomapp.security;

import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
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
                throw new UserException(UserExceptionMessages.INVALID_CREDENTIALS);
            }
            if (userDetails == null) throw new UserException(UserExceptionMessages.USER_NOT_FOUND);
            if (!passwordEncoder.matches(authentication.getCredentials().toString(),
                    userDetails.getPassword())) {
                throw new UserException(UserExceptionMessages.INVALID_CREDENTIALS);
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        };


    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHTTPRequests -> authorizeHTTPRequests
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                                )
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
*/
package com.aly.ecomapp.security;

import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true) // you still can use @RolesAllowed on controllers
@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final JwtConfig jwtFilter;

    public WebSecurityConfig(CustomUserDetailService userDetailsService, JwtConfig jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authManager(PasswordEncoder passwordEncoder) {
        return authentication -> {
            UserDetails userDetails;
            try {
                // principal here is the EMAIL
                userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
            } catch (Exception e) {
                throw new UserException(UserExceptionMessages.INVALID_CREDENTIALS);
            }
            if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                throw new UserException(UserExceptionMessages.INVALID_CREDENTIALS);
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}

