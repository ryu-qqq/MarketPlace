package com.ryuqq.marketplace.application.inboundqna.dto.result;

/** QnA 웹훅 수신 결과. */
public record QnaWebhookResult(int total, int created, int duplicated, int failed) {

    public static QnaWebhookResult of(int total, int created, int duplicated, int failed) {
        return new QnaWebhookResult(total, created, duplicated, failed);
    }
}
