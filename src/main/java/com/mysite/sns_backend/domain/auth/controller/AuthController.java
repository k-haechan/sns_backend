package com.mysite.sns_backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import com.mysite.sns_backend.domain.member.entity.Member;
import com.mysite.sns_backend.domain.member.service.MemberService;
import com.mysite.sns_backend.global.cache.service.RedisBlacklistService;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;
import com.mysite.sns_backend.global.security.jwt.enums.JwtType;
import com.mysite.sns_backend.global.security.jwt.service.JwtService;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
	private final MemberService memberService;
	private final RedisBlacklistService redisBlacklistService;

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


	/**
	 * 로그아웃 처리 - 리프레시 토큰 블랙리스트 등록 및 쿠키 제거
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "로그아웃합니다.")
	public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = CookieUtil.extractCookie(request, JwtType.REFRESH_TOKEN.getTokenName());

		if (refreshToken == null || refreshToken.isBlank()) {
			throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
		}

		// 토큰에서 만료 시간 추출 후 블랙리스트에 등록
		Claims claims = jwtService.parseClaims(refreshToken, JwtType.REFRESH_TOKEN);
		long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
		redisBlacklistService.addToBlacklist(refreshToken, Math.max(remainingTime / 1000, 0));

		// 인증 정보 제거 및 쿠키 삭제
		SecurityContextHolder.clearContext();
		CookieUtil.clearCookie(response, JwtType.ACCESS_TOKEN.getTokenName());
		CookieUtil.clearCookie(response, JwtType.REFRESH_TOKEN.getTokenName());

		return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다.", null));
	}

	/**
	 * 액세스 토큰 재발급 - 리프레시 토큰 유효 시 새로운 액세스 토큰 반환
	 */
	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급", description = "Access Token을 재발급합니다.")
	public ResponseEntity<ApiResponse<?>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = CookieUtil.extractCookie(request, JwtType.REFRESH_TOKEN.getTokenName());

		if (refreshToken == null || refreshToken.isBlank()) {
			throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
		}

		// 토큰 파싱 및 사용자 인증 정보 생성
		String username = jwtService.parseClaims(refreshToken, JwtType.REFRESH_TOKEN).getSubject();
		Member member = memberService.findByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			member.getUsername(),
			null,
			member.getAuthorities()
		);

		// 블랙리스트 체크
		if (redisBlacklistService.isBlacklisted(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// 새 AccessToken 발급
		addTokenCookie(response, JwtType.ACCESS_TOKEN, authentication);

		return ResponseEntity.ok().body(ApiResponse.success("Access Token이 재발급되었습니다.", null));
	}
}
