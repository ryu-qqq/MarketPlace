package com.ryuqq.marketplace.application.outboundseller.port.in.command;

import com.ryuqq.marketplace.application.outboundseller.dto.command.RecoverTimeoutOutboundSellerCommand;

public interface RecoverTimeoutOutboundSellerUseCase {
    void execute(RecoverTimeoutOutboundSellerCommand command);
}
