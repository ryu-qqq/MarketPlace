package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper.ImageVariantJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository.ImageVariantJpaRepository;
import com.ryuqq.marketplace.application.imagevariant.port.out.command.ImageVariantCommandPort;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import org.springframework.stereotype.Component;

/**
 * ImageVariantCommandAdapter - 이미지 Variant 명령 어댑터.
 *
 * <p>ImageVariantCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageVariantCommandAdapter implements ImageVariantCommandPort {

    private final ImageVariantJpaRepository repository;
    private final ImageVariantJpaEntityMapper mapper;

    public ImageVariantCommandAdapter(
            ImageVariantJpaRepository repository, ImageVariantJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ImageVariant variant) {
        ImageVariantJpaEntity entity = mapper.toEntity(variant);
        ImageVariantJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
