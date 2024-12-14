package com.pp.web.dto.member;


import com.pp.domain.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoUpdateDTO {
    private String name;
    private String studentNumber;
    private String phoneNumber;
    private String nickname;
    private Gender gender;
}
