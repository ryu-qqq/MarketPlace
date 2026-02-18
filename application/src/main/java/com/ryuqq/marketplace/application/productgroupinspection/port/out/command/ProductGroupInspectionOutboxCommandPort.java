package com.ryuqq.marketplace.application.productgroupinspection.port.out.command;

import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;

/** 검수 Outbox Command Port. */
public interface ProductGroupInspectionOutboxCommandPort {

    Long persist(ProductGroupInspectionOutbox outbox);
}
