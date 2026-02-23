package com.ryuqq.marketplace.adapter.out.client.fileflow.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FileFlow Client Configuration.
 *
 * <p>FileFlowClientProperties를 바인딩합니다. fileflow.base-url 설정이 있을 때만 활성화됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(FileFlowClientProperties.class)
@ConditionalOnProperty(prefix = "fileflow", name = "base-url")
public class FileFlowClientConfig {}
