package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.adapter;

import static com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.QImageTransformOutboxJpaEntity.imageTransformOutboxJpaEntity;

import com.querydsl.core.Tuple;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.mapper.ImageTransformOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository.ImageTransformOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.imagetransform.port.out.query.ImageTransformOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * ImageTransformOutboxQueryAdapter - 이미지 변환 Outbox 조회 어댑터.
 *
 * <p>ImageTransformOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageTransformOutboxQueryAdapter implements ImageTransformOutboxQueryPort {

    private final ImageTransformOutboxQueryDslRepository queryDslRepository;
    private final ImageTransformOutboxJpaEntityMapper mapper;

    public ImageTransformOutboxQueryAdapter(
            ImageTransformOutboxQueryDslRepository queryDslRepository,
            ImageTransformOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public ImageTransformOutbox getById(Long outboxId) {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        ImageTransformOutboxJpaEntity entity = queryDslRepository.findById(outboxId);
        if (entity == null) {
            throw new IllegalStateException(
                    "ImageTransformOutbox를 찾을 수 없습니다. outboxId=" + outboxId);
        }
        return mapper.toDomain(entity);
    }

    @Override
    public List<ImageTransformOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageTransformOutbox> findProcessingOutboxes(int limit) {
        return queryDslRepository.findProcessingOutboxes(limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ImageTransformOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ImageTransformOutbox> findProcessingByTransformRequestId(
            String transformRequestId) {
        return queryDslRepository
                .findProcessingByTransformRequestId(transformRequestId)
                .map(mapper::toDomain);
    }

    @Override
    public Map<Long, Set<ImageVariantType>> findActiveVariantTypesBySourceImageIds(
            List<Long> sourceImageIds, List<ImageVariantType> variantTypes) {
        List<Tuple> tuples = queryDslRepository.findActiveOutboxPairs(sourceImageIds, variantTypes);

        Map<Long, Set<ImageVariantType>> result = new HashMap<>();
        for (Tuple tuple : tuples) {
            Long sourceImageId = tuple.get(imageTransformOutboxJpaEntity.sourceImageId);
            ImageVariantType domainType = tuple.get(imageTransformOutboxJpaEntity.variantType);

            result.computeIfAbsent(sourceImageId, k -> new HashSet<>()).add(domainType);
        }
        return result;
    }
}
