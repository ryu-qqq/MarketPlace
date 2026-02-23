package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.RegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.UpdateInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundBrandMapping Command API 변환 매퍼. */
@Component
public class InboundBrandMappingCommandApiMapper {

    public RegisterInboundBrandMappingCommand toCommand(
            Long inboundSourceId, RegisterInboundBrandMappingApiRequest request) {
        return new RegisterInboundBrandMappingCommand(
                inboundSourceId,
                request.externalBrandCode(),
                request.externalBrandName(),
                request.internalBrandId());
    }

    public BatchRegisterInboundBrandMappingCommand toBatchCommand(
            Long inboundSourceId, BatchRegisterInboundBrandMappingApiRequest request) {
        List<BatchRegisterInboundBrandMappingCommand.MappingEntry> entries =
                request.entries().stream()
                        .map(
                                e ->
                                        new BatchRegisterInboundBrandMappingCommand.MappingEntry(
                                                e.externalBrandCode(),
                                                e.externalBrandName(),
                                                e.internalBrandId()))
                        .toList();
        return new BatchRegisterInboundBrandMappingCommand(inboundSourceId, entries);
    }

    public UpdateInboundBrandMappingCommand toCommand(
            Long id, UpdateInboundBrandMappingApiRequest request) {
        return new UpdateInboundBrandMappingCommand(
                id, request.externalBrandName(), request.internalBrandId(), request.status());
    }
}
