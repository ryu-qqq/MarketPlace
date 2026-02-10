package com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.RegisterBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.UpdateBrandPresetApiRequest;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset Command API Mapper. */
@Component
public class BrandPresetCommandApiMapper {

    public RegisterBrandPresetCommand toCommand(RegisterBrandPresetApiRequest request) {
        return new RegisterBrandPresetCommand(
                request.shopId(), request.salesChannelBrandId(), request.presetName());
    }

    public UpdateBrandPresetCommand toCommand(
            Long brandPresetId, UpdateBrandPresetApiRequest request) {
        return new UpdateBrandPresetCommand(
                brandPresetId, request.presetName(), request.salesChannelBrandId());
    }

    public DeleteBrandPresetsCommand toDeleteCommand(List<Long> ids) {
        return new DeleteBrandPresetsCommand(ids);
    }
}
