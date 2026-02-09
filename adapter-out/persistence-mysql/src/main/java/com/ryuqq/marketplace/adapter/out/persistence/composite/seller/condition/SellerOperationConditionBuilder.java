package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.QSellerAddressJpaEntity.sellerAddressJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/**
 * SellerOperationConditionBuilder - 셀러 운영 메타데이터 QueryDSL 조건 빌더.
 *
 * <p>SellerAddress 조회 쿼리용 조건.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerOperationConditionBuilder {

    // ===== SellerAddress 조건 =====

    public BooleanExpression addressSellerIdEq(Long sellerId) {
        return sellerId != null ? sellerAddressJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression addressNotDeleted() {
        return sellerAddressJpaEntity.deletedAt.isNull();
    }
}
