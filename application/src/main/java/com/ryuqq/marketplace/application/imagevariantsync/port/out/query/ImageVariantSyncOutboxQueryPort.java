package com.ryuqq.marketplace.application.imagevariantsync.port.out.query;

import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import java.util.List;

/**
 * ImageVariantSyncOutbox Query Port.
 *
 * <p>이미지 Variant Sync Outbox 조회를 위한 포트입니다.
 */
public interface ImageVariantSyncOutboxQueryPort {

    /**
     * PENDING 상태의 Outbox 목록 조회 (스케줄러용).
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageVariantSyncOutbox> findPendingOutboxes(int limit);

    /**
     * 해당 sourceImageId에 PENDING 상태의 Outbox가 존재하는지 확인합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @return PENDING Outbox 존재 여부
     */
    boolean existsPendingBySourceImageId(long sourceImageId);
}
