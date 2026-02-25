package com.ryuqq.marketplace.domain.inboundproduct.vo;

/**
 * InboundProduct 상태 enum.
 *
 * <p>상태 머신:
 *
 * <pre>
 * RECEIVED → MAPPED                  (매핑 성공)
 * RECEIVED → PENDING_MAPPING         (매핑 실패)
 * PENDING_MAPPING → MAPPED           (매핑 등록 후 재처리)
 * MAPPED → PENDING_CONVERSION        (비동기 변환 대기)
 * PENDING_CONVERSION → CONVERTED     (ProductGroup 생성 완료)
 * PENDING_CONVERSION → CONVERT_FAILED (변환 실패)
 * CONVERT_FAILED → PENDING_CONVERSION (재시도)
 * PENDING_CONVERSION → PERMANENTLY_FAILED (복구 불가능한 페이로드 오류)
 * CONVERT_FAILED → PERMANENTLY_FAILED    (재시도 중 복구 불가능 판정)
 * CONVERTED → MAPPED                 (재수신으로 변경 감지 시)
 * LEGACY_IMPORTED → CONVERTED        (배치 전환 완료)
 * </pre>
 */
public enum InboundProductStatus {
    RECEIVED("수신"),
    PENDING_MAPPING("매핑 대기"),
    MAPPED("매핑 완료"),
    PENDING_CONVERSION("변환 대기"),
    CONVERTED("변환 완료"),
    CONVERT_FAILED("변환 실패"),
    PERMANENTLY_FAILED("영구 실패"),
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

    public boolean isPendingConversion() {
        return this == PENDING_CONVERSION;
    }

    public boolean isConverted() {
        return this == CONVERTED;
    }

    public boolean isConvertFailed() {
        return this == CONVERT_FAILED;
    }

    public boolean isPermanentlyFailed() {
        return this == PERMANENTLY_FAILED;
    }

    public boolean isLegacyImported() {
        return this == LEGACY_IMPORTED;
    }

    public boolean isReadyForConversion() {
        return this == PENDING_CONVERSION;
    }

    /** MAPPED 또는 CONVERT_FAILED 상태에서 PENDING_CONVERSION으로 전이 가능. */
    public boolean canMarkPendingConversion() {
        return this == MAPPED || this == CONVERT_FAILED;
    }

    public boolean canApplyMapping() {
        return this == RECEIVED || this == PENDING_MAPPING;
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
