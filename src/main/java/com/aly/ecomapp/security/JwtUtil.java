/*package com.aly.ecomapp.security;
import com.aly.ecomapp.exception.JwtException;
import com.aly.ecomapp.exception.JwtExceptionMessages;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    // TODO Uncomment the following line when UserRepo is available
//    @Autowired private TestUserRepo testUserRepo;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
//todo uncomment the following line when UserRepo is available
        //        User testUser = userRepo.findByUsername(username);
//        if(testUser == null) {
//            logger.error("JWT generation failed: {}", username);
//            throw new UserException(UserExceptionMessages.USER_NOT_FOUND);
//        }
        return Jwts.builder()
                .setSubject(username)
                .claim("role","ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

   public String getUsernameFromToken(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        if(claims==null) throw new JwtException(JwtExceptionMessages.INVALID_JWT_TOKEN);
        return claims.getSubject();
   }

    public void validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
        }catch (Exception e){
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException(JwtExceptionMessages.INVALID_JWT_TOKEN);
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
            logger.error("Expired JWT token: {}", token);
            throw new JwtException(JwtExceptionMessages.EXPIRED);
        }
    }


}*/
package com.aly.ecomapp.security;

import com.aly.ecomapp.exception.JwtException;
import com.aly.ecomapp.exception.JwtExceptionMessages;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private Long expirationTime; // ms
    @Getter private Key key;

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String roleWithPrefix) {
        return Jwts.builder()
                .setSubject(email)                          // email as subject
                .claim("role", roleWithPrefix)               // e.g. ROLE_ADMIN
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if (claims == null) throw new JwtException(JwtExceptionMessages.INVALID_JWT_TOKEN);
        return claims.getSubject();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException(JwtExceptionMessages.INVALID_JWT_TOKEN);
        }
    }
}




