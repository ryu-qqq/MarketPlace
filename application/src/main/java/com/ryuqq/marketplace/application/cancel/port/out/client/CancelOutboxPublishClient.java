package com.ryuqq.marketplace.application.cancel.port.out.client;

/** 취소 아웃박스 SQS 발행 클라이언트 포트. */
public interface CancelOutboxPublishClient {

    /**
     * SQS 메시지 발행.
     *
     * @param messageBody JSON 직렬화된 메시지 본문
     * @return SQS 메시지 ID
     */
    String publish(String messageBody);
}
