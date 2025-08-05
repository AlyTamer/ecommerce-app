package com.aly.ecomapp.security;
import com.aly.ecomapp.testing.TestUserRepo;
import com.aly.ecomapp.exceptions.JwtException;
import com.aly.ecomapp.exceptions.JwtExceptionMessages;
import com.aly.ecomapp.exceptions.UserException;
import com.aly.ecomapp.exceptions.UserExceptionMessages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import com.aly.ecomapp.testing.TestUser;
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
    private TestUserRepo testUserRepo;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        TestUser testUser = testUserRepo.findByUsername(username);
        if(testUser == null) {
            throw new UserException(UserExceptionMessages.userNotFound);
        }
        return Jwts.builder()
                .setSubject(username)
                .claim("role", testUser.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

   public String getUsernameToken(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        if(claims==null) throw new JwtException(JwtExceptionMessages.invalidToken);
        return claims.getSubject();
   }

    public void validateToken(String token) {
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
            return ;
        else {
            throw new JwtException(JwtExceptionMessages.expiredToken);
        }
    }


}
