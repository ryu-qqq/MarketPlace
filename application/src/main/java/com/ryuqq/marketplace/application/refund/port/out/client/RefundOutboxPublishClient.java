package com.ryuqq.marketplace.application.refund.port.out.client;

/** 환불 아웃박스 SQS 발행 클라이언트 포트. */
public interface RefundOutboxPublishClient {

    /**
     * SQS 메시지 발행.
     *
     * @param messageBody JSON 직렬화된 메시지 본문
     * @return SQS 메시지 ID
     */
    String publish(String messageBody);
}
