package com.mysite.sns_backend.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.sns_backend.common.response.ApiResponse;
import com.mysite.sns_backend.domain.member.dto.join.JoinRequest;
import com.mysite.sns_backend.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {
	private final MemberService memberService;

	@PostMapping
	@Operation(summary = "회원 가입", description = "회원의 정보를 제공하여 회원가입을 진행합니다.")
	public ResponseEntity<ApiResponse<Void>> join(@RequestBody @Valid JoinRequest request) {
		memberService.join(request);
		return ResponseEntity.ok(ApiResponse.success("회원 가입이 완료되었습니다.", null));
	}
}
