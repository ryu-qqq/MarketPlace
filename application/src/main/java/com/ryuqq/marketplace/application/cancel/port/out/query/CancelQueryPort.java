package com.ryuqq.marketplace.application.cancel.port.out.query;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 취소 Query Port. */
public interface CancelQueryPort {

    Optional<Cancel> findById(CancelId id);

    Optional<Cancel> findByOrderItemId(OrderItemId orderItemId);

    List<Cancel> findAllByOrderItemId(OrderItemId orderItemId);

    List<Cancel> findByOrderItemIds(List<OrderItemId> orderItemIds);

    List<Cancel> findByCriteria(CancelSearchCriteria criteria);

    long countByCriteria(CancelSearchCriteria criteria);

    Map<CancelStatus, Long> countByStatus();

    /** cancelId 목록으로 일괄 조회. sellerId가 null이면 전체 조회 (슈퍼어드민). */
    List<Cancel> findByIdIn(List<String> cancelIds, Long sellerId);
}
