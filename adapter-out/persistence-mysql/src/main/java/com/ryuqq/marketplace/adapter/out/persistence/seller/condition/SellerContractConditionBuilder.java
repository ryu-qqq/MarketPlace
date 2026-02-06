package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerContractJpaEntity.sellerContractJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity.ContractStatusJpaValue;
import org.springframework.stereotype.Component;

/**
 * SellerContractConditionBuilder - 셀러 계약 정보 QueryDSL 조건 빌더.
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerContractConditionBuilder {

    /**
     * ID 일치 조건.
     *
     * @param id 계약 ID
     * @return ID 일치 조건
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? sellerContractJpaEntity.id.eq(id) : null;
    }

    /**
     * 셀러 ID 일치 조건.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 ID 일치 조건
     */
    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerContractJpaEntity.sellerId.eq(sellerId) : null;
    }

    /**
     * 계약 상태 일치 조건.
     *
     * @param status 계약 상태
     * @return 상태 일치 조건
     */
    public BooleanExpression statusEq(ContractStatusJpaValue status) {
        return status != null ? sellerContractJpaEntity.status.eq(status) : null;
    }
}
