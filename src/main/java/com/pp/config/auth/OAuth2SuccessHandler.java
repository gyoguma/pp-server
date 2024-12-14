package com.pp.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.domain.Member;
import com.pp.domain.enums.Gender;
import com.pp.domain.enums.MemberStatus;
import com.pp.domain.enums.Role;
import com.pp.repository.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        UserDto userDto = userRequestMapper.toDto(oAuth2User);

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        log.info("토큰 발행 시작");
        Token token = tokenService.generateToken(userDto.getEmail(), "USER");

        log.info("Generated token: {}", token);

        //member가 새로운 사람인지 확인, 맴버 레포지토리에서 이메일 비여있으면 새유저
        boolean isNewUser = memberRepository.findByEmail(userDto.getEmail()).isEmpty();
        String targetUrl;

        //새로운 유저면 유저 정보 입력하는곳으로
       if (isNewUser) {

           OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
           String provideId = oauthToken.getName();
           Member member = Member.builder()
                   .name(userDto.getName())
                   .email(userDto.getEmail())
                   .password(UUID.randomUUID().toString())
                   .role(Role.USER)
                   .status(MemberStatus.ACTIVE)
                   .nickname("User" + UUID.randomUUID().toString().substring(0,5))
                   .phoneNumber("010-0000-0000")
                   .studentNumber("2020000000")
                   .gender(Gender.MALE)
                   .provider("google")
                   .providerId(provideId)
                   .rating(3.5) //초기값
                   .build();
           memberRepository.save(member);


           log.info("새로운 유저 발견!!!!!!!!!!!{}", userDto.getEmail());
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/users/addInfo")
                    .queryParam("access_token", token.getAccessToken())
                    .queryParam("refresh_token", token.getRefreshToken())
                    .build().toUriString();
        } else {
            log.info("기존유저{}", userDto.getEmail());
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/")
                    .queryParam("access_token", token.getAccessToken())
                    .queryParam("refresh_token", token.getRefreshToken())
                    .build().toUriString();
        }

        // Add tokens to response headers
        response.setHeader("Authorization", "Bearer " + token.getAccessToken());
        response.setHeader("Refresh", token.getRefreshToken());
        
        // Perform the redirect
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}