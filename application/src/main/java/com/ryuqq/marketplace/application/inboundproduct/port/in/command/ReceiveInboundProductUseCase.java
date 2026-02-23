package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;

public interface ReceiveInboundProductUseCase {
    InboundProductConversionResult execute(ReceiveInboundProductCommand command);
}
