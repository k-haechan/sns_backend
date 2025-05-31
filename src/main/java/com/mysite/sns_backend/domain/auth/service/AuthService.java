package com.mysite.sns_backend.domain.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.mysite.sns_backend.domain.auth.dto.login.LoginRequest;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final AuthenticationManager authenticationManager;

	/**
	 * 로그인 시도
	 * @param request 로그인 요청 (username, password)
	 * @return 인증된 Authentication 객체
	 * @throws CustomException 인증 실패 시 커스텀 예외를 던짐
	 */
	public Authentication login(LoginRequest request) {
		try {
			// Spring Security 인증 처리 위임
			return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					request.username(),
					request.password()
				)
			);
		} catch (BadCredentialsException e) {
			// 회원이름 또는 비밀번호 불일치
			throw new CustomException(ErrorCode.BAD_CREDENTIAL);

		} catch (AuthenticationException e) {
			// 기타 인증 관련 예외 (계정 잠김, 만료 등 포함)
			throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
		}
	}
}
