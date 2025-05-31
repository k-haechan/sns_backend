package com.mysite.sns_backend.domain.member.enums;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.key));
	}
}
