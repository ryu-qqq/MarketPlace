package com.ryuqq.marketplace.application.legacy.sellicorder.port.out;

/**
 * 셀릭 주문 중복 확인용 조회 Port.
 *
 * <p>luxurydb external_order 테이블에서 셀릭 IDX 기준 중복 여부를 확인합니다.
 */
public interface SellicLegacyOrderQueryPort {

    /**
     * 셀릭 IDX 기준 중복 주문 존재 여부를 확인합니다.
     *
     * @param siteId 사이트 ID
     * @param externalIdx 셀릭 IDX
     * @return 이미 존재하면 true
     */
    boolean existsByExternalIdx(long siteId, long externalIdx);
}
