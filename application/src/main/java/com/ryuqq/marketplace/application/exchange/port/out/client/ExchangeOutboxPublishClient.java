package com.ryuqq.marketplace.application.exchange.port.out.client;

/** 교환 아웃박스 SQS 발행 클라이언트 포트. */
public interface ExchangeOutboxPublishClient {

    /**
     * SQS 메시지 발행.
     *
     * @param messageBody JSON 직렬화된 메시지 본문
     * @return SQS 메시지 ID
     */
    String publish(String messageBody);
}
