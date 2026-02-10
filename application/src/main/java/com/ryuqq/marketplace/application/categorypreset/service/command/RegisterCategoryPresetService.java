package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCommandManager;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.RegisterCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetErrorCode;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetException;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 등록 Service. */
@Service
public class RegisterCategoryPresetService implements RegisterCategoryPresetUseCase {

    private final CategoryPresetValidator validator;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetCommandManager commandManager;
    private final CategoryPresetReadManager readManager;
    private final ShopReadManager shopReadManager;

    public RegisterCategoryPresetService(
            CategoryPresetValidator validator,
            CategoryPresetCommandFactory commandFactory,
            CategoryPresetCommandManager commandManager,
            CategoryPresetReadManager readManager,
            ShopReadManager shopReadManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.readManager = readManager;
        this.shopReadManager = shopReadManager;
    }

    @Override
    public Long execute(RegisterCategoryPresetCommand command) {
        Shop shop = shopReadManager.getById(ShopId.of(command.shopId()));
        Long salesChannelCategoryId =
                readManager
                        .findSalesChannelCategoryIdByCode(
                                shop.salesChannelId(), command.categoryCode())
                        .orElseThrow(
                                () ->
                                        new CategoryPresetException(
                                                CategoryPresetErrorCode
                                                        .CATEGORY_PRESET_CHANNEL_MISMATCH,
                                                String.format(
                                                        "카테고리 코드 '%s'를 찾을 수 없습니다",
                                                        command.categoryCode())));

        validator.validateSameChannel(command.shopId(), salesChannelCategoryId);

        CategoryPreset categoryPreset =
                CategoryPreset.forNew(
                        command.shopId(),
                        salesChannelCategoryId,
                        command.presetName(),
                        commandFactory.now());
        return commandManager.persist(categoryPreset);
    }
}
