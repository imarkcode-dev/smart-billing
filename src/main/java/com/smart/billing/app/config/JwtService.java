package com.smart.billing.app.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.smart.billing.app.dto.LoginResponseDTO;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    
    public String generateToken(LoginResponseDTO user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.roleUser());
        extraClaims.put("id", user.id());
        return buildToken(extraClaims, user.email(), jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))     
                .setExpiration(Date.from(expiry))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public boolean isTokenValid(String token, String userEmail) {
        final String email = extractEmail(token);
        return (email.equals(userEmail)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    


}
