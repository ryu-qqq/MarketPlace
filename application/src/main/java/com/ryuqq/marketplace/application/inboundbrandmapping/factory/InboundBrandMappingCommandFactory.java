package com.ryuqq.marketplace.application.inboundbrandmapping.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InboundBrandMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class InboundBrandMappingCommandFactory {

    private final TimeProvider timeProvider;

    public InboundBrandMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public InboundBrandMapping create(RegisterInboundBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return InboundBrandMapping.forNew(
                command.inboundSourceId(),
                command.externalBrandCode(),
                command.externalBrandName(),
                command.internalBrandId(),
                now);
    }

    public List<InboundBrandMapping> createAll(BatchRegisterInboundBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                InboundBrandMapping.forNew(
                                        command.inboundSourceId(),
                                        entry.externalBrandCode(),
                                        entry.externalBrandName(),
                                        entry.internalBrandId(),
                                        now))
                .toList();
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, InboundBrandMappingUpdateData, changedAt)
     */
    public UpdateContext<Long, InboundBrandMappingUpdateData> createUpdateContext(
            UpdateInboundBrandMappingCommand command) {
        InboundBrandMappingUpdateData updateData =
                InboundBrandMappingUpdateData.of(
                        command.externalBrandName(),
                        command.internalBrandId(),
                        InboundBrandMappingStatus.fromString(command.status()));
        return new UpdateContext<>(command.id(), updateData, timeProvider.now());
    }
}
