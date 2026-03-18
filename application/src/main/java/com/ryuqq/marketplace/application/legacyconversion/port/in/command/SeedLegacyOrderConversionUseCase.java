package com.ryuqq.marketplace.application.legacyconversion.port.in.command;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyOrderConversionResult;

/**
 * 레거시 주문 벌크 변환 시딩 유스케이스.
 *
 * <p>luxurydb의 활성 주문에 대해 PENDING outbox 엔트리를 시딩합니다.
 */
public interface SeedLegacyOrderConversionUseCase {

    /**
     * 레거시 주문 ID를 커서 기반으로 스캔하여 PENDING outbox 엔트리를 생성합니다.
     *
     * @param cursorAfterOrderId 이 ID 이후부터 스캔 (exclusive)
     * @param batchSize 한 번에 처리할 최대 개수
     * @return 시딩 결과 (스캔/생성/건너뛴 개수, 마지막 커서, 완료 여부)
     */
    SeedLegacyOrderConversionResult execute(long cursorAfterOrderId, int batchSize);
}
