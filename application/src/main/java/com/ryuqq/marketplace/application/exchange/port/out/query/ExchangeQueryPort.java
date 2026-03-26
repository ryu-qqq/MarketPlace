package com.ryuqq.marketplace.application.exchange.port.out.query;

import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 교환 클레임 Query Port. */
public interface ExchangeQueryPort {

    Optional<ExchangeClaim> findById(ExchangeClaimId id);

    Optional<ExchangeClaim> findByOrderItemId(OrderItemId orderItemId);

    List<ExchangeClaim> findByOrderItemIds(List<OrderItemId> orderItemIds);

    List<ExchangeClaim> findByCriteria(ExchangeSearchCriteria criteria);

    long countByCriteria(ExchangeSearchCriteria criteria);

    Map<ExchangeStatus, Long> countByStatus();

    List<ExchangeClaim> findAllByOrderItemId(OrderItemId orderItemId);

    /** exchangeClaimId 목록으로 일괄 조회. sellerId가 null이면 전체 조회 (슈퍼어드민). */
    List<ExchangeClaim> findByIdIn(List<String> exchangeClaimIds, Long sellerId);
}
