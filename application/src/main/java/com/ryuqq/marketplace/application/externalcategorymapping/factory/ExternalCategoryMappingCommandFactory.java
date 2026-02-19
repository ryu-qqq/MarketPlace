package com.ryuqq.marketplace.application.externalcategorymapping.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ExternalCategoryMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class ExternalCategoryMappingCommandFactory {

    private final TimeProvider timeProvider;

    public ExternalCategoryMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ExternalCategoryMapping create(RegisterExternalCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return ExternalCategoryMapping.forNew(
                command.externalSourceId(),
                command.externalCategoryCode(),
                command.externalCategoryName(),
                command.internalCategoryId(),
                now);
    }

    public List<ExternalCategoryMapping> createAll(
            BatchRegisterExternalCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return command.entries().stream()
                .map(
                        entry ->
                                ExternalCategoryMapping.forNew(
                                        command.externalSourceId(),
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
     * @return UpdateContext (id, ExternalCategoryMappingUpdateData, changedAt)
     */
    public UpdateContext<Long, ExternalCategoryMappingUpdateData> createUpdateContext(
            UpdateExternalCategoryMappingCommand command) {
        ExternalCategoryMappingUpdateData updateData =
                ExternalCategoryMappingUpdateData.of(
                        command.externalCategoryName(),
                        command.internalCategoryId(),
                        ExternalCategoryMappingStatus.fromString(command.status()));
        return new UpdateContext<>(command.id(), updateData, timeProvider.now());
    }
}
