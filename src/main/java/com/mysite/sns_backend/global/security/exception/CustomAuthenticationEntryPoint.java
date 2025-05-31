package com.mysite.sns_backend.global.security.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sns_backend.common.response.ApiErrorResponse;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
		response.setContentType("application/json;charset=UTF-8");
		ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ErrorCode.AUTHENTICATION_FAILED); // 에러코드 상황별로 분기처리
		response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
	}
}
