package com.netology.diplom.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;

@Component
public class AuthProvider {
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_AUDIENCE = "audience";
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
   private String secret ;

    public String generateAccessToken(@NonNull UserDetails userDetails) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
      return Jwts.builder().setClaims(claims).setExpiration(Date.from(accessExpirationInstant))
                .signWith(SignatureAlgorithm.HS512,secret ).compact();

    }

    public Claims getClaimsFromToken(final String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (final Exception e) {
            claims = null;
        }
        return claims;
    }

    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final AuthUser user = (AuthUser) userDetails;
        final String username = getUsernameFromToken(token);
        return username.equals(user.getUsername());
    }

    public String getUsernameFromToken(final String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();

        } catch (final Exception e) {
            username = null;
        }
        return username;
    }
}
