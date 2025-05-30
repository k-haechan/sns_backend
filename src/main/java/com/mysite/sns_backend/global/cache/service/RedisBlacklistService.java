package com.mysite.sns_backend.global.cache.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mysite.sns_backend.global.cache.RedisKey;

/**
 * Redis를 이용한 토큰 블랙리스트 관리 서비스
 * - 로그아웃 시 refresh token을 블랙리스트에 등록하여 재사용 방지
 * - Redis에 토큰 키를 저장하고 TTL(유효시간) 동안 유지
 */
@Service
public class RedisBlacklistService {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisBlacklistService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 토큰을 블랙리스트에 등록
	 * @param token 블랙리스트에 추가할 토큰 문자열
	 * @param ttlSeconds 블랙리스트 유지 시간(초 단위)
	 */
	public void addToBlacklist(String token, long ttlSeconds) {
		redisTemplate.opsForValue()
			.set(RedisKey.blacklistKey(token), "logout", Duration.ofSeconds(ttlSeconds));
	}

	/**
	 * 토큰이 블랙리스트에 있는지 확인
	 * @param token 검사할 토큰 문자열
	 * @return true: 블랙리스트 존재, false: 없음
	 */
	public boolean isBlacklisted(String token) {
		return redisTemplate.hasKey(RedisKey.blacklistKey(token));
	}

	/**
	 * 블랙리스트에서 토큰 제거
	 * @param token 제거할 토큰 문자열
	 */
	public void removeFromBlacklist(String token) {
		redisTemplate.delete(RedisKey.blacklistKey(token));
	}
}
