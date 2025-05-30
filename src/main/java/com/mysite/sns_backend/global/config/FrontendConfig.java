package com.mysite.sns_backend.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class FrontendConfig {
	// 프론트엔드 URL
	@Value("${custom.frontend.urls}")
	private List<String> urls;

	public String getPrimaryUrl() {
		return urls.getFirst();
	}
}
