package com.ryuqq.marketplace.application.shipment.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 배송 페이지 조회 결과.
 *
 * @param results 배송 목록
 * @param pageMeta 페이지 메타 정보
 */
public record ShipmentPageResult(List<ShipmentListResult> results, PageMeta pageMeta) {

    public ShipmentPageResult {
        results = results != null ? List.copyOf(results) : List.of();
    }
}
