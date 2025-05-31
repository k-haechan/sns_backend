package com.mysite.sns_backend.domain.auth.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = "아이디는 필수입니다.")
	@Schema(description = "로그인할 사용자의 username", example = "testUser")
	String username,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Schema(description = "로그인할 사용자의 password", example = "test1234")
	String password
) {}
