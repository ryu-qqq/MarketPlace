package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity.productJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.QProductGroupImageJpaEntity.productGroupImageJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OMS 상품 enrichment 조회 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class OmsProductEnrichmentConditionBuilder {

    public BooleanExpression imageProductGroupIdIn(List<Long> pgIds) {
        return productGroupImageJpaEntity.productGroupId.in(pgIds);
    }

    public BooleanExpression imageThumbnailType() {
        return productGroupImageJpaEntity.imageType.eq(ImageType.THUMBNAIL.name());
    }

    public BooleanExpression imageNotDeleted() {
        return productGroupImageJpaEntity.deleted.isFalse();
    }

    public BooleanExpression productProductGroupIdIn(List<Long> pgIds) {
        return productJpaEntity.productGroupId.in(pgIds);
    }

    public BooleanExpression syncProductGroupIdIn(List<Long> pgIds) {
        return outboundSyncOutboxJpaEntity.productGroupId.in(pgIds);
    }
}
