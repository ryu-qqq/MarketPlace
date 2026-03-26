package com.ryuqq.marketplace.adapter.in.rest.internal.dto.response;

import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;

/**
 * 주문 생성 웹훅 응답.
 *
 * @param total 전체 수신 건수
 * @param created 변환 완료 건수
 * @param pending 매핑 대기 건수
 * @param duplicated 중복 건수
 * @param failed 실패 건수
 */
public record OrderCreatedWebhookResponse(
        int total, int created, int pending, int duplicated, int failed) {

    public static OrderCreatedWebhookResponse from(InboundOrderPollingResult result) {
        return new OrderCreatedWebhookResponse(
                result.total(),
                result.created(),
                result.pending(),
                result.duplicated(),
                result.failed());
    }
}
