package com.mysite.sns_backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(String message, @JsonInclude(JsonInclude.Include.NON_NULL) T data) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>("요청을 성공적으로 완료하였습니다.", data);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(message, data);
	}
}
