package com.pp.apipayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pp.apipayload.code.BaseCode;
import com.pp.apipayload.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"}) // 필드 순서 명시적 지정
public class ApiResponse<T> { // "일관된" 에러 메시지 응답 Dto

    @JsonProperty("isSuccess") // 필드 이름 지정, 여기 같은 경우는 이미 isSuccess라 굳이 안 적어도 되긴 함.
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null은 포함하지 않음
    private T result; // 제네릭


    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result){ // 제네릭 메서드,   result가 어디서 오는지 잘 확인. 
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), result);
    }

    // 기존의 onSuccess 메서드보다 좀 더 유연하게 특정 상태 코드나 메시지를 커스터마이징해서 사용할 수 있다.

    public static <T> ApiResponse<T> of(BaseCode code, T result){ // HttpStatus까지, code는 인터페이스로 SuccessStatus가 이를 구현함
            return new ApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), result);
    }


    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(false, code, message, data);
    }
}