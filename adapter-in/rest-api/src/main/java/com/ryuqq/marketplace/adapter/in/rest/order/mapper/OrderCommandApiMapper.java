package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.springframework.stereotype.Component;

/** Order Command API Mapper. */
@Component
public class OrderCommandApiMapper {

    public AddClaimHistoryMemoCommand toAddMemoCommand(
            String orderItemId,
            AddClaimHistoryMemoApiRequest request,
            MarketAccessChecker.ActorInfo actor) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.ORDER,
                orderItemId,
                orderItemId,
                request.message(),
                String.valueOf(actor.actorId()),
                actor.username());
    }
}
