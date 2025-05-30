package com.mysite.sns_backend.global.config;

import java.time.Duration;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mysite.sns_backend.global.security.jwt.dto.JwtProperties;
import com.mysite.sns_backend.global.security.jwt.enums.JwtType;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * JWT 관련 설정 정보를 관리하는 Config 클래스
 * - application.yml 또는 properties 에서 secret, 만료시간을 주입받아 SecretKey 생성 및 보관
 * - JwtType(ACCESS_TOKEN, REFRESH_TOKEN)별로 JwtProperties 제공
 */
@Configuration
public class JwtConfig {

	@Value("${custom.jwt.access-token.secret}")
	private String accessSecret; // AccessToken 서명용 시크릿 문자열

	@Value("${custom.jwt.refresh-token.secret}")
	private String refreshSecret; // RefreshToken 서명용 시크릿 문자열

	@Value("${custom.jwt.access-token.expiration-time}")
	private Duration accessExpiration; // AccessToken 만료시간

	@Value("${custom.jwt.refresh-token.expiration-time}")
	private Duration refreshExpiration; // RefreshToken 만료시간

	private SecretKey accessKey; // AccessToken 서명용 SecretKey 객체
	private SecretKey refreshKey; // RefreshToken 서명용 SecretKey 객체

	/**
	 * Bean 초기화 직후 호출되어 시크릿 문자열을 SecretKey 객체로 변환 후 저장
	 */
	@PostConstruct
	private void initKeys() {
		this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
		this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
	}

	/**
	 * JwtType에 따라 JwtProperties(토큰명, SecretKey, 만료시간)를 반환
	 * @param jwtType ACCESS_TOKEN 또는 REFRESH_TOKEN
	 * @return JwtProperties 토큰 처리에 필요한 속성 집합
	 */
	public JwtProperties getJwtProperties(JwtType jwtType) {
		return switch (jwtType) {
			case ACCESS_TOKEN -> new JwtProperties("access-token", accessKey, accessExpiration);
			case REFRESH_TOKEN -> new JwtProperties("refresh-token", refreshKey, refreshExpiration);
		};
	}

	/**
	 * JwtType에 따라 토큰 만료시간 반환
	 * @param jwtType 토큰 종류
	 * @return Duration 만료시간
	 */
	public Duration getExpirationTime(JwtType jwtType) {
		return switch (jwtType) {
			case ACCESS_TOKEN -> accessExpiration;
			case REFRESH_TOKEN -> refreshExpiration;
		};
	}
}
