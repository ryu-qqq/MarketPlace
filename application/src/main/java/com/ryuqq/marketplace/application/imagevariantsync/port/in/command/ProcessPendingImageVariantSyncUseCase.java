package com.ryuqq.marketplace.application.imagevariantsync.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;

/**
 * PENDING 상태의 이미지 Variant Sync Outbox 처리 UseCase.
 *
 * <p>스케줄러에서 호출되어 PENDING Outbox를 배치 처리합니다.
 */
public interface ProcessPendingImageVariantSyncUseCase {

    /**
     * PENDING Outbox를 배치 처리합니다.
     *
     * @param batchSize 배치 크기
     * @return 배치 처리 결과
     */
    SchedulerBatchProcessingResult execute(int batchSize);
}
