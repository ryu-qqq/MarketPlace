package com.ryuqq.marketplace.application.externalsource.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceCode;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** ExternalSource Command Factory. */
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
}
