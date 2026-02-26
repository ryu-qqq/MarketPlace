package com.ryuqq.marketplace.application.inboundcategorymapping.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InboundCategoryMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class InboundCategoryMappingCommandFactory {

    private final TimeProvider timeProvider;

    public InboundCategoryMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public InboundCategoryMapping create(RegisterInboundCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return InboundCategoryMapping.forNew(
                command.inboundSourceId(),
                command.externalCategoryCode(),
                command.externalCategoryName(),
                command.internalCategoryId(),
                now);
    }

    public List<InboundCategoryMapping> createAll(
            BatchRegisterInboundCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                InboundCategoryMapping.forNew(
                                        command.inboundSourceId(),
                                        entry.externalCategoryCode(),
                                        entry.externalCategoryName(),
                                        entry.internalCategoryId(),
                                        now))
                .toList();
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, InboundCategoryMappingUpdateData, changedAt)
     */
    public UpdateContext<Long, InboundCategoryMappingUpdateData> createUpdateContext(
            UpdateInboundCategoryMappingCommand command) {
        InboundCategoryMappingUpdateData updateData =
                InboundCategoryMappingUpdateData.of(
                        command.externalCategoryName(),
                        command.internalCategoryId(),
                        InboundCategoryMappingStatus.fromString(command.status()));
        return new UpdateContext<>(command.id(), updateData, timeProvider.now());
    }
}
