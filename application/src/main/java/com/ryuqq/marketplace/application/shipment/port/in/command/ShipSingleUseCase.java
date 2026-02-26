package com.ryuqq.marketplace.application.shipment.port.in.command;

import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;

/** 단건 송장등록 UseCase. */
public interface ShipSingleUseCase {

    void execute(ShipSingleCommand command);
}
