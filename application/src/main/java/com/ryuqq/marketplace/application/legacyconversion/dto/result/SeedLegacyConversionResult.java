package com.ryuqq.marketplace.application.legacyconversion.dto.result;

/**
 * 레거시 벌크 변환 시딩 결과.
 *
 * @param scanned 스캔한 레거시 ID 수
 * @param created 새로 생성한 Outbox 수
 * @param skipped 이미 존재하여 건너뛴 수
 * @param lastCursor 마지막으로 처리한 레거시 상품그룹 ID (다음 호출의 시작점)
 * @param completed 전체 스캔이 완료되었는지 여부
 */
public record SeedLegacyConversionResult(
        int scanned, int created, int skipped, long lastCursor, boolean completed) {

    public static SeedLegacyConversionResult of(
            int scanned, int created, int skipped, long lastCursor) {
        return new SeedLegacyConversionResult(scanned, created, skipped, lastCursor, false);
    }

    public static SeedLegacyConversionResult allCompleted() {
        return new SeedLegacyConversionResult(0, 0, 0, 0, true);
    }
}
