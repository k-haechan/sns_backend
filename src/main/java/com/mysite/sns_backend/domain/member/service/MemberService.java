package com.mysite.sns_backend.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sns_backend.domain.member.dto.join.JoinRequest;
import com.mysite.sns_backend.domain.member.entity.Member;
import com.mysite.sns_backend.domain.member.repository.MemberRepository;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	// 회원가입
	@Transactional
	public void join(JoinRequest request) {
		if (memberRepository.existsByEmail(request.email())) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		if (memberRepository.existsByPhone(request.phone())) {
			throw new CustomException(ErrorCode.DUPLICATE_PHONE);
		}

		if (memberRepository.existsByUsername(request.username())) {
			throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
		}

		try {
			memberRepository.save(request.toEntity(passwordEncoder));
		} catch (Exception e) {
			throw new CustomException(
				ErrorCode.MEMBER_JOIN_FAILED
			);
		}
	}

	public Member findByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
