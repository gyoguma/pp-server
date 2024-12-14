package com.pp.web.dto.member;


import com.pp.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class MemberRequestDTO {

    @Getter
    @Setter
    public static class JoinDto{
        @NotBlank
        String name;

        @NotBlank
        @Email
        String email; // 이메일 필드 추가

        @NotBlank
        String password; // 비밀번호 필드 추가

        @NotNull
        Integer gender;

        @NotNull
        Integer birthYear;

        @NotNull
        Integer birthMonth;

        @NotNull
        Integer birthDay;

        @Size(min = 5, max = 12)
        String address;

        @Size(min = 5, max = 12)
        String specAddress;

        @NotNull
        Role role; // 역할 필드 추가
    }

    @Getter
    @Setter
    public static class AdminCheckDTO {
        @NotBlank
        Long memberId;

        @NotNull
        Role role;
    }

}