package com.mysite.sns_backend.global.security.jwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtType {
	ACCESS_TOKEN("access-token"),
	REFRESH_TOKEN("refresh-token");

	private final String tokenName;
}
