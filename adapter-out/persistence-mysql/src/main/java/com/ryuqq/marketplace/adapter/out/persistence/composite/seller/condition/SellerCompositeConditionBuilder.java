package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerBusinessInfoJpaEntity.sellerBusinessInfoJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerContractJpaEntity.sellerContractJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerCsJpaEntity.sellerCsJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity.sellerJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerSettlementJpaEntity.sellerSettlementJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerCompositeConditionBuilder - 셀러 Composite QueryDSL 조건 빌더.
 *
 * <p>Seller + SellerBusinessInfo + SellerCs + SellerContract + SellerSettlement 조인 쿼리용 조건.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 조회합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerCompositeConditionBuilder {

    // ===== Seller 조건 =====

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerJpaEntity.id.eq(sellerId) : null;
    }

    public BooleanExpression sellerIdIn(List<Long> sellerIds) {
        return sellerIds != null && !sellerIds.isEmpty() ? sellerJpaEntity.id.in(sellerIds) : null;
    }

    public BooleanExpression sellerNotDeleted() {
        return sellerJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression sellerActiveEq(Boolean active) {
        return active != null ? sellerJpaEntity.active.eq(active) : null;
    }

    // ===== BusinessInfo 조건 =====

    public BooleanExpression businessInfoSellerIdEq() {
        return sellerBusinessInfoJpaEntity.sellerId.eq(sellerJpaEntity.id);
    }

    public BooleanExpression businessInfoNotDeleted() {
        return sellerBusinessInfoJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression businessInfoJoinCondition() {
        return businessInfoSellerIdEq().and(businessInfoNotDeleted());
    }

    // ===== Cs 조건 =====

    public BooleanExpression csSellerIdEq() {
        return sellerCsJpaEntity.sellerId.eq(sellerJpaEntity.id);
    }

    public BooleanExpression csNotDeleted() {
        return sellerCsJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression csJoinCondition() {
        return csSellerIdEq().and(csNotDeleted());
    }

    // ===== Contract 조건 =====

    public BooleanExpression contractSellerIdEq() {
        return sellerContractJpaEntity.sellerId.eq(sellerJpaEntity.id);
    }

    public BooleanExpression contractNotDeleted() {
        return sellerContractJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression contractJoinCondition() {
        return contractSellerIdEq().and(contractNotDeleted());
    }

    // ===== Settlement 조건 =====

    public BooleanExpression settlementSellerIdEq() {
        return sellerSettlementJpaEntity.sellerId.eq(sellerJpaEntity.id);
    }

    public BooleanExpression settlementNotDeleted() {
        return sellerSettlementJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression settlementJoinCondition() {
        return settlementSellerIdEq().and(settlementNotDeleted());
    }
}
