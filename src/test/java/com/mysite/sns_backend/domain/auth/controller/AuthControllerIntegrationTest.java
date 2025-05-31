package com.mysite.sns_backend.domain.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hibernate.validator.internal.util.Contracts.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sns_backend.domain.auth.dto.login.LoginRequest;
import com.mysite.sns_backend.domain.member.dto.join.JoinRequest;
import com.mysite.sns_backend.domain.member.service.MemberService;
import com.mysite.sns_backend.global.cache.service.RedisBlacklistService;

import jakarta.servlet.http.Cookie;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MemberService memberService;
	@Autowired
	private RedisBlacklistService redisBlacklistService;

	@BeforeEach
	void setUp() {
		memberService.join(new JoinRequest(
			"testUser",
			"test1234",
			"password123",
			"test@email.com",
			"01012345678"
		));
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void loginSuccess() throws Exception {
		LoginRequest loginRequest = new LoginRequest("testUser", "password123");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("로그인에 성공했습니다."));
	}

	@Test
	@DisplayName("로그아웃 토큰 없음 예외 테스트")
	void logoutNoToken() throws Exception {
		mockMvc.perform(post("/api/v1/auth/logout"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("리프레시 토큰 없음 예외 테스트")
	void refreshTokenWithoutCookie() throws Exception {
		mockMvc.perform(post("/api/v1/auth/refresh"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
	void loginFailureWrongPassword() throws Exception {
		LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Test
	@DisplayName("로그인 실패 테스트 - 존재하지 않는 사용자")
	void loginFailureNonExistentUser() throws Exception {
		LoginRequest loginRequest = new LoginRequest("nonExistentUser", "password123");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Test
	@DisplayName("refresh 토큰 재발급 성공 테스트")
	void refreshTokenSuccess() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
		{
			"username": "testUser",
			"password": "password123"
		}
		"""))
			.andExpect(status().isOk())
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		Cookie[] cookies = response.getCookies();

		String refreshToken = Arrays.stream(cookies)
			.filter(cookie -> "refresh-token".equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);

		assertThat(refreshToken).as("refresh-token 쿠키가 있어야 합니다.").isNotNull();

		result = mockMvc.perform(post("/api/v1/auth/refresh")
				.cookie(new Cookie("refresh-token", refreshToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Access Token이 재발급되었습니다."))
		.andReturn();

		response = result.getResponse();
		Cookie[] newCookies = response.getCookies();

		boolean hasAccessToken = Arrays.stream(newCookies)
			.anyMatch(cookie -> "access-token".equals(cookie.getName()));

		assertTrue(hasAccessToken, "access-token 쿠키가 응답에 존재해야 합니다.");
	}

	@Test
	@DisplayName("로그아웃 성공 테스트, blacklisted refresh-token으로 재발급 실패")
	void refreshTokenFailureInvalidToken() throws Exception {
		// 로그인하여 쿠키에 access-token과 refresh-token을 설정합니다.
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
		{
			"username": "testUser",
			"password": "password123"
		}
		"""))
			.andExpect(status().isOk())
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		Cookie[] cookies = response.getCookies();

		String accessToken = Arrays.stream(cookies)
			.filter(cookie -> "access-token".equals(cookie.getName()))
			.findFirst()
			.map(Cookie::getValue)
			.orElse(null);

		String refreshToken = Arrays.stream(cookies)
			.filter(cookie -> "refresh-token".equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);

		assertThat(accessToken).as("access-token 쿠키가 있어야 합니다.").isNotNull();
		assertThat(refreshToken).as("refresh-token 쿠키가 있어야 합니다.").isNotNull();

		// 로그아웃 요청을 보내고, refresh-token을 블랙리스트에 추가합니다.
		mockMvc.perform(post("/api/v1/auth/logout")
				.cookie(new Cookie("refresh-token", refreshToken), new Cookie("access-token", accessToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("로그아웃에 성공했습니다."));
		assertThat(refreshToken).as("refresh-token 쿠키가 있어야 합니다.").isNotNull();

		// 블랙리스트에 추가된 refresh-token으로 재발급 시도
		mockMvc.perform(post("/api/v1/auth/refresh")
				.cookie(new Cookie("refresh-token", refreshToken)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));

		// 블랙리스트에 추가된 refresh-token 제거
		redisBlacklistService.removeFromBlacklist(refreshToken);
	}
}
