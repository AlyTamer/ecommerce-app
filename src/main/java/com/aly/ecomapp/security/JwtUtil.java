package com.aly.ecomapp.security;
import com.aly.ecomapp.UserRepo;
import com.aly.ecomapp.exceptions.JwtException;
import com.aly.ecomapp.exceptions.JwtExceptionMessages;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expirationTime;
    @Getter
    private Key key;
    @Autowired
    private UserRepo userRepo;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        User user = userRepo.findByUserName(username);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", user.getRoles().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

   public String getUserNameToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
   }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
        }catch (Exception e){
            throw new JwtException(JwtExceptionMessages.invalidToken);
        }
        if (Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .after(new Date()))
            return true;
        else {
            throw new JwtException(JwtExceptionMessages.expiredToken);
        }
    }


}
