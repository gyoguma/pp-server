package com.pp.web.controller;


import com.pp.apipayload.ApiResponse;
import com.pp.config.auth.TokenService;
import com.pp.converter.MemberConverter;
import com.pp.domain.Member;
import com.pp.service.member.MemberCommandService;
import com.pp.service.member.MemberQueryService;
import com.pp.validation.annotation.ExistMember;
import com.pp.web.dto.member.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Validated
public class MemberRestController { // 구현 끝

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final TokenService tokenService;


    @GetMapping("/byToken")
    @Operation(summary = "토큰으로 유저 정보 얻기 이메일", description = "관리자 페이지 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ResponseEntity<?> getMembersByToken(HttpServletRequest request)  {
        //토큰 검증 시작
        String token = tokenService.resolveAccessToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing or invalid format");
        }
        if (!tokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        //토크 검증 완료

        String email = tokenService.getEmail(token);
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{memberId}")
    @Operation(summary = "특정 회원 조회 API", description = "특정 회원을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원의 아이디, path variable 입니다!")
    })
    public ApiResponse<MemberResponseDTO.GetMemberDTO> getMember(@ExistMember @PathVariable Long memberId) {
        Member member = memberQueryService.getMember(memberId);
        return ApiResponse.onSuccess(MemberConverter.toGetMemberResultDTO(member));
    }




    @PatchMapping("/{memberId}")
    @Operation(summary = "특정 회원 수정 API", description = "특정 회원을 수정하는 API입니다. (구현 X)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원의 아이디, path variable 입니다!")
    })
    public ApiResponse<MemberResponseDTO.UpdateMemberDTO> updateMember(@ExistMember @PathVariable Long memberId) {
        return null; // 회원 수정 구현 X
    }


    @DeleteMapping("/{memberId}")
    @Operation(summary = "특정 회원 삭제 API", description = "특정 회원을 삭제하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "회원의 아이디, path variable 입니다!")
    })
    public ApiResponse<Void> deleteMember(@ExistMember @PathVariable Long memberId) {
        // 인증된 사용자 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw null; //인증 안된 사용자
        }
        memberCommandService.deleteMember(memberId);
        return ApiResponse.onSuccess(null);
    }

}
