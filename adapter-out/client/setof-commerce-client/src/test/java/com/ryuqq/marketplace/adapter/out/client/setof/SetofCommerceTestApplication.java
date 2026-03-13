package com.ryuqq.marketplace.adapter.out.client.setof;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 세토프 커머스 클라이언트 통합 테스트용 경량 애플리케이션.
 *
 * <p>세토프 API 관련 빈만 로드합니다 (DB, 기타 인프라 불필요).
 */
@SpringBootApplication(scanBasePackages = "com.ryuqq.marketplace.adapter.out.client.setof")
public class SetofCommerceTestApplication {}
