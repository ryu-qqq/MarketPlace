package com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper;

import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.RegisterExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.UpdateExternalSourceApiRequest;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import org.springframework.stereotype.Component;

/** ExternalSource Command API 변환 매퍼. */
@Component
public class ExternalSourceCommandApiMapper {

    public RegisterExternalSourceCommand toCommand(RegisterExternalSourceApiRequest request) {
        return new RegisterExternalSourceCommand(
                request.code(), request.name(), request.type(), request.description());
    }

    public UpdateExternalSourceCommand toCommand(
            Long externalSourceId, UpdateExternalSourceApiRequest request) {
        return new UpdateExternalSourceCommand(
                externalSourceId, request.name(), request.status(), request.description());
    }
}
