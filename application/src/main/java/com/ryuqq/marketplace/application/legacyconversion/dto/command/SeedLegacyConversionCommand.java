package com.ryuqq.marketplace.application.legacyconversion.dto.command;

/**
 * 레거시 벌크 변환 시딩 커맨드.
 *
 * @param batchSize 한 번에 스캔할 ID 개수
 * @param maxTotal 전체 시딩 제한 (0 = 무제한)
 * @param cursorAfterProductGroupId 커서 기반 스캔 시작점 (이 ID 이후부터 조회)
 */
public record SeedLegacyConversionCommand(
        int batchSize, int maxTotal, long cursorAfterProductGroupId) {

    public static SeedLegacyConversionCommand of(int batchSize, int maxTotal, long cursor) {
        return new SeedLegacyConversionCommand(batchSize, maxTotal, cursor);
    }
}
