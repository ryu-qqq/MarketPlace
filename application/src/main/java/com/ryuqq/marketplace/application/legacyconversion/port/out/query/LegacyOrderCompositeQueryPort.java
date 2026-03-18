package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import java.util.Optional;

/**
 * 레거시 주문 복합 조회 Port.
 *
 * <p>단일 주문 ID로 orders + 스냅샷 + 외부주문 + 배송지 정보를 복합 조회합니다.
 */
public interface LegacyOrderCompositeQueryPort {

    /**
     * 주문 ID로 레거시 주문 복합 정보를 조회합니다.
     *
     * @param orderId 레거시 주문 ID
     * @return 주문 복합 결과 Optional
     */
    Optional<LegacyOrderCompositeResult> fetchOrderComposite(long orderId);
}
