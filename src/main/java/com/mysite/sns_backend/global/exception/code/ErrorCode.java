package com.mysite.sns_backend.global.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// 유효성 검증
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "유효성 검사 실패"),

	// 서버 오류
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류"),

	// 리소스 관련 오류
	NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "요청하신 리소스를 찾을 수 없습니다."),

	// 회원가입 실패
	MEMBER_JOIN_FAILED(HttpStatus.CONFLICT, "MEMBER_JOIN_FAILED", "회원가입에 실패했습니다. 이미 가입된 회원정보가 존재합니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 가입된 이메일입니다."),
	DUPLICATE_USERNAME(HttpStatus.CONFLICT, "DUPLICATE_USERNAME", "이미 가입된 사용자명입니다."),
	DUPLICATE_PHONE(HttpStatus.CONFLICT, "DUPLICATE_PHONE", "이미 가입된 전화번호입니다."),

	// 인증(401)
	BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIAL", "아이디 또는 비밀번호가 올바르지 않습니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "토큰이 존재하지 않습니다."),
	MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND", "존재하지 않는 사용자입니다."),
	AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", "인증에 실패했습니다."),

	// 권한(403)
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다.");



	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
