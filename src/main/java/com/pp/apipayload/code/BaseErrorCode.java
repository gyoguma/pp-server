package com.pp.apipayload.code;

public interface BaseErrorCode { // ErrorStatus 코드를 위한 인터페이스

    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();
}