package com.ryuqq.marketplace.application.inboundorder.dto.result;

/**
 * 인바운드 주문 폴링 결과.
 *
 * @param total 전체 수신 건수
 * @param created 변환 완료 건수
 * @param pending 매핑 대기 건수
 * @param duplicated 중복 건수
 * @param failed 실패 건수
 */
public record InboundOrderPollingResult(
        int total, int created, int pending, int duplicated, int failed) {

    public static InboundOrderPollingResult of(
            int total, int created, int pending, int duplicated, int failed) {
        return new InboundOrderPollingResult(total, created, pending, duplicated, failed);
    }

    public static InboundOrderPollingResult empty() {
        return new InboundOrderPollingResult(0, 0, 0, 0, 0);
    }

    public InboundOrderPollingResult merge(InboundOrderPollingResult other) {
        return new InboundOrderPollingResult(
                this.total + other.total,
                this.created + other.created,
                this.pending + other.pending,
                this.duplicated + other.duplicated,
                this.failed + other.failed);
    }
}
