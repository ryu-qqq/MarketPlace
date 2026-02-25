package com.ryuqq.marketplace.application.productnotice.internal;

import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.factory.ProductNoticeCommandFactory;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeEntryCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.application.productnotice.validator.NoticeEntriesValidator;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeUpdateData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notice Command Coordinator.
 *
 * <p>Notice + Entry 등록/수정을 조율합니다.
 */
@Component
public class ProductNoticeCommandCoordinator {

    private final ProductNoticeCommandFactory noticeCommandFactory;
    private final NoticeEntriesValidator noticeEntriesValidator;
    private final ProductNoticeCommandManager noticeCommandManager;
    private final ProductNoticeEntryCommandManager entryCommandManager;
    private final ProductNoticeReadManager noticeReadManager;
    private final ProductNoticeCommandFacade noticeCommandFacade;

    public ProductNoticeCommandCoordinator(
            ProductNoticeCommandFactory noticeCommandFactory,
            NoticeEntriesValidator noticeEntriesValidator,
            ProductNoticeCommandManager noticeCommandManager,
            ProductNoticeEntryCommandManager entryCommandManager,
            ProductNoticeReadManager noticeReadManager,
            ProductNoticeCommandFacade noticeCommandFacade) {
        this.noticeCommandFactory = noticeCommandFactory;
        this.noticeEntriesValidator = noticeEntriesValidator;
        this.noticeCommandManager = noticeCommandManager;
        this.entryCommandManager = entryCommandManager;
        this.noticeReadManager = noticeReadManager;
        this.noticeCommandFacade = noticeCommandFacade;
    }

    /**
     * 등록 Command로 Notice 생성 + 검증 + 저장.
     *
     * @param command 고시정보 등록 Command
     * @return 저장된 noticeId
     */
    @Transactional
    public Long register(RegisterProductNoticeCommand command) {
        ProductNotice notice = noticeCommandFactory.create(command);
        return register(notice);
    }

    /**
     * 도메인 객체로 Notice 검증 + 저장.
     *
     * @param productNotice ProductNotice 도메인 객체
     * @return 저장된 noticeId
     */
    @Transactional
    public Long register(ProductNotice productNotice) {
        noticeEntriesValidator.validate(productNotice);
        return persist(productNotice);
    }

    /**
     * 수정 Command 기반: 기존 Notice 조회 → 검증 → 수정 → 저장.
     *
     * @param command 고시정보 수정 Command
     */
    @Transactional
    public void update(UpdateProductNoticeCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        ProductNotice existing = noticeReadManager.getByProductGroupId(pgId);

        ProductNoticeUpdateData updateData = noticeCommandFactory.createUpdateData(command);
        noticeEntriesValidator.validate(updateData);

        entryCommandManager.deleteByNoticeId(existing.idValue());
        existing.update(updateData);
        noticeCommandFacade.persist(existing);
    }

    /**
     * Notice + Entry 등록 전용 저장 (insert only).
     *
     * @param productNotice ProductNotice 도메인 객체
     * @return 저장된 noticeId
     */
    @Transactional
    public Long persist(ProductNotice productNotice) {
        Long noticeId = noticeCommandManager.persist(productNotice);
        productNotice.assignId(ProductNoticeId.of(noticeId));
        entryCommandManager.persistAll(productNotice.entries());
        return noticeId;
    }
}
