package com.ryuqq.marketplace.application.productnotice.port.out.command;

import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;

/** ProductNotice Command Port. */
public interface ProductNoticeCommandPort {

    Long persist(ProductNotice productNotice);
}
