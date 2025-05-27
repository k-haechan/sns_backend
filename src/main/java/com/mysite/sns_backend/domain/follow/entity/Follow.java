package com.mysite.sns_backend.domain.follow.entity;

import com.mysite.sns_backend.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA 프록시용 기본 생성자
public class Follow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 팔로우 ID

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_sender"))
	private Member sender; // 팔로우를 요청하는 사람

	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_receiver"))
	private Member receiver; // 팔로우를 받는 사람

	@Column(nullable = false)
	private Boolean isAccepted; // 수락 여부
}
