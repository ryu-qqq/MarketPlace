package com.ryuqq.marketplace.application.seller.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCsCommand;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

/**
 * SellerCs Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerCsCommandFactory {

    private final TimeProvider timeProvider;

    public SellerCsCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * UpdateSellerCsCommand로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (SellerId, SellerCsUpdateData, changedAt)
     */
    public UpdateContext<SellerId, SellerCsUpdateData> createUpdateContext(
            UpdateSellerCsCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        SellerCsUpdateData updateData =
                SellerCsUpdateData.of(
                        toCsContact(command.csContact()),
                        toOperatingHours(command.operatingHours()),
                        command.operatingDays(),
                        command.kakaoChannelUrl());
        return new UpdateContext<>(sellerId, updateData, timeProvider.now());
    }

    private CsContact toCsContact(UpdateSellerCsCommand.CsContactCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return CsContact.of(cmd.phone(), cmd.mobile(), cmd.email());
    }

    private OperatingHours toOperatingHours(UpdateSellerCsCommand.OperatingHoursCommand cmd) {
        if (cmd == null) {
            return OperatingHours.businessHours();
        }
        LocalTime startTime = LocalTime.parse(cmd.startTime());
        LocalTime endTime = LocalTime.parse(cmd.endTime());
        return OperatingHours.of(startTime, endTime);
    }
}
