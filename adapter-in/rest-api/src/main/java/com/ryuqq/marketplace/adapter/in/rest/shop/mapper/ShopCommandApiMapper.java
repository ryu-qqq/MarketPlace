package com.ryuqq.marketplace.adapter.in.rest.shop.mapper;

import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.RegisterShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.UpdateShopApiRequest;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import org.springframework.stereotype.Component;

/** Shop Command API Mapper. */
@Component
public class ShopCommandApiMapper {

    public RegisterShopCommand toCommand(RegisterShopApiRequest request) {
        return new RegisterShopCommand(request.shopName(), request.accountId());
    }

    public UpdateShopCommand toCommand(Long shopId, UpdateShopApiRequest request) {
        return new UpdateShopCommand(
                shopId, request.shopName(), request.accountId(), request.status());
    }
}
