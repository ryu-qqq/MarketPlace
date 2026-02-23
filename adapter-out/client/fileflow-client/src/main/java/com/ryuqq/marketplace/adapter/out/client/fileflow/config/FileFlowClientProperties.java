package com.ryuqq.marketplace.adapter.out.client.fileflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileFlow 클라이언트 커스텀 프로퍼티.
 *
 * <p>SDK AutoConfiguration이 처리하는 base-url, service-name, service-token, timeout 외에 MarketPlace
 * 어댑터에서 추가로 필요한 설정을 바인딩합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "fileflow")
public record FileFlowClientProperties(String cdnDomain, String bucket) {}
