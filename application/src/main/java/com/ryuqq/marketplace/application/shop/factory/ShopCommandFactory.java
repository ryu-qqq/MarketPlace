package com.ryuqq.marketplace.application.shop.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.aggregate.ShopUpdateData;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Shop Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class ShopCommandFactory {

    private final TimeProvider timeProvider;

    public ShopCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 Shop 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return Shop 도메인 객체
     */
    public Shop create(RegisterShopCommand command) {
        Instant now = timeProvider.now();
        return Shop.forNew(command.salesChannelId(), command.shopName(), command.accountId(), now);
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (ShopId, ShopUpdateData, changedAt)
     */
    public UpdateContext<ShopId, ShopUpdateData> createUpdateContext(UpdateShopCommand command) {
        ShopId shopId = ShopId.of(command.shopId());
        ShopUpdateData updateData =
                ShopUpdateData.of(
                        command.shopName(),
                        command.accountId(),
                        ShopStatus.fromString(command.status()));
        return new UpdateContext<>(shopId, updateData, timeProvider.now());
    }
}
