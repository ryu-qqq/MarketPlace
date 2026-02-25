package com.ryuqq.marketplace.application.legacyproduct.port.out.query;

import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite;
import java.util.Optional;

/**
 * 세토프 DB 상품그룹 조회 Port.
 *
 * <p>LEGACY_IMPORTED 폴백 경로 및 배치 전환에서 세토프 DB의 상품 데이터를 읽습니다.
 *
 * <p>전환기: persistence-mysql-legacy 모듈이 구현 (세토프 DB 직접 조회)
 *
 * <p>최종: 제거 예정
 */
public interface SetofProductGroupQueryPort {

    /**
     * 세토프 상품그룹 ID로 전체 복합 데이터 조회.
     *
     * @param productGroupId 세토프 product_group.PRODUCT_GROUP_ID
     * @return 상품그룹 + 상품 + 옵션 + 재고 + 이미지 + 공지 + 배송 복합 데이터
     */
    Optional<SetofProductGroupComposite> findByProductGroupId(long productGroupId);
}
