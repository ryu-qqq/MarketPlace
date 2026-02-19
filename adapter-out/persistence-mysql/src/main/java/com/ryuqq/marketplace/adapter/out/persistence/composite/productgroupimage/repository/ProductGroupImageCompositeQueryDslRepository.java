package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.QImageUploadOutboxJpaEntity.imageUploadOutboxJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.QProductGroupImageJpaEntity.productGroupImageJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ImageOutboxProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ImageProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDto;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * ProductGroupImageCompositeQueryDslRepository - 이미지 + 아웃박스 Composite 조회 Repository.
 *
 * <p>product_group_images + image_upload_outboxes 크로스 도메인 조회.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ProductGroupImageCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ProductGroupImageCompositeQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public ProductGroupImageCompositeDto findByProductGroupId(Long productGroupId) {
        List<ImageProjectionDto> images = fetchImages(productGroupId);
        List<Long> imageIds = images.stream().map(ImageProjectionDto::imageId).toList();
        List<ImageOutboxProjectionDto> outboxes =
                imageIds.isEmpty() ? List.of() : fetchOutboxes(imageIds);

        return new ProductGroupImageCompositeDto(productGroupId, images, outboxes);
    }

    private List<ImageProjectionDto> fetchImages(Long productGroupId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ImageProjectionDto.class,
                                productGroupImageJpaEntity.id,
                                productGroupImageJpaEntity.imageType,
                                productGroupImageJpaEntity.originUrl,
                                productGroupImageJpaEntity.uploadedUrl))
                .from(productGroupImageJpaEntity)
                .where(
                        productGroupImageJpaEntity.productGroupId.eq(productGroupId),
                        productGroupImageJpaEntity.deleted.isFalse())
                .fetch();
    }

    private List<ImageOutboxProjectionDto> fetchOutboxes(List<Long> imageIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ImageOutboxProjectionDto.class,
                                imageUploadOutboxJpaEntity.sourceId,
                                imageUploadOutboxJpaEntity.status.stringValue(),
                                imageUploadOutboxJpaEntity.retryCount,
                                imageUploadOutboxJpaEntity.errorMessage))
                .from(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.sourceId.in(imageIds),
                        imageUploadOutboxJpaEntity.sourceType.eq(
                                ImageSourceType.PRODUCT_GROUP_IMAGE))
                .fetch();
    }
}
