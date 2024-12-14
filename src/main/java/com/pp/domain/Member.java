package com.pp.domain;

import com.pp.domain.common.BaseEntity;
import com.pp.domain.enums.Gender;
import com.pp.domain.enums.MemberStatus;
import com.pp.domain.enums.Role;
import com.pp.domain.mapping.ChatParticipate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    String name; // 회원 이름

    @Column(nullable = false, length = 50)
    String email; // 회원 이메일

    @Column(nullable = false)
    String password; // 회원 비밀번호

    @Enumerated(EnumType.STRING)
    Role role; // 회원 권한

    @Column(nullable = false, length = 50)
    String nickname; // 회원 닉네임

    @Column(nullable = false, length = 15)
    String phoneNumber; // 회원 전화번호

    @Column(nullable = false, length = 15)
    String studentNumber; // 회원 학번

    @ColumnDefault("3.5")
    Double rating; // 회원 학점(당근 온도와 같은 역할)

    @Enumerated(EnumType.STRING)
    Gender gender; // 회원 성별

    @Enumerated(EnumType.STRING)
    MemberStatus status; // 회원 상태

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ReviewAlarm> reviewAlarmList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatAlarm> chatAlarmList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatParticipate> chatParticipateListList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessageListList = new ArrayList<>();


    //==로그인용==//
    @Column
    private String provider;    // OAuth2 provider (google, naver, etc)

    @Column
    private String providerId;  // OAuth2 provider's user id

    @Column(name = "is_new_user")
    private boolean isNewUser = false;


    //==연관관계 메서드==// - 로직상 양쪽을 다 구현.
    public void setMemberRating(Double changeRating) {
        this.rating += changeRating;
    }


    //로그인시 사용하는 매서드
    public Member update(String name, String studentNumber, String phoneNumber, String nickname, Gender gender) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.gender = gender;
        return this;
    }

    public void setNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public boolean isNewUser() {
        return isNewUser;
    }



}
