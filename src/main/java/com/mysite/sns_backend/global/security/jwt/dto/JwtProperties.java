package com.mysite.sns_backend.global.security.jwt.dto;

import java.time.Duration;

import javax.crypto.SecretKey;

public record JwtProperties(
	String tokenName,
	SecretKey secretKey,
	Duration expirationTime
) {}
