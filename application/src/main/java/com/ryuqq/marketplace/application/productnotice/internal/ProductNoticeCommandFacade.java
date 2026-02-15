package com.ryuqq.marketplace.application.productnotice.internal;

import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeEntryCommandManager;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notice Command Facade.
 *
 * <p>ProductNotice 저장 → ProductNoticeEntry 저장을 조율합니다.
 */
@Component
public class NoticeCommandFacade {

    private final ProductNoticeCommandManager noticeCommandManager;
    private final ProductNoticeEntryCommandManager entryCommandManager;

    public NoticeCommandFacade(
            ProductNoticeCommandManager noticeCommandManager,
            ProductNoticeEntryCommandManager entryCommandManager) {
        this.noticeCommandManager = noticeCommandManager;
        this.entryCommandManager = entryCommandManager;
    }

    /**
     * Notice + Entry 저장.
     *
     * @param productNotice ProductNotice 도메인 객체
     * @return 저장된 noticeId
     */
    @Transactional
    public Long persist(ProductNotice productNotice) {
        Long noticeId = noticeCommandManager.persist(productNotice);
        assignNoticeIdToEntries(noticeId, productNotice.entries());
        entryCommandManager.persistAll(productNotice.entries());
        return noticeId;
    }

    private void assignNoticeIdToEntries(Long noticeId, List<ProductNoticeEntry> entries) {
        ProductNoticeId id = ProductNoticeId.of(noticeId);
        for (ProductNoticeEntry entry : entries) {
            entry.assignProductNoticeId(id);
        }
    }
}
