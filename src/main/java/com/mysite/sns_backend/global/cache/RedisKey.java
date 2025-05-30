package com.mysite.sns_backend.global.cache;

/**
 * Redis 키 관리를 위한 유틸 클래스
 * - Redis에 저장하는 키들의 네임스페이스(prefix)를 통일하고 관리
 * - 현재는 블랙리스트 토큰 키 생성에 사용
 */
public final class RedisKey {

	// 블랙리스트 토큰 키 접두사
	private static final String BLACKLIST_PREFIX = "blacklist:";

	// 인스턴스 생성 방지
	private RedisKey() {}

	/**
	 * 블랙리스트에 저장할 Redis 키 생성
	 * @param token 블랙리스트 대상 토큰
	 * @return 접두사가 붙은 Redis 키 문자열
	 */
	public static String blacklistKey(String token) {
		return BLACKLIST_PREFIX + token;
	}
}
