package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.QImageUploadOutboxJpaEntity.imageUploadOutboxJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QDescriptionImageJpaEntity.descriptionImageJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QProductGroupDescriptionJpaEntity.productGroupDescriptionJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionImageOutboxProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionImageProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionProjectionDto;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductGroupDescriptionCompositeQueryDslRepository - 상세설명 + 이미지 + 아웃박스 Composite 조회 Repository.
 *
 * <p>product_group_descriptions + description_images + image_upload_outboxes 크로스 도메인 조회.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 */
@Repository
public class ProductGroupDescriptionCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public ProductGroupDescriptionCompositeQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<DescriptionCompositeDto> findByProductGroupId(Long productGroupId) {
        DescriptionProjectionDto description = fetchDescription(productGroupId);
        if (description == null) {
            return Optional.empty();
        }

        List<DescriptionImageProjectionDto> images =
                fetchDescriptionImages(description.descriptionId());
        List<Long> imageIds = images.stream().map(DescriptionImageProjectionDto::imageId).toList();
        List<DescriptionImageOutboxProjectionDto> outboxes =
                imageIds.isEmpty() ? List.of() : fetchOutboxes(imageIds);

        return Optional.of(
                new DescriptionCompositeDto(productGroupId, description, images, outboxes));
    }

    private DescriptionProjectionDto fetchDescription(Long productGroupId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                DescriptionProjectionDto.class,
                                productGroupDescriptionJpaEntity.id,
                                productGroupDescriptionJpaEntity.publishStatus,
                                productGroupDescriptionJpaEntity.cdnPath))
                .from(productGroupDescriptionJpaEntity)
                .where(productGroupDescriptionJpaEntity.productGroupId.eq(productGroupId))
                .fetchOne();
    }

    private List<DescriptionImageProjectionDto> fetchDescriptionImages(Long descriptionId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                DescriptionImageProjectionDto.class,
                                descriptionImageJpaEntity.id,
                                descriptionImageJpaEntity.originUrl,
                                descriptionImageJpaEntity.uploadedUrl))
                .from(descriptionImageJpaEntity)
                .where(
                        descriptionImageJpaEntity.productGroupDescriptionId.eq(descriptionId),
                        descriptionImageJpaEntity.deleted.isFalse())
                .fetch();
    }

    private List<DescriptionImageOutboxProjectionDto> fetchOutboxes(List<Long> imageIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                DescriptionImageOutboxProjectionDto.class,
                                imageUploadOutboxJpaEntity.sourceId,
                                imageUploadOutboxJpaEntity.status.stringValue(),
                                imageUploadOutboxJpaEntity.retryCount,
                                imageUploadOutboxJpaEntity.errorMessage))
                .from(imageUploadOutboxJpaEntity)
                .where(
                        imageUploadOutboxJpaEntity.sourceId.in(imageIds),
                        imageUploadOutboxJpaEntity.sourceType.eq(ImageSourceType.DESCRIPTION_IMAGE))
                .fetch();
    }
}
