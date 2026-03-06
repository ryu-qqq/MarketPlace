package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import java.util.List;

/** luxurydb에서 활성 product_group_id를 커서 기반으로 스캔하는 포트. */
public interface LegacyProductGroupIdScanPort {

    /**
     * 지정된 ID 이후의 활성 상품그룹 ID 목록을 조회합니다.
     *
     * @param afterId 이 ID 이후부터 조회 (exclusive)
     * @param limit 최대 조회 개수
     * @return 활성 상품그룹 ID 목록 (오름차순)
     */
    List<Long> findActiveProductGroupIdsAfter(long afterId, int limit);
}
