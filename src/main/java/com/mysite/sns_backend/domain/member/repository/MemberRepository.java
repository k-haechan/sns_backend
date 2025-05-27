package com.mysite.sns_backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sns_backend.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByPhone(String phone);
}
