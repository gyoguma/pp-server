package com.pp.web.controller;


import com.pp.config.auth.Token;
import com.pp.config.auth.TokenService;
import com.pp.domain.Member;
import com.pp.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TokenController {
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    /*
    //json으로 처리할꺼라 필요없음
    @GetMapping("/token/expired")
    public ResponseEntity<String> auth() {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body("Token has expired");
    }

     */

    @GetMapping("/token/users")
    public ResponseEntity<Map<String, Object>> getUsersByToken(HttpServletRequest request) {
        String accessToken = tokenService.resolveAccessToken(request);
        if(!tokenService.validateToken(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = tokenService.getEmail(accessToken);
        Optional<Member> member = memberRepository.findByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("userId",member.get().getId());
        return ResponseEntity.ok(response);
    }

    //리프래시 토큰 발급
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Refresh");

        if (token != null && tokenService.validateToken(token)) {
            String email = tokenService.getEmail(token);
            Token newToken = tokenService.generateToken(email, "USER");

            //json header
            response.addHeader("Auth", newToken.getAccessToken());
            response.addHeader("Refresh", newToken.getRefreshToken());
            response.setContentType("application/json;charset=UTF-8");

            //json body
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", newToken.getAccessToken());
            tokenMap.put("refreshToken", newToken.getRefreshToken());
            return ResponseEntity.ok(tokenMap);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid or expired refresh token"));
        }
    }

@PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
    String accessToken = tokenService.resolveAccessToken(request);
    if (accessToken != null) {
        // Clear security context
        SecurityContextHolder.clearContext();
    }
    return ResponseEntity.ok("Logged out successfully");
    }






    @GetMapping("/로그인 필요한 웹사이트")
    public ResponseEntity<?> getSomeResource(HttpServletRequest request) {
        //토큰 검증 시작
        String token = tokenService.resolveAccessToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid format");
        }
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        //토크 검증 완료

        //원하는거
        String uid = tokenService.getEmail(token);

        return ResponseEntity.ok("Resource accessed successfully");
    }

    //refresh 토큰 기반으로 access토큰 재발급 api
    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = tokenService.resolveRefreshToken(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("[missing_token] Authorization header missing or invalid format");
        }

        if (!tokenService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("[invalid_token] Invalid or expired refresh token");
        }


        String uid = tokenService.getEmail(refreshToken);
        String role = "USER"; //와드 나중에 바꾸기  User user = userService.findByEmail(uid);  role = user.getRole()

        Token newTokens = tokenService.generateToken(uid, role);

        //json body
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newTokens.getAccessToken());
        //tokens.put("refresh_token", newTokens.getRefreshToken());

        return ResponseEntity.ok(tokens);
    }

}