package com.ryuqq.marketplace.application.legacy.notice.internal;

import com.ryuqq.marketplace.application.legacy.notice.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * 레거시 고시정보 Coordinator.
 *
 * <p>고시정보 등록 및 수정 라이프사이클을 관리합니다.
 */
@Component
public class LegacyNoticeCommandCoordinator {

    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyProductNoticeCommandManager noticeCommandManager;

    public LegacyNoticeCommandCoordinator(
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyProductNoticeCommandManager noticeCommandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.noticeCommandManager = noticeCommandManager;
    }

    /** 고시정보 등록 (상품그룹 등록 시 사용). */
    public void register(LegacyProductGroupId groupId, LegacyProductNotice notice) {
        noticeCommandManager.persist(groupId, notice);
    }

    /** 고시정보 수정. */
    public void update(
            LegacyProductGroupId groupId, LegacyProductNotice notice, Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.updateNotice(notice, changedAt);
        noticeCommandManager.persist(groupId, productGroup.notice());
        commandManager.persist(productGroup);
    }
}
