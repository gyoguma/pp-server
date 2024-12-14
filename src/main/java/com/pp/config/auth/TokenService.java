package com.pp.config.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Service
public class TokenService{

    private String secret;
    private Key secretKey;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    public Token generateToken(String uid, String role) {
        long tokenPeriod = 1000L * 60L * 10L; // 10 minutes for access token
        long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 3L; // 3 months for refresh token

        Claims claims = Jwts.claims().setSubject(uid);
        claims.put("role", role);

        Date now = new Date();
        Date accessTokenExpiry = new Date(now.getTime() + tokenPeriod);
        Date refreshTokenExpiry = new Date(now.getTime() + refreshPeriod);

        // Generate access token
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiry)
                .signWith(secretKey)
                .compact();

        // Generate refresh token
        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiry)
                .signWith(secretKey)
                .compact();

        return new Token(accessToken, refreshToken);
    }


    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            return refreshToken.substring(7);
        }
        return null;
    }
}