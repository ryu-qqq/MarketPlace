package com.ryuqq.marketplace.application.legacy.productgroup.port.out.query;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import java.util.Optional;

/**
 * 세토프 DB 상품그룹 Composition 조회 Port.
 *
 * <p>ProductGroup + Image + Description + Notice + Delivery + OptionGroup 크로스 테이블 조인 조회를 제공합니다.
 *
 * <p>전환기: persistence-mysql-legacy 모듈이 구현 (세토프 DB 직접 조회)
 *
 * <p>최종: 제거 예정
 */
public interface LegacyProductGroupCompositionQueryPort {

    /**
     * 세토프 상품그룹 Composite 상세 조회 (Product 제외).
     *
     * <p>ProductGroup + Image + Description + Notice + Delivery + OptionGroup 조인.
     *
     * @param productGroupId 세토프 product_group.PRODUCT_GROUP_ID
     * @return 상품그룹 Composite 결과
     */
    Optional<LegacyProductGroupCompositeResult> findCompositeById(long productGroupId);
}
