package com.mysite.sns_backend.global.security.jwt.service;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.mysite.sns_backend.global.config.JwtConfig;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;
import com.mysite.sns_backend.global.security.jwt.dto.JwtProperties;
import com.mysite.sns_backend.global.security.jwt.enums.JwtType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtConfig jwtConfig;

	/**
	 * 토큰 종류에 따른 만료 시간 반환
	 *
	 * @param jwtType 액세스 토큰 또는 리프레시 토큰 타입
	 * @return 해당 토큰의 만료 기간 (Duration)
	 */
	public Duration getExpirationTime(JwtType jwtType) {
		return jwtConfig.getExpirationTime(jwtType);
	}

	/**
	 * 인증 정보를 기반으로 JWT 토큰 생성
	 *
	 * @param authentication 인증 정보
	 * @param jwtType 토큰 타입 (ACCESS_TOKEN, REFRESH_TOKEN)
	 * @return 생성된 JWT 토큰 문자열
	 */
	public String generateToken(Authentication authentication, JwtType jwtType) {

		JwtProperties jwtProperties = jwtConfig.getJwtProperties(jwtType);

		SecretKey secretKey = jwtProperties.secretKey();
		Duration expirationTime = jwtProperties.expirationTime();

		Date now = new Date();
		Date expiry = Date.from(now.toInstant().plus(expirationTime));

		// JWT 빌더 초기화
		JwtBuilder jwtBuilder = Jwts.builder()
			.subject(authentication.getName())  // 토큰의 주체(Username)
			.issuedAt(now)                      // 발행 시간
			.expiration(expiry)                 // 만료 시간
			.signWith(secretKey);               // 서명 (비밀키 사용)

		// 액세스 토큰일 경우만 roles 클레임 추가
		if (jwtType == JwtType.ACCESS_TOKEN) {
			jwtBuilder.claim("roles", authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList());
		}

		// 토큰 생성 및 반환
		return jwtBuilder.compact();
	}

	/**
	 * JWT 토큰에서 Claims(클레임) 추출
	 *
	 * @param token JWT 토큰 문자열
	 * @param jwtType 토큰 타입
	 * @return 파싱된 Claims 객체
	 * @throws CustomException 토큰 만료 또는 유효하지 않은 경우 예외 발생
	 */
	public Claims parseClaims(String token, JwtType jwtType) {

		JwtProperties jwtProperties = jwtConfig.getJwtProperties(jwtType);

		SecretKey secretKey = jwtProperties.secretKey();

		try {
			// JWT 파서 빌드 및 토큰 파싱
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		} catch (ExpiredJwtException e) {
			// 토큰 만료 시 처리
			throw new CustomException(ErrorCode.TOKEN_EXPIRED);

		} catch (JwtException | IllegalArgumentException e) {
			// 유효하지 않은 토큰 처리
			log.warn("JWT 파싱 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}
}
