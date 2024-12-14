package com.pp.config.auth;


import com.pp.repository.member.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;


    // 허용된 이메일 도메인
    private static final String ALLOWED_EMAIL_DOMAIN = "@gachon.ac.kr";//"@gachon.ac.kr";


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //  1번 oauth유저 인포메이션 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        //	2번
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        //	3번
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.info("커스텀서비스registrationId = {}", registrationId);
        log.info("커스텀서비스userNameAttributeName = {}", userNameAttributeName);

        // 4번
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        var memberAttribute = oAuth2Attribute.convertToMap();


        //구글부터 받아온 유저 이메일
        log.info("구글부터 받아온 유저 이메일 CustomOAuth2UserService{}", oAuth2Attribute.getEmail());
        // 이메일 주소가 ALLOWED_EMAIL_DOMAIN 도메인이 아니면 예외 발생
        if (!oAuth2Attribute.getEmail().endsWith(ALLOWED_EMAIL_DOMAIN)) {
            log.info("이메일 주소 가천대 이메일 아님{}", oAuth2Attribute.getEmail());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_email_domain",
                            "Only @gachon.ac.kr email addresses are allowed",
                            null)
            );
        }


        return new DefaultOAuth2User(
                Collections.singleton(
                        new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute,
                "email");
    }
}