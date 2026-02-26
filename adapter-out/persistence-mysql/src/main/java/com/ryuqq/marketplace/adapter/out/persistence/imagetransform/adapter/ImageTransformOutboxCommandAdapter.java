package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.mapper.ImageTransformOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository.ImageTransformOutboxJpaRepository;
import com.ryuqq.marketplace.application.imagetransform.port.out.command.ImageTransformOutboxCommandPort;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import org.springframework.stereotype.Component;

/**
 * ImageTransformOutboxCommandAdapter - 이미지 변환 Outbox 명령 어댑터.
 *
 * <p>ImageTransformOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageTransformOutboxCommandAdapter implements ImageTransformOutboxCommandPort {

    private final ImageTransformOutboxJpaRepository repository;
    private final ImageTransformOutboxJpaEntityMapper mapper;

    public ImageTransformOutboxCommandAdapter(
            ImageTransformOutboxJpaRepository repository,
            ImageTransformOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ImageTransformOutbox outbox) {
        ImageTransformOutboxJpaEntity entity = mapper.toEntity(outbox);
        ImageTransformOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }
}
