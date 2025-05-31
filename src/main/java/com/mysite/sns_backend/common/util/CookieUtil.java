package com.mysite.sns_backend.common.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 쿠키 생성 및 삭제, 조회 관련 유틸리티 클래스.
 * 전역적으로 정적 메서드만 사용하므로 인스턴스 생성 금지.
 */
public final class CookieUtil {

	private CookieUtil() {} // 유틸 클래스이므로 인스턴스 생성 방지

	/**
	 * 지정된 이름, 값, 만료 기간(Duration)으로 쿠키를 설정합니다.
	 * HTTP Only, Secure, SameSite=Strict 속성 포함.
	 *
	 * @param response  HTTP 응답 객체
	 * @param name      쿠키 이름
	 * @param value     쿠키 값
	 * @param maxAge    쿠키 유효 기간 (Duration 단위)
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
		ResponseCookie cookie = ResponseCookie.from(name, value)
			.httpOnly(true)             // 자바스크립트 접근 방지
			.secure(true)               // HTTPS 환경에서만 전송
			.path("/")                  // 전체 경로에서 쿠키 접근 가능
			.maxAge(maxAge)             // Duration을 초로 자동 변환
			.sameSite("Strict")         // 크로스 사이트 요청 시 쿠키 미포함
			.build();

		response.addHeader("Set-Cookie", cookie.toString());
	}

	/**
	 * 지정된 이름의 쿠키를 즉시 만료시킵니다.
	 *
	 * @param response HTTP 응답 객체
	 * @param name     삭제할 쿠키 이름
	 */
	public static void clearCookie(HttpServletResponse response, String name) {
		setCookie(response, name, "", Duration.ofSeconds(0));
	}

	/**
	 * 요청에서 특정 이름의 쿠키 값을 추출합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @param name    쿠키 이름
	 * @return        해당 쿠키 값, 없으면 null
	 */
	public static String extractCookie(HttpServletRequest request, String name) {
		if (request.getCookies() == null) return null;

		for (Cookie cookie : request.getCookies()) {
			if (name.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
