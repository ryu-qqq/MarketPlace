package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper.ImageVariantJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository.ImageVariantQueryDslRepository;
import com.ryuqq.marketplace.application.imagevariant.port.out.query.ImageVariantQueryPort;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageVariantQueryAdapter - 이미지 Variant 조회 어댑터.
 *
 * <p>ImageVariantQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageVariantQueryAdapter implements ImageVariantQueryPort {

    private final ImageVariantQueryDslRepository queryDslRepository;
    private final ImageVariantJpaEntityMapper mapper;

    public ImageVariantQueryAdapter(
            ImageVariantQueryDslRepository queryDslRepository, ImageVariantJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ImageVariant> findBySourceImageId(Long sourceImageId, ImageSourceType sourceType) {
        return queryDslRepository.findBySourceImageId(sourceImageId, sourceType).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageVariant> findBySourceImageIds(
            List<Long> sourceImageIds, ImageSourceType sourceType) {
        return queryDslRepository.findBySourceImageIds(sourceImageIds, sourceType).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
