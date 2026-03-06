package com.ryuqq.marketplace.application.order.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 주문 목록 페이지 결과.
 *
 * @param orders 주문 목록
 * @param pageMeta 페이지 메타 정보
 */
public record OrderPageResult(List<OrderListResult> orders, PageMeta pageMeta) {}
