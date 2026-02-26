package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping Command API 변환 매퍼. */
@Component
public class InboundCategoryMappingCommandApiMapper {

    public RegisterInboundCategoryMappingCommand toCommand(
            Long inboundSourceId, RegisterInboundCategoryMappingApiRequest request) {
        return new RegisterInboundCategoryMappingCommand(
                inboundSourceId,
                request.externalCategoryCode(),
                request.externalCategoryName(),
                request.internalCategoryId());
    }

    public BatchRegisterInboundCategoryMappingCommand toBatchCommand(
            Long inboundSourceId, BatchRegisterInboundCategoryMappingApiRequest request) {
        List<BatchRegisterInboundCategoryMappingCommand.MappingEntry> entries =
                request.entries().stream()
                        .map(
                                e ->
                                        new BatchRegisterInboundCategoryMappingCommand.MappingEntry(
                                                e.externalCategoryCode(),
                                                e.externalCategoryName(),
                                                e.internalCategoryId()))
                        .toList();
        return new BatchRegisterInboundCategoryMappingCommand(inboundSourceId, entries);
    }

    public UpdateInboundCategoryMappingCommand toCommand(
            Long id, UpdateInboundCategoryMappingApiRequest request) {
        return new UpdateInboundCategoryMappingCommand(
                id, request.externalCategoryName(), request.internalCategoryId(), request.status());
    }
}
