package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.RegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.UpdateExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Command API 변환 매퍼. */
@Component
public class ExternalBrandMappingCommandApiMapper {

    public RegisterExternalBrandMappingCommand toCommand(
            Long externalSourceId, RegisterExternalBrandMappingApiRequest request) {
        return new RegisterExternalBrandMappingCommand(
                externalSourceId,
                request.externalBrandCode(),
                request.externalBrandName(),
                request.internalBrandId());
    }

    public BatchRegisterExternalBrandMappingCommand toBatchCommand(
            Long externalSourceId, BatchRegisterExternalBrandMappingApiRequest request) {
        List<BatchRegisterExternalBrandMappingCommand.MappingEntry> entries =
                request.entries().stream()
                        .map(
                                e ->
                                        new BatchRegisterExternalBrandMappingCommand.MappingEntry(
                                                e.externalBrandCode(),
                                                e.externalBrandName(),
                                                e.internalBrandId()))
                        .toList();
        return new BatchRegisterExternalBrandMappingCommand(externalSourceId, entries);
    }

    public UpdateExternalBrandMappingCommand toCommand(
            Long id, UpdateExternalBrandMappingApiRequest request) {
        return new UpdateExternalBrandMappingCommand(
                id, request.externalBrandName(), request.internalBrandId(), request.status());
    }
}
