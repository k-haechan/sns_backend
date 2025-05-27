package com.mysite.sns_backend.common.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

public record ApiErrorResponse(
	String message,
	String code,
	@JsonInclude(JsonInclude.Include.NON_NULL) List<String> errorDetails) {
	public static ApiErrorResponse of(ErrorCode errorCode) {
		return new ApiErrorResponse(errorCode.getMessage(), errorCode.getCode(), null);
	}

	public static ApiErrorResponse of(ErrorCode errorCode, List<String> errorDetails) {
		return new ApiErrorResponse(errorCode.getMessage(), errorCode.getCode(), errorDetails);
	}
}
