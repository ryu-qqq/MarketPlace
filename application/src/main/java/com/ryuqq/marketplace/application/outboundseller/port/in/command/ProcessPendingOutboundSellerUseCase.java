package com.ryuqq.marketplace.application.outboundseller.port.in.command;

import com.ryuqq.marketplace.application.outboundseller.dto.command.ProcessPendingOutboundSellerCommand;

public interface ProcessPendingOutboundSellerUseCase {
    void execute(ProcessPendingOutboundSellerCommand command);
}
