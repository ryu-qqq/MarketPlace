package com.ryuqq.marketplace.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 인증 쿠키 프로퍼티 등록 Configuration.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(AuthCookieProperties.class)
public class AuthCookieConfig {}
