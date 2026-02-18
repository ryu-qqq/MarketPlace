package com.ryuqq.marketplace.adapter.out.client.ai.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI Client Configuration.
 *
 * <p>spring.ai.openai.api-key 설정이 있을 때만 활성화됩니다. Spring AI 자동 구성이 ChatModel 빈을 생성합니다.
 */
@Configuration
@EnableConfigurationProperties(SpringAiClientProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class SpringAiClientConfig {}
