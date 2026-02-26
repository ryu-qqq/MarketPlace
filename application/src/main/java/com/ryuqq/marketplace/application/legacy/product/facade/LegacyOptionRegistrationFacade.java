package com.ryuqq.marketplace.application.legacy.product.facade;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyOptionDetailCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyOptionGroupCommandManager;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import org.springframework.stereotype.Component;

/**
 * 레거시 옵션 등록 Facade.
 *
 * <p>옵션그룹 + 옵션상세를 저장합니다. OptionResolver에서 중복 제거 후 호출됩니다.
 */
@Component
public class LegacyOptionRegistrationFacade {

    private final LegacyOptionGroupCommandManager optionGroupCommandManager;
    private final LegacyOptionDetailCommandManager optionDetailCommandManager;

    public LegacyOptionRegistrationFacade(
            LegacyOptionGroupCommandManager optionGroupCommandManager,
            LegacyOptionDetailCommandManager optionDetailCommandManager) {
        this.optionGroupCommandManager = optionGroupCommandManager;
        this.optionDetailCommandManager = optionDetailCommandManager;
    }

    /** 옵션그룹 저장 후 optionGroupId 반환. */
    public Long persistOptionGroup(LegacyOptionGroup optionGroup) {
        return optionGroupCommandManager.persist(optionGroup);
    }

    /** 옵션상세 저장 후 optionDetailId 반환. */
    public Long persistOptionDetail(LegacyOptionDetail optionDetail) {
        return optionDetailCommandManager.persist(optionDetail);
    }
}
