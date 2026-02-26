package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductGroupUpdateData;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 기본정보 Coordinator.
 *
 * <p>상품그룹 등록 및 기본정보(가격, 전시상태, 품절 등) 수정 라이프사이클을 관리합니다.
 */
@Component
public class LegacyProductGroupCommandCoordinator {

    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;

    public LegacyProductGroupCommandCoordinator(
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    /** 상품그룹 등록 후 productGroupId 반환. */
    public Long register(LegacyProductGroup productGroup) {
        return commandManager.persist(productGroup);
    }

    /** 상품그룹 기본정보 수정 (전체 수정 시 사용). */
    public void updateBasicInfo(
            LegacyProductGroupId groupId,
            LegacyProductGroupUpdateData updateData,
            Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.updateProductGroupDetails(updateData, changedAt);
        commandManager.persist(productGroup);
    }

    /** 가격 수정. */
    public void updatePrice(
            LegacyProductGroupId groupId, long regularPrice, long currentPrice, Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.updatePrice(regularPrice, currentPrice, changedAt);
        commandManager.persist(productGroup);
    }

    /** 전시 상태 변경. */
    public void updateDisplayStatus(
            LegacyProductGroupId groupId, String displayYn, Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.updateDisplayYn(displayYn, changedAt);
        commandManager.persist(productGroup);
    }

    /** 품절 처리. */
    public void markSoldOut(LegacyProductGroupId groupId, Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.markSoldOut(changedAt);
        commandManager.persist(productGroup);
    }
}
