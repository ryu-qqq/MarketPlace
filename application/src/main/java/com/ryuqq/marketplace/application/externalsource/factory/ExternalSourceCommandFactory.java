package com.ryuqq.marketplace.application.externalsource.factory;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceCode;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceUpdateData;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ExternalSource Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 */
@Component
public class ExternalSourceCommandFactory {

    private final TimeProvider timeProvider;

    public ExternalSourceCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ExternalSource create(RegisterExternalSourceCommand command) {
        Instant now = timeProvider.now();
        ExternalSourceCode code = ExternalSourceCode.of(command.code());
        ExternalSourceType type = ExternalSourceType.fromString(command.type());
        return ExternalSource.forNew(code, command.name(), type, command.description(), now);
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext (externalSourceId, ExternalSourceUpdateData, changedAt)
     */
    public UpdateContext<Long, ExternalSourceUpdateData> createUpdateContext(
            UpdateExternalSourceCommand command) {
        ExternalSourceUpdateData updateData =
                ExternalSourceUpdateData.of(
                        command.name(),
                        command.description(),
                        ExternalSourceStatus.fromString(command.status()));
        return new UpdateContext<>(command.externalSourceId(), updateData, timeProvider.now());
    }
}
