package com.ryuqq.marketplace.application.legacy.seller.port.out;

import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.util.Optional;

/**
 * 레거시 셀러 Composition 조회 Port.
 *
 * <p>레거시 스키마(luxurydb)에서 셀러 복합 정보를 조회합니다. 반환 타입은 표준 {@link SellerAdminCompositeResult}이며, 레거시 테이블
 * 구조를 표준 객체로 매핑하는 책임은 adapter-out에 있습니다.
 */
public interface LegacySellerCompositionQueryPort {

    /**
     * 레거시 셀러 ID로 Admin용 셀러 Composite 조회.
     *
     * @param sellerId 레거시 셀러 ID (luxurydb seller.seller_id)
     * @return Admin용 셀러 Composite 결과
     */
    Optional<SellerAdminCompositeResult> findAdminCompositeById(long sellerId);
}
