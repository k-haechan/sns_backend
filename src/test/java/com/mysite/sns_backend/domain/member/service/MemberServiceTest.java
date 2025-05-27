package com.mysite.sns_backend.domain.member.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mysite.sns_backend.domain.member.dto.join.JoinRequest;
import com.mysite.sns_backend.domain.member.entity.Member;
import com.mysite.sns_backend.domain.member.repository.MemberRepository;
import com.mysite.sns_backend.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder; // 만약 사용 중이라면

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("회원가입 테스트 - 성공")
	void joinSuccessTest() {
		// Given
		JoinRequest request =
			new JoinRequest("testUser", "테스트", "test1234", "test@email.com", "01012345678");

		when(memberRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByPhone(request.phone())).thenReturn(false);
		when(memberRepository.existsByUsername(request.username())).thenReturn(false);

		when(memberRepository.save(any(Member.class)))
			.thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 객체를 그대로 반환

		// When
		memberService.join(request);

		// Then
		verify(memberRepository).save(any(Member.class)); // save 호출 검증
	}

	@Test
	@DisplayName("회원가입 테스트 - 실패 (중복 이메일)")
	void joinFailDuplicateEmail() {
		// Given
		JoinRequest request =
			new JoinRequest("testUser", "테스트", "test1234", "test@email.com", "01012345678");

		when(memberRepository.existsByEmail(request.email())).thenReturn(true); // 이메일 중복 발생

		// When & Then
		assertThatThrownBy(() -> memberService.join(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("이미 가입된 이메일입니다.");

		verify(memberRepository, never()).save(any()); // 저장 메서드는 호출되지 않아야 함
	}

	@Test
	@DisplayName("회원가입 테스트 - 실패 (중복 전화번호)")
	void joinFailDuplicatePhone() {
		// Given
		JoinRequest request =
			new JoinRequest("testUser", "테스트", "test1234", "test@email.com", "01012345678");

		when(memberRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByPhone(request.phone())).thenReturn(true); // 전화번호 중복 발생

		// When & Then
		assertThatThrownBy(() -> memberService.join(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("이미 가입된 전화번호입니다.");

		verify(memberRepository, never()).save(any()); // 저장 메서드는 호출되지 않아야 함
	}

	@Test
	@DisplayName("회원가입 테스트 - 실패 (중복 사용자명)")
	void joinFailDuplicateUsername() {
		// Given
		JoinRequest request =
			new JoinRequest("testUser", "테스트", "test1234", "test@email.com", "01012345678");

		when(memberRepository.existsByEmail(request.email())).thenReturn(false);
		when(memberRepository.existsByPhone(request.phone())).thenReturn(false);
		when(memberRepository.existsByUsername(request.username())).thenReturn(true); // 사용자명 중복 발생

		// When & Then
		assertThatThrownBy(() -> memberService.join(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("이미 가입된 사용자명입니다.");

		verify(memberRepository, never()).save(any()); // 저장 메서드는 호출되지 않아야 함
	}
}
