package com.pp.web.controller;

import com.pp.apipayload.ApiResponse;
import com.pp.config.auth.Token;
import com.pp.config.auth.TokenService;
import com.pp.domain.Member;
import com.pp.repository.member.MemberRepository;
import com.pp.service.member.MemberCommandService;
import com.pp.web.dto.member.MemberRequestDTO;
import com.pp.web.dto.member.MemberResponseDTO;
import com.pp.web.dto.member.UserInfoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin("http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class MemberViewController {

    private final MemberCommandService memberCommandService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;


    //유저 새로 가입할떄 추가 정보 받아옴
    @PostMapping("/users/addInfo")
    public ResponseEntity<?> addInfo(
            @RequestHeader("Authorization") String token, // JWT 토큰
            @RequestBody UserInfoUpdateDTO updateInfo ){   //리액트에서 받은 JSON의 userInfo르UserInfoUpdateDTO맞게 바꿈
        // JWT에서 사용자 정보 추출
        String jwtToken = token.replace("Bearer ", "");

        //토큰에서 이메일 추출
        String email = tokenService.getEmail(jwtToken);
        log.info("맴버뷰컨트롤러Attempting to find member with email: {}", email);
        // 사용자 데이터 업데이트
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("맴버뷰컨트롤러Member not found for email: {}", email);
                    return new RuntimeException("Member not found");
                });

        //member.setName(updateInfo.getName());
        member.setName(member.getName() + "/" +updateInfo.getStudentNumber());
        member.setStudentNumber(updateInfo.getStudentNumber());
        member.setPhoneNumber(updateInfo.getPhoneNumber());
        member.setNickname(updateInfo.getNickname());
        member.setGender(updateInfo.getGender());
        member.setNewUser(false); // 신규 사용자 상태 업데이트

        memberRepository.save(member);


        //업데이트 후 새 토큰 줌
        Token newTokens = tokenService.generateToken(email, "USER");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User information updated successfully");
        response.put("access_token", newTokens.getAccessToken());
        response.put("refresh_token", newTokens.getRefreshToken());


        return ResponseEntity.ok("User information updated successfully");
    }


    @PostMapping("/members/signup")
    @Operation(summary = "회원 가입 API",description = "회원 가입을 처리하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Void> add(@RequestBody @Valid MemberRequestDTO.JoinDto request){
        return null;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API",description = "로그인을 처리하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Void> login(@RequestBody @Valid MemberRequestDTO.JoinDto request){
        return null;
    }

    @GetMapping("/admin")
    @Operation(summary = "관리자 페이지 조회 API", description = "관리자 페이지 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<MemberResponseDTO.AdminResultDTO> admin(@RequestBody @Valid MemberRequestDTO.AdminCheckDTO request) {
        return null;
    }
}
