package com.ryuqq.marketplace.adapter.out.persistence.imageupload.adapter;

import static com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupImageJpaEntity.productGroupImageJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QDescriptionImageJpaEntity.descriptionImageJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadedUrlUpdatePort;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import org.springframework.stereotype.Component;

/**
 * ImageUploadedUrlUpdateAdapter - 이미지 uploaded_url 업데이트 어댑터.
 *
 * <p>ImageUploadedUrlUpdatePort를 구현하여 sourceType별로 대상 테이블의 uploaded_url을 업데이트합니다.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 */
@Component
public class ImageUploadedUrlUpdateAdapter implements ImageUploadedUrlUpdatePort {

    private final JPAQueryFactory queryFactory;

    public ImageUploadedUrlUpdateAdapter(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public void updateUploadedUrl(ImageSourceType sourceType, Long sourceId, String uploadedUrl) {
        switch (sourceType) {
            case PRODUCT_GROUP_IMAGE ->
                    queryFactory
                            .update(productGroupImageJpaEntity)
                            .set(productGroupImageJpaEntity.uploadedUrl, uploadedUrl)
                            .where(productGroupImageJpaEntity.id.eq(sourceId))
                            .execute();
            case DESCRIPTION_IMAGE ->
                    queryFactory
                            .update(descriptionImageJpaEntity)
                            .set(descriptionImageJpaEntity.uploadedUrl, uploadedUrl)
                            .where(descriptionImageJpaEntity.id.eq(sourceId))
                            .execute();
        }
    }
}
