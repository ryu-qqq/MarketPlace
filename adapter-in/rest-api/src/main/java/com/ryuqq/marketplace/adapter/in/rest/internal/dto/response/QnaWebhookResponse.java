package com.ryuqq.marketplace.adapter.in.rest.internal.dto.response;

import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;

/** QnA 웹훅 수신 응답. */
public record QnaWebhookResponse(int total, int created, int duplicated, int failed) {

    public static QnaWebhookResponse from(QnaWebhookResult result) {
        return new QnaWebhookResponse(
                result.total(), result.created(), result.duplicated(), result.failed());
    }
}
