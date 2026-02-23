package com.ryuqq.marketplace.application.productintelligence.port.out.client;

/**
 * Intelligence Pipeline SQS 발행 클라이언트.
 *
 * <p>각 분석 큐(description, option, notice)와 aggregation 큐로 메시지를 발행합니다. 큐별 Adapter가 이 인터페이스를 구현합니다.
 */
public interface IntelligenceAnalysisPublishClient {

    /** 큐 이름 식별자. */
    String queueName();

    /**
     * 메시지 발행.
     *
     * @param messageBody JSON 직렬화된 메시지 본문
     * @return SQS messageId
     */
    String publish(String messageBody);
}
