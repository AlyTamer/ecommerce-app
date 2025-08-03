package com.aly.ecomapp.security;

import com.aly.ecomapp.exceptions.JwtException;
import com.aly.ecomapp.exceptions.JwtExceptionMessages;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtConfig extends OncePerRequestFilter {
    private final CustomUserDetailService userDetailsService;

    @Autowired
    public JwtConfig(CustomUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JwtException(JwtExceptionMessages.invalidHeader);
            }
            String token = authHeader.substring(7);
            JwtUtil jwtUtil = new JwtUtil();
            jwtUtil.validateToken(token);
            String username = jwtUtil.getUserNameToken(token);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = io.jsonwebtoken.Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String role = claims.get("role", String.class);
                List<SimpleGrantedAuthority> authorities;
                UsernamePasswordAuthenticationToken auth;
                authorities = List.of(new SimpleGrantedAuthority(role));
                UserDetails user = userDetailsService.loadUserByUsername(username);
                auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                System.out.println("JWT Username: " + username);
                System.out.println("JWT Role: " + role);
                System.out.println("Authorities: " + authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } else {
                throw new JwtException(JwtExceptionMessages.illegalArgument);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
