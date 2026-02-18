package com.ryuqq.marketplace.application.productnotice.port.out.command;

import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;

/** ProductNoticeEntry Command Port. */
public interface ProductNoticeEntryCommandPort {

    void persist(ProductNoticeEntry entry);
}
