package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.mapper.ImageVariantSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.repository.ImageVariantSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.query.ImageVariantSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageVariantSyncOutboxQueryAdapter - 이미지 Variant Sync Outbox 조회 어댑터.
 *
 * <p>ImageVariantSyncOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageVariantSyncOutboxQueryAdapter implements ImageVariantSyncOutboxQueryPort {

    private final ImageVariantSyncOutboxQueryDslRepository queryDslRepository;
    private final ImageVariantSyncOutboxJpaEntityMapper mapper;

    public ImageVariantSyncOutboxQueryAdapter(
            ImageVariantSyncOutboxQueryDslRepository queryDslRepository,
            ImageVariantSyncOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ImageVariantSyncOutbox> findPendingOutboxes(int limit) {
        return queryDslRepository.findPendingOutboxes(limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsPendingBySourceImageId(long sourceImageId) {
        return queryDslRepository.existsPendingBySourceImageId(sourceImageId);
    }
}
