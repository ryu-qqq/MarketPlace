package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

/**
 * Verification 큐 메시지 발행 클라이언트.
 *
 * <p>Scoring 통과 또는 Enhancement 완료 후 Verification 단계로 전달할 때 사용합니다. 큐 URL은 어댑터 구현체에서 관리합니다.
 */
public interface InspectionVerificationPublishClient {

    String publish(String messageBody);
}
