package com.ryuqq.marketplace.application.order.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 상품주문 리스트 페이지 결과 (V5).
 *
 * @param productOrders 상품주문 리스트 항목
 * @param pageMeta 페이지 메타 정보
 */
public record ProductOrderPageResult(
        List<ProductOrderListResult> productOrders, PageMeta pageMeta) {}
