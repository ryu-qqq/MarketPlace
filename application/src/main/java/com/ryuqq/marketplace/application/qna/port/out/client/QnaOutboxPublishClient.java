package com.ryuqq.marketplace.application.qna.port.out.client;

/** QnA 아웃박스 SQS 발행 클라이언트 포트. */
public interface QnaOutboxPublishClient {
    void publish(String messageBody);
}
