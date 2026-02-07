package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.QSellerAddressJpaEntity.sellerAddressJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAddressConditionBuilder - 셀러 주소 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerAddressConditionBuilder {

    public BooleanExpression idEq(Long id) {
        return id != null ? sellerAddressJpaEntity.id.eq(id) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerAddressJpaEntity.sellerId.eq(sellerId) : null;
    }

    /** 복합 조회: 셀러 ID 목록 IN 조건 */
    public BooleanExpression sellerIdIn(List<Long> sellerIds) {
        return sellerIds != null && !sellerIds.isEmpty()
                ? sellerAddressJpaEntity.sellerId.in(sellerIds)
                : null;
    }

    public BooleanExpression addressTypeEq(String addressType) {
        return addressType != null ? sellerAddressJpaEntity.addressType.eq(addressType) : null;
    }

    /** 복합 조회: 주소 유형 목록 IN 조건 */
    public BooleanExpression addressTypeIn(List<String> addressTypes) {
        return addressTypes != null && !addressTypes.isEmpty()
                ? sellerAddressJpaEntity.addressType.in(addressTypes)
                : null;
    }

    public BooleanExpression defaultAddressEq(Boolean defaultAddress) {
        return defaultAddress != null
                ? sellerAddressJpaEntity.defaultAddress.eq(defaultAddress)
                : null;
    }

    /** 배송지 이름 정확 일치 (중복 검사용). */
    public BooleanExpression addressNameEq(String addressName) {
        if (addressName == null || addressName.isBlank()) {
            return null;
        }
        return sellerAddressJpaEntity.addressName.eq(addressName.trim());
    }

    public BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return sellerAddressJpaEntity
                .addressName
                .containsIgnoreCase(keyword)
                .or(sellerAddressJpaEntity.address.containsIgnoreCase(keyword));
    }
}
