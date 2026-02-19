package com.ryuqq.marketplace.application.externalbrandmapping.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ExternalBrandMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class ExternalBrandMappingCommandFactory {

    private final TimeProvider timeProvider;

    public ExternalBrandMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ExternalBrandMapping create(RegisterExternalBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return ExternalBrandMapping.forNew(
                command.externalSourceId(),
                command.externalBrandCode(),
                command.externalBrandName(),
                command.internalBrandId(),
                now);
    }

    public List<ExternalBrandMapping> createAll(BatchRegisterExternalBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                ExternalBrandMapping.forNew(
                                        command.externalSourceId(),
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
     * @return UpdateContext (id, ExternalBrandMappingUpdateData, changedAt)
     */
    public UpdateContext<Long, ExternalBrandMappingUpdateData> createUpdateContext(
            UpdateExternalBrandMappingCommand command) {
        ExternalBrandMappingUpdateData updateData =
                ExternalBrandMappingUpdateData.of(
                        command.externalBrandName(),
                        command.internalBrandId(),
                        ExternalBrandMappingStatus.fromString(command.status()));
        return new UpdateContext<>(command.id(), updateData, timeProvider.now());
    }
}
