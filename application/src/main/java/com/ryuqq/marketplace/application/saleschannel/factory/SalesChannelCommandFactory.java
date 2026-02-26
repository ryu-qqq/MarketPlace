package com.ryuqq.marketplace.application.saleschannel.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannelUpdateData;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SalesChannel Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class SalesChannelCommandFactory {

    private final TimeProvider timeProvider;

    public SalesChannelCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 SalesChannel 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return SalesChannel 도메인 객체
     */
    public SalesChannel create(RegisterSalesChannelCommand command) {
        Instant now = timeProvider.now();
        return SalesChannel.forNew(command.channelName(), now);
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (SalesChannelId, SalesChannelUpdateData, changedAt)
     */
    public UpdateContext<SalesChannelId, SalesChannelUpdateData> createUpdateContext(
            UpdateSalesChannelCommand command) {
        SalesChannelId id = SalesChannelId.of(command.salesChannelId());
        SalesChannelUpdateData updateData =
                SalesChannelUpdateData.of(
                        command.channelName(), SalesChannelStatus.fromString(command.status()));
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }
}
