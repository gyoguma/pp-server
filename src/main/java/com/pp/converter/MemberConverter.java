package com.pp.converter;

import com.pp.domain.Member;
import com.pp.web.dto.member.MemberResponseDTO;

public class MemberConverter {

    public static MemberResponseDTO.GetMemberDTO toGetMemberResultDTO(Member member) {
        return MemberResponseDTO.GetMemberDTO.builder()
                .nickname(member.getNickname())
                .rating(member.getRating())
                .build();
    }
}
