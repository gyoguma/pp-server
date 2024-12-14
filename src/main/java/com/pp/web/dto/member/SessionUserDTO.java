package com.pp.web.dto.member;


import com.pp.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

//로그인 할떄 세션에 저장할 필드
@Getter
@Setter
public class SessionUserDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String provider;

    private Map<String, Object> attributes;

    public SessionUserDTO(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.provider = member.getProvider();

    }
}