package com.ryuqq.marketplace.adapter.in.sqs.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SQS Consumer Configuration.
 *
 * <p>Spring Cloud AWS SQS 기반 리스너 설정입니다. SqsAsyncClient 및 메시지 리스너 팩토리는 Spring Cloud AWS
 * auto-configuration에 의해 자동 생성됩니다.
 */
@Configuration
@EnableConfigurationProperties(SqsConsumerProperties.class)
public class SqsConsumerConfig {}
