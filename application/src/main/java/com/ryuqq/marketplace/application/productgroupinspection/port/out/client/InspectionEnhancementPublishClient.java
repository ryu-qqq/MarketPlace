package com.ryuqq.marketplace.application.productgroupinspection.port.out.client;

/**
 * Enhancement 큐 메시지 발행 클라이언트.
 *
 * <p>Scoring 점수 미달 시 Enhancement 단계로 전달할 때 사용합니다. 큐 URL은 어댑터 구현체에서 관리합니다.
 */
public interface InspectionEnhancementPublishClient {

    String publish(String messageBody);
}
