package com.ryuqq.marketplace.application.imagetransform.port.out.query;

import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ImageTransformOutbox Query Port.
 *
 * <p>이미지 변환 Outbox 조회를 위한 포트입니다.
 */
public interface ImageTransformOutboxQueryPort {

    /**
     * ID로 Outbox 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox
     * @throws IllegalStateException 존재하지 않는 경우
     */
    ImageTransformOutbox getById(Long outboxId);

    /**
     * PENDING 상태의 Outbox 목록 조회 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageTransformOutbox> findPendingOutboxes(Instant beforeTime, int limit);

    /**
     * PROCESSING 상태의 Outbox 목록 조회 (폴링 스케줄러용).
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageTransformOutbox> findProcessingOutboxes(int limit);

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회 (타임아웃 복구 스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageTransformOutbox> findProcessingTimeoutOutboxes(Instant timeoutThreshold, int limit);

    /**
     * 주어진 소스 이미지 ID 목록과 Variant 타입 목록에 대해 활성(PENDING/PROCESSING) 상태의 Outbox가 존재하는 (sourceImageId,
     * variantType) 쌍을 반환합니다.
     *
     * @param sourceImageIds 소스 이미지 ID 목록
     * @param variantTypes Variant 타입 목록
     * @return sourceImageId별 활성 상태인 Variant 타입 Set
     */
    Map<Long, Set<ImageVariantType>> findActiveVariantTypesBySourceImageIds(
            List<Long> sourceImageIds, List<ImageVariantType> variantTypes);
}
