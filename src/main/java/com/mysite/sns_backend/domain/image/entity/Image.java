package com.mysite.sns_backend.domain.image.entity;

import com.mysite.sns_backend.domain.post.entity.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시용 기본 생성자
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_image_post"))
	private Post post;

	private String path; // 이미지 경로

	public Image(Post post, String path) {
		this.post = post;
		this.path = path;
	}
}
