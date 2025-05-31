package com.mysite.sns_backend.global.security.user;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mysite.sns_backend.domain.member.entity.Member;
import com.mysite.sns_backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	/**
	 * 사용자 이름(username)으로 DB에서 사용자 조회 후 UserDetails 반환
	 *
	 * @param username 인증 요청 시 전달된 사용자 이름
	 * @return UserDetails - Spring Security에서 사용하는 인증용 사용자 정보
	 * @throws UsernameNotFoundException 해당 사용자 없으면 예외 발생
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws RuntimeException {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

		// User 객체는 Spring Security 내장 UserDetails 구현체
		return new User(
			member.getUsername(),
			member.getPassword(),
			// 권한 목록 생성 (Member Role을 GrantedAuthority로 변환)
			List.of(new SimpleGrantedAuthority(member.getRole().getKey()))
		);
	}
}
