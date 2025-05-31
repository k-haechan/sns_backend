package com.mysite.sns_backend.domain.member.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import com.mysite.sns_backend.domain.member.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시용 기본 생성자
@EntityListeners(AuditingEntityListener.class)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(unique = true)
	private String email; // 인증 전에는 null

	@Column(unique = true)
	private String phone; // 인증 전에는 null

	private String profileImagePath;

	private String profileMessage;

	@Column(nullable = false)
	private boolean open = true; // 기본 공개(true)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Member(String username, String name, String password, String email, String phone, Role role) {
		this.username = username;
		this.name = name;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.role = role;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.getAuthorities();
	}
}
