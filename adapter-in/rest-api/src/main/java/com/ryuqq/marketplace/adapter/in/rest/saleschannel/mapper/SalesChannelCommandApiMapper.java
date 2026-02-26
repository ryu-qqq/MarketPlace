package com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper;

import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.RegisterSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.UpdateSalesChannelApiRequest;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import org.springframework.stereotype.Component;

/** SalesChannel Command API Mapper. */
@Component
public class SalesChannelCommandApiMapper {

    public RegisterSalesChannelCommand toCommand(RegisterSalesChannelApiRequest request) {
        return new RegisterSalesChannelCommand(request.channelName());
    }

    public UpdateSalesChannelCommand toCommand(
            Long salesChannelId, UpdateSalesChannelApiRequest request) {
        return new UpdateSalesChannelCommand(
                salesChannelId, request.channelName(), request.status());
    }
}
