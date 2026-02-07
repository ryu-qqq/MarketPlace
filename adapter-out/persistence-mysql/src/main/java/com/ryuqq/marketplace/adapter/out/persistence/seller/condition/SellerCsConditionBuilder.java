package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerCsJpaEntity.sellerCsJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/**
 * SellerCsConditionBuilder - 셀러 CS 정보 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerCsConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id CS ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerCsJpaEntity.id.eq(id) : null;
    }

    /**
     * 셀러 ID 일치 조건.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 ID 일치 조건
     */
    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerCsJpaEntity.sellerId.eq(sellerId) : null;
    }
}
