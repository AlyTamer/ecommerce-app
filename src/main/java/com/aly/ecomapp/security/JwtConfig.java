   /*package com.aly.ecomapp.security;

    import com.aly.ecomapp.exception.JwtException;
    import com.aly.ecomapp.exception.JwtExceptionMessages;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.IOException;
    import java.util.List;

    @Component
    public class JwtConfig extends OncePerRequestFilter {
        private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);
        private final CustomUserDetailService userDetailsService;
        private final JwtUtil jwtUtil;

        @Autowired
        public JwtConfig(CustomUserDetailService userDetailsService, JwtUtil jwtUtil) {
            this.userDetailsService = userDetailsService;
            this.jwtUtil           = jwtUtil;
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getRequestURI();
            return path.startsWith("/v3/api-docs")
                    || path.startsWith("/swagger-ui")
                    || path.startsWith("/swagger-resources")
                    || path.startsWith("/webjars")
                    || path.equals("/swagger-ui.html");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException, ServletException, IOException {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = auth.substring(7);
            try {
                jwtUtil.validateToken(token);
                String username = jwtUtil.getUsernameFromToken(token);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String role = claims.get("Role", String.class);

                var authorities = List.of(new SimpleGrantedAuthority(role));
                var user        = userDetailsService.loadUserByUsername(username);
                var authToken   = new UsernamePasswordAuthenticationToken(user, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (JwtException e) {
                logger.error("JWT validation failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }

            filterChain.doFilter(request, response);
        }
    }
*/

   package com.aly.ecomapp.security;

   import com.aly.ecomapp.exception.JwtException;
   import io.jsonwebtoken.Claims;
   import io.jsonwebtoken.Jwts;
   import jakarta.servlet.*;
   import jakarta.servlet.http.*;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
   import org.springframework.security.core.context.SecurityContextHolder;
   import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
   import org.springframework.stereotype.Component;
   import org.springframework.web.filter.OncePerRequestFilter;

   import java.io.IOException;

   @Component
   public class JwtConfig extends OncePerRequestFilter {
       private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

       private final CustomUserDetailService userDetailsService;
       private final JwtUtil jwtUtil;

       public JwtConfig(CustomUserDetailService userDetailsService, JwtUtil jwtUtil) {
           this.userDetailsService = userDetailsService;
           this.jwtUtil = jwtUtil;
       }

       @Override
       protected boolean shouldNotFilter(HttpServletRequest request) {
           String p = request.getRequestURI();
           return p.startsWith("/v3/api-docs")
                   || p.startsWith("/swagger-ui")
                   || p.startsWith("/swagger-resources")
                   || p.startsWith("/webjars")
                   || p.equals("/swagger-ui.html");
       }

       @Override
       protected void doFilterInternal(HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain chain) throws ServletException, IOException {
           String auth = request.getHeader("Authorization");
           if (auth == null || !auth.startsWith("Bearer ")) {
               chain.doFilter(request, response);
               return;
           }

           String token = auth.substring(7);
           try {
               jwtUtil.validateToken(token);
               String email = jwtUtil.getEmailFromToken(token);

               Claims claims = Jwts.parserBuilder()
                       .setSigningKey(jwtUtil.getKey())
                       .build()
                       .parseClaimsJws(token)
                       .getBody();

               var user = userDetailsService.loadUserByUsername(email); // loads from DB
               var authToken = new UsernamePasswordAuthenticationToken(
                       user,
                       null,
                       user.getAuthorities() // <- directly from DB
               );
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);


           } catch (JwtException e) {
               log.error("JWT validation failed: {}", e.getMessage());
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
               return;
           }

           chain.doFilter(request, response);
       }
   }
