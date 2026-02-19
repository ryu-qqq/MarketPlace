package com.ryuqq.marketplace.bootstrap.config;

import org.springframework.context.annotation.Configuration;

/**
 * Core Configuration.
 *
 * <p>Web API 부트스트랩 전용 설정입니다. 공통 인프라 빈(Clock, batchExecutor, FileStorageClient)은
 * CommonClientFallbackConfig에서 제공합니다.
 */
@Configuration
public class CoreConfiguration {}
