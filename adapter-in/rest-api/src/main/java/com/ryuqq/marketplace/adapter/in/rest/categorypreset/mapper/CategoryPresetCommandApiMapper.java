package com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper;

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.DeleteCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.RegisterCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.UpdateCategoryPresetApiRequest;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import org.springframework.stereotype.Component;

/** CategoryPreset Command API Mapper. */
@Component
public class CategoryPresetCommandApiMapper {

    public RegisterCategoryPresetCommand toRegisterCommand(
            RegisterCategoryPresetApiRequest request) {
        return new RegisterCategoryPresetCommand(
                request.shopId(), request.presetName(), request.categoryCode());
    }

    public UpdateCategoryPresetCommand toUpdateCommand(
            Long presetId, UpdateCategoryPresetApiRequest request) {
        return new UpdateCategoryPresetCommand(
                presetId, request.presetName(), request.categoryCode());
    }

    public DeleteCategoryPresetsCommand toDeleteCommand(DeleteCategoryPresetsApiRequest request) {
        return new DeleteCategoryPresetsCommand(request.ids());
    }
}
