package com.pp.web.dto.member;


import com.pp.domain.enums.Gender;
import com.pp.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMemberDTO {
        String nickname;
        Double rating;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberDTO {
        Long memberId;
        String name;
        String email;
        String phoneNumber;
        String studentNumber;
        Gender gender;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminResultDTO {
        String adminName;
        Role role;
    }
}