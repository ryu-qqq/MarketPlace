package com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.entity.ImageVariantSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.mapper.ImageVariantSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariantsync.repository.ImageVariantSyncOutboxJpaRepository;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.command.ImageVariantSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import org.springframework.stereotype.Component;

/**
 * ImageVariantSyncOutboxCommandAdapter - 이미지 Variant Sync Outbox 명령 어댑터.
 *
 * <p>ImageVariantSyncOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageVariantSyncOutboxCommandAdapter implements ImageVariantSyncOutboxCommandPort {

    private final ImageVariantSyncOutboxJpaRepository repository;
    private final ImageVariantSyncOutboxJpaEntityMapper mapper;

    public ImageVariantSyncOutboxCommandAdapter(
            ImageVariantSyncOutboxJpaRepository repository,
            ImageVariantSyncOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ImageVariantSyncOutbox outbox) {
        ImageVariantSyncOutboxJpaEntity entity = mapper.toEntity(outbox);
        ImageVariantSyncOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }
}
