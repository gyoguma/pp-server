package com.pp.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,IOException  {
        try {
            filterChain.doFilter(request, response);

            //토큰 만료시, 리액트에 TOKEN_EXPIRED보냄
        } catch (ExpiredJwtException e) {
            log.error("JWT Token Expired: {}", e.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token expired");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("code", "TOKEN_EXPIRED");

            new ObjectMapper().writeValue(response.getWriter(), errorResponse);

        } catch (Exception e) {
            log.error("JWT Authentication error", e);
            
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            
            try {
                new ObjectMapper().writeValue(response.getWriter(), errorResponse);
            } catch (IOException ex) {
                log.error("Error writing error response", ex);
            }
        }
    }
}