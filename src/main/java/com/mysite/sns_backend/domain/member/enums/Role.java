package com.mysite.sns_backend.domain.member.enums;

import lombok.Getter;

@Getter
public enum Role {
	USER("ROLE_USER", "일반 사용자"),
	ADMIN("ROLE_ADMIN", "관리자");

	private final String key;
	private final String title;

	Role(String key, String title) {
		this.key = key;
		this.title = title;
	}
}
