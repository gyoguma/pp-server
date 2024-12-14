package com.pp.web.controller;


import com.pp.apipayload.ApiResponse;
import com.pp.domain.Member;
import com.pp.service.member.MemberQueryService;
import com.pp.service.review.ReviewCommandService;
import com.pp.validation.annotation.ExistMember;
import com.pp.web.dto.review.ReviewRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
public class ReviewRestController { // 개발 완료

    private final ReviewCommandService reviewCommandService;
    private final MemberQueryService memberQueryService;

    @PostMapping("/{memberId}")
    @Operation(summary = "별점 리뷰 등록 API",description = "별점 리뷰를 등록하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "별점을 적용할 회원의 아이디, path variable 입니다.")
    })
    public ApiResponse<Void> register(@ExistMember @PathVariable Long memberId,
                                      @RequestBody @Valid ReviewRequestDTO.ReviewRatingDTO request){
        Member member = memberQueryService.getMember(memberId);
        Double starRating = request.getStarRating();
        reviewCommandService.changeRating(member, starRating);
        return ApiResponse.onSuccess(null);
    }
}
