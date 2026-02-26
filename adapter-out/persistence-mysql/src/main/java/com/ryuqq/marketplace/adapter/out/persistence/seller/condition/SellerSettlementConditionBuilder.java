package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerSettlementJpaEntity.sellerSettlementJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/**
 * SellerSettlementConditionBuilder - 셀러 정산 정보 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerSettlementConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 정산 정보 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerSettlementJpaEntity.id.eq(id) : null;
    }

    /**
     * 셀러 ID 일치 조건.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 ID 일치 조건
     */
    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerSettlementJpaEntity.sellerId.eq(sellerId) : null;
    }

    /**
     * 인증 여부 조건.
     *
     * @param verified 인증 여부
     * @return 인증 여부 조건
     */
    public BooleanExpression verifiedEq(Boolean verified) {
        return verified != null ? sellerSettlementJpaEntity.verified.eq(verified) : null;
    }
}
