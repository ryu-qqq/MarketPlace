package com.ryuqq.marketplace.application.productnotice.port.out.command;

import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;

/** ProductNoticeEntry Command Port. */
public interface ProductNoticeEntryCommandPort {

    void deleteByNoticeId(Long productNoticeId);

    void persistAll(Long productNoticeId, List<ProductNoticeEntry> entries);
}
