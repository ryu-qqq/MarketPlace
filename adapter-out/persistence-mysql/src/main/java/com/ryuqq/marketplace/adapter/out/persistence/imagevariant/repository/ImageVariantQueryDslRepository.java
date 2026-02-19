package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.QImageVariantJpaEntity.imageVariantJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ImageVariantQueryDslRepository - 이미지 Variant QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ImageVariantQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ImageVariantQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 소스 이미지 ID 목록과 소스 타입으로 Variant 목록 조회.
     *
     * @param sourceImageIds 소스 이미지 ID 목록
     * @param sourceType 이미지 소스 타입
     * @return Variant 목록
     */
    public List<ImageVariantJpaEntity> findBySourceImageIds(
            List<Long> sourceImageIds, ImageSourceType sourceType) {
        if (sourceImageIds == null || sourceImageIds.isEmpty()) {
            return Collections.emptyList();
        }
        return queryFactory
                .selectFrom(imageVariantJpaEntity)
                .where(
                        imageVariantJpaEntity.sourceImageId.in(sourceImageIds),
                        imageVariantJpaEntity.sourceType.eq(sourceType))
                .orderBy(
                        imageVariantJpaEntity.sourceImageId.asc(),
                        imageVariantJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 단일 소스 이미지 ID와 소스 타입으로 Variant 목록 조회.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @return Variant 목록
     */
    public List<ImageVariantJpaEntity> findBySourceImageId(
            Long sourceImageId, ImageSourceType sourceType) {
        return queryFactory
                .selectFrom(imageVariantJpaEntity)
                .where(
                        imageVariantJpaEntity.sourceImageId.eq(sourceImageId),
                        imageVariantJpaEntity.sourceType.eq(sourceType))
                .orderBy(imageVariantJpaEntity.createdAt.desc())
                .fetch();
    }
}
