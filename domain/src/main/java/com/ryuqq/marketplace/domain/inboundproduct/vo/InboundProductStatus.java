package com.ryuqq.marketplace.domain.inboundproduct.vo;

/**
 * InboundProduct 상태 enum.
 *
 * <p>상태 머신:
 *
 * <pre>
 * RECEIVED → MAPPED              (매핑 성공)
 * RECEIVED → PENDING_MAPPING     (매핑 실패)
 * PENDING_MAPPING → MAPPED       (매핑 등록 후 재처리)
 * MAPPED → CONVERTED             (ProductGroup 생성/수정 완료)
 * MAPPED → CONVERT_FAILED        (변환 실패)
 * CONVERTED → MAPPED             (재수신으로 변경 감지 시)
 * </pre>
 */
public enum InboundProductStatus {
    RECEIVED("수신"),
    PENDING_MAPPING("매핑 대기"),
    MAPPED("매핑 완료"),
    CONVERTED("변환 완료"),
    CONVERT_FAILED("변환 실패");

    private final String description;

    InboundProductStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isReceived() {
        return this == RECEIVED;
    }

    public boolean isPendingMapping() {
        return this == PENDING_MAPPING;
    }

    public boolean isMapped() {
        return this == MAPPED;
    }

    public boolean isConverted() {
        return this == CONVERTED;
    }

    public boolean isConvertFailed() {
        return this == CONVERT_FAILED;
    }

    public boolean isReadyForConversion() {
        return this == MAPPED;
    }

    public boolean canApplyMapping() {
        return this == RECEIVED || this == PENDING_MAPPING;
    }

    public static InboundProductStatus fromString(String status) {
        return InboundProductStatus.valueOf(status);
    }
}
