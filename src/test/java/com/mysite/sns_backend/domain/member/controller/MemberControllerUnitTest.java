package com.mysite.sns_backend.domain.member.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sns_backend.domain.member.dto.join.JoinRequest;
import com.mysite.sns_backend.domain.member.service.MemberService;
import com.mysite.sns_backend.global.config.SecurityConfig;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
class MemberControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;


	@Test
	@DisplayName("회원가입 실패 - 중복된 사용자 이름(username)")
	void join_failure_duplicateUsername() throws Exception {
		// given
		JoinRequest request = new JoinRequest(
			"existingUser", "테스트", "test1234", "test1234@email.com", "01012345678");

		doThrow(new CustomException(ErrorCode.DUPLICATE_USERNAME)).when(memberService).join(any(JoinRequest.class));

		// when & then
		mockMvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_USERNAME.getCode()))
			.andExpect(jsonPath("$.message").value(
				ErrorCode.DUPLICATE_USERNAME.getMessage()));
	}

	@Test
	@DisplayName("회원가입 실패 - 중복된 이메일(email)")
	void join_failure_duplicateEmail() throws Exception {
		// given
		JoinRequest request = new JoinRequest(
			"testUser", "테스트", "test1234", "test1234@email.com", "01012345678");

		doThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL)).when(memberService).join(any(JoinRequest.class));

		// when & then
		mockMvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
			.andExpect(jsonPath("$.message").value(
				ErrorCode.DUPLICATE_EMAIL.getMessage()));
	}

	@Test
	@DisplayName("회원가입 실패 - 중복된 전화번호(phone)")
	void join_failure_duplicatePhone() throws Exception {
		// given
		JoinRequest request = new JoinRequest(
			"testUser", "테스트", "test1234", "test1234@email.com", "01012345678");

		doThrow(new CustomException(ErrorCode.DUPLICATE_PHONE)).when(memberService).join(any(JoinRequest.class));

		// when & then
		mockMvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_PHONE.getCode()))
			.andExpect(jsonPath("$.message").value(
				ErrorCode.DUPLICATE_PHONE.getMessage()));
	}

	@Test
	@DisplayName("회원가입 실패 - 유효성 검사 실패")
	void join_failure_validation() throws Exception {
		// given
		JoinRequest request = new JoinRequest(
			"t", " ", "test", "invalidEmail", "01012345678");

		doThrow(new CustomException(ErrorCode.VALIDATION_FAILED)).when(memberService).join(any(JoinRequest.class));

		// when & then
		mockMvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.getCode()))
			.andExpect(jsonPath("$.message").value(
				ErrorCode.VALIDATION_FAILED.getMessage()))
			.andExpect(jsonPath("$.errorDetails", hasItem("email: 유효한 이메일 주소를 입력해 주세요.")))
			.andExpect(jsonPath("$.errorDetails", hasItem("name: 이름은 필수입니다.")))
			.andExpect(jsonPath("$.errorDetails", hasItem("password: 비밀번호는 8~30자의 영문자와 숫자를 모두 포함해야 합니다.")))
			.andExpect(jsonPath("$.errorDetails", hasItem("username: 회원 이름은 2자 이상 50자 이내로 입력해야 합니다.")));
	}
}
