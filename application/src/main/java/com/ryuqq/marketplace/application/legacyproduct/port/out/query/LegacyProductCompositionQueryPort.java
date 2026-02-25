package com.ryuqq.marketplace.application.legacyproduct.port.out.query;

import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductCompositeResult;
import java.util.List;

/**
 * 세토프 DB 상품(Product) Composition 조회 Port.
 *
 * <p>Product + ProductOption + OptionGroup + OptionDetail + Stock 조인을 통한 상품 단위 조회를 제공합니다.
 *
 * <p>전환기: persistence-mysql-legacy 모듈이 구현 (세토프 DB 직접 조회)
 *
 * <p>최종: 제거 예정
 */
public interface LegacyProductCompositionQueryPort {

    /**
     * 세토프 상품그룹 ID로 상품+옵션+재고 목록 조회.
     *
     * <p>Product + ProductStock + ProductOption + OptionGroup + OptionDetail 조인.
     *
     * @param productGroupId 세토프 product_group.PRODUCT_GROUP_ID
     * @return 상품 정보 목록
     */
    List<LegacyProductCompositeResult> findProductsByProductGroupId(long productGroupId);
}
