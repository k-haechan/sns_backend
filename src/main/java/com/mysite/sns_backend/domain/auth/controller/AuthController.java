package com.mysite.sns_backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sns_backend.common.response.ApiResponse;
import com.mysite.sns_backend.common.util.CookieUtil;
import com.mysite.sns_backend.domain.auth.dto.login.LoginRequest;
import com.mysite.sns_backend.domain.auth.service.AuthService;
import com.mysite.sns_backend.global.security.jwt.enums.JwtType;
import com.mysite.sns_backend.global.security.jwt.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	/**
	 * 토큰을 생성하여 쿠키에 설정
	 */
	private void addTokenCookie(HttpServletResponse response, JwtType jwtType, Authentication authentication) {
		CookieUtil.setCookie(
			response,
			jwtType.getTokenName(),
			jwtService.generateToken(authentication, jwtType),
			jwtService.getExpirationTime(jwtType)
		);
	}

	/**
	 * 로그인 처리 - 인증 성공 시 액세스/리프레시 토큰 발급
	 */
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "회원 정보를 기반으로 로그인합니다.")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
		// 로그인 요청 처리
		Authentication authentication = authService.login(request);

		// SecurityContext에 인증 정보 저장
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// AccessToken, RefreshToken 쿠키에 저장
		addTokenCookie(response, JwtType.ACCESS_TOKEN, authentication);
		addTokenCookie(response, JwtType.REFRESH_TOKEN, authentication);

		return ResponseEntity.ok().body(ApiResponse.success("로그인에 성공했습니다.", null));
	}
}
