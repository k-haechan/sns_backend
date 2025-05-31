package com.mysite.sns_backend.global.security.jwt.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mysite.sns_backend.common.util.CookieUtil;
import com.mysite.sns_backend.global.security.jwt.enums.JwtType;
import com.mysite.sns_backend.global.security.jwt.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	/**
	 * JWT 인증 필터: 요청마다 실행되며, access-token이 존재하면 사용자 인증을 수행합니다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// 쿠키에서 access-token 추출
		String token = CookieUtil.extractCookie(request, JwtType.ACCESS_TOKEN.getTokenName());

		// 토큰이 존재하면 검증 시도
		if (token != null) {
			try {
				// JWT 파싱 및 claims 추출
				Claims claims = jwtService.parseClaims(token, JwtType.ACCESS_TOKEN);
				String username = claims.getSubject();
				List<String> roles = claims.get("roles", List.class); // roles는 JWT에 포함된 사용자 권한

				// 권한 문자열을 Security 권한 객체로 변환
				List<SimpleGrantedAuthority> authorities = roles.stream()
					.map(SimpleGrantedAuthority::new)
					.toList();

				// 인증 객체 생성 및 SecurityContext에 저장
				Authentication authentication = new UsernamePasswordAuthenticationToken(
					username,
					null,
					authorities
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (JwtException e) {
				// 유효하지 않은 토큰일 경우 로그만 출력하고 인증 처리하지 않음
				log.warn("JWT 처리 실패: {}", e.getMessage());
			}
		}

		// 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}
