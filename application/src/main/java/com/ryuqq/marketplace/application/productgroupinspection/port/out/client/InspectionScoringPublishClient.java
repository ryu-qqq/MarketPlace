package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

/**
 * Scoring 큐 메시지 발행 클라이언트.
 *
 * <p>Outbox Relay에서 Scoring 단계 시작 시 사용합니다. 큐 URL은 어댑터 구현체에서 관리합니다.
 */
public interface InspectionScoringPublishClient {

    String publish(String messageBody);
}
