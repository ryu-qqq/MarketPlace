package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.RegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.UpdateExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Command API 변환 매퍼. */
@Component
public class ExternalCategoryMappingCommandApiMapper {

    public RegisterExternalCategoryMappingCommand toCommand(
            Long externalSourceId, RegisterExternalCategoryMappingApiRequest request) {
        return new RegisterExternalCategoryMappingCommand(
                externalSourceId,
                request.externalCategoryCode(),
                request.externalCategoryName(),
                request.internalCategoryId());
    }

    public BatchRegisterExternalCategoryMappingCommand toBatchCommand(
            Long externalSourceId, BatchRegisterExternalCategoryMappingApiRequest request) {
        List<BatchRegisterExternalCategoryMappingCommand.MappingEntry> entries =
                request.entries().stream()
                        .map(
                                e ->
                                        new BatchRegisterExternalCategoryMappingCommand
                                                .MappingEntry(
                                                e.externalCategoryCode(),
                                                e.externalCategoryName(),
                                                e.internalCategoryId()))
                        .toList();
        return new BatchRegisterExternalCategoryMappingCommand(externalSourceId, entries);
    }

    public UpdateExternalCategoryMappingCommand toCommand(
            Long id, UpdateExternalCategoryMappingApiRequest request) {
        return new UpdateExternalCategoryMappingCommand(
                id, request.externalCategoryName(), request.internalCategoryId(), request.status());
    }
}
