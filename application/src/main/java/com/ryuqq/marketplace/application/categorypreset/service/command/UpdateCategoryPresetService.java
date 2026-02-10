package com.ryuqq.marketplace.application.categorypreset.service.command;

import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCommandManager;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.command.UpdateCategoryPresetUseCase;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetErrorCode;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 수정 Service. */
@Service
public class UpdateCategoryPresetService implements UpdateCategoryPresetUseCase {

    private final CategoryPresetValidator validator;
    private final CategoryPresetCommandFactory commandFactory;
    private final CategoryPresetCommandManager commandManager;
    private final CategoryPresetReadManager readManager;
    private final ShopReadManager shopReadManager;

    public UpdateCategoryPresetService(
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
    public void execute(UpdateCategoryPresetCommand command) {
        CategoryPreset categoryPreset =
                validator.findExistingOrThrow(CategoryPresetId.of(command.categoryPresetId()));

        Long salesChannelCategoryId = categoryPreset.salesChannelCategoryId();

        if (command.categoryCode() != null && !command.categoryCode().isBlank()) {
            Shop shop = shopReadManager.getById(ShopId.of(categoryPreset.shopId()));
            salesChannelCategoryId =
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
            validator.validateSameChannel(categoryPreset.shopId(), salesChannelCategoryId);
        }

        Instant now = commandFactory.now();
        String presetName =
                command.presetName() != null ? command.presetName() : categoryPreset.presetName();
        categoryPreset.update(presetName, salesChannelCategoryId, now);
        commandManager.persist(categoryPreset);
    }
}
