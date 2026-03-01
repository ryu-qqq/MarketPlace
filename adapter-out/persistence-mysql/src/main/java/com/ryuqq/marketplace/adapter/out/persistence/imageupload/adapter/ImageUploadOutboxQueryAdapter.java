package com.ryuqq.marketplace.adapter.out.persistence.imageupload.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper.ImageUploadOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository.ImageUploadOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.imageupload.port.out.query.ImageUploadOutboxQueryPort;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageUploadOutboxQueryAdapter - 이미지 업로드 Outbox 조회 어댑터.
 *
 * <p>ImageUploadOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageUploadOutboxQueryAdapter implements ImageUploadOutboxQueryPort {

    private final ImageUploadOutboxQueryDslRepository queryDslRepository;
    private final ImageUploadOutboxJpaEntityMapper mapper;

    public ImageUploadOutboxQueryAdapter(
            ImageUploadOutboxQueryDslRepository queryDslRepository,
            ImageUploadOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ImageUploadOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageUploadOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageUploadOutbox> findBySourceIdsAndSourceType(
            List<Long> sourceIds, ImageSourceType sourceType) {
        return queryDslRepository.findBySourceIdsAndSourceType(sourceIds, sourceType).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageUploadOutbox> findProcessingOutboxes(int limit) {
        return queryDslRepository.findProcessingOutboxes(limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageUploadOutbox> findRecoverableFailedOutboxes(Instant failedBefore, int limit) {
        return queryDslRepository.findRecoverableFailedOutboxes(failedBefore, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
