package com.mysite.sns_backend.domain.member.dto.join;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mysite.sns_backend.domain.member.entity.Member;
import com.mysite.sns_backend.domain.member.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRequest(
	@NotBlank(message = "회원 이름은 필수입니다.")
	@Size(min = 2, max = 50, message = "회원 이름은 2자 이상 50자 이내로 입력해야 합니다.")
	@Schema(description = "회원가입할 사용자의 username", example = "testUser")
	String username,

	@NotBlank(message = "이름은 필수입니다.")
	@Schema(description = "회원가입할 사용자의 name", example = "테스트")
	String name,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$",
		message = "비밀번호는 8~30자의 영문자와 숫자를 모두 포함해야 합니다."
	)
	@Schema(description = "회원가입할 사용자의 password", example = "test1234")
	String password,

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "유효한 이메일 주소를 입력해 주세요.")
	@Schema(description = "회원가입할 사용자의 email", example = "test@naver.com")
	String email,

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(
		regexp = "^010[0-9]{8}$",
		message = "전화번호는 010으로 시작하며 11자리 숫자여야 합니다."
	)
	@Schema(description = "회원가입할 사용자의 phone", example = "01012345678")
	String phone
) {
	public Member toEntity(PasswordEncoder passwordEncoder) {
		return new Member(username, name, passwordEncoder.encode(password), email, phone, Role.USER);
	}
}
