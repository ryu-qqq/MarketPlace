package com.ryuqq.marketplace.application.claimsync.port.out.command;

import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;

import java.util.List;

/** 외부 주문상품 매핑 저장 포트. */
public interface ExternalOrderItemMappingCommandPort {

    void persist(ExternalOrderItemMapping mapping);

    void persistAll(List<ExternalOrderItemMapping> mappings);
}
