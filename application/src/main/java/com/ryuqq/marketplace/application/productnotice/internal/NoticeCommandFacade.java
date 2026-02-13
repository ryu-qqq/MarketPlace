package com.ryuqq.marketplace.application.productnotice.internal;

import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeEntryCommandManager;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notice Command Facade.
 *
 * <p>ProductNotice 저장 → ProductNoticeEntry 교체를 조율합니다.
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
     * <p>1. Notice 저장 → noticeId 획득
     *
     * <p>2. 기존 entries 삭제
     *
     * <p>3. 새 entries 저장
     *
     * @param productNotice ProductNotice 도메인 객체
     * @return 저장된 noticeId
     */
    @Transactional
    public Long persist(ProductNotice productNotice) {
        Long noticeId = noticeCommandManager.persist(productNotice);

        entryCommandManager.deleteByNoticeId(noticeId);
        entryCommandManager.persistAll(noticeId, productNotice.entries());

        return noticeId;
    }
}
