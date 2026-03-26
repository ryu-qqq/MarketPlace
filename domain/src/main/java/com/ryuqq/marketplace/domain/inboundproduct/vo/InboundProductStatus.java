package com.ryuqq.marketplace.domain.inboundproduct.vo;

/**
 * InboundProduct 상태 enum.
 *
 * <p>상태 머신 (동기 변환):
 *
 * <pre>
 * RECEIVED → MAPPED                  (매핑 성공)
 * RECEIVED → PENDING_MAPPING         (매핑 실패)
 * PENDING_MAPPING → MAPPED           (매핑 등록 후 재수신)
 * MAPPED → CONVERTED                 (동기 변환 완료)
 * CONVERTED → CONVERTED              (재수신 시 갱신)
 * LEGACY_IMPORTED → MAPPED           (크롤러 재수신 시 매핑 적용)
 * LEGACY_IMPORTED → CONVERTED        (배치 전환 완료)
 * </pre>
 *
 * <p>PENDING_CONVERSION, CONVERT_FAILED는 기존 DB 데이터 호환을 위해 유지합니다.
 */
public enum InboundProductStatus {
    RECEIVED("수신"),
    PENDING_MAPPING("매핑 대기"),
    MAPPED("매핑 완료"),
    PENDING_CONVERSION("변환 대기"),
    CONVERT_FAILED("변환 실패"),
    CONVERTED("변환 완료"),
    LEGACY_IMPORTED("레거시 임포트");

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

    public boolean isLegacyImported() {
        return this == LEGACY_IMPORTED;
    }

    public boolean isPendingConversion() {
        return this == PENDING_CONVERSION;
    }

    public boolean isConvertFailed() {
        return this == CONVERT_FAILED;
    }

    public boolean canApplyMapping() {
        return this == RECEIVED || this == PENDING_MAPPING || this == LEGACY_IMPORTED;
    }

    public boolean canMarkPendingConversion() {
        return this == MAPPED || this == CONVERT_FAILED;
    }

    /** OMS 내부 경로로 처리 가능한 상태인지 확인. */
    public boolean canRouteToInternal() {
        return this == CONVERTED;
    }

    /** 레거시 fallback 경로로 처리해야 하는 상태인지 확인. */
    public boolean requiresLegacyFallback() {
        return this == LEGACY_IMPORTED;
    }

    public static InboundProductStatus fromString(String status) {
        return InboundProductStatus.valueOf(status);
    }
}
