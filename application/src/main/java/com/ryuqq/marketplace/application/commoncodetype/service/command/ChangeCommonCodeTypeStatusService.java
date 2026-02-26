package com.ryuqq.marketplace.application.commoncodetype.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.ChangeCommonCodeTypeStatusUseCase;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ChangeCommonCodeTypeStatusService - 공통 코드 타입 활성화 상태 변경 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-FAC-001: 상태변경은 StatusChangeContext 사용
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리
 *
 * @author ryu-qqq
 */
@Service
public class ChangeCommonCodeTypeStatusService implements ChangeCommonCodeTypeStatusUseCase {

    private final CommonCodeTypeCommandFactory commandFactory;
    private final CommonCodeTypeCommandManager commandManager;
    private final CommonCodeTypeValidator validator;

    public ChangeCommonCodeTypeStatusService(
            CommonCodeTypeCommandFactory commandFactory,
            CommonCodeTypeCommandManager commandManager,
            CommonCodeTypeValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(ChangeActiveStatusCommand command) {
        List<StatusChangeContext<CommonCodeTypeId>> contexts =
                commandFactory.createStatusChangeContexts(command);

        if (contexts.isEmpty()) {
            return;
        }

        List<CommonCodeTypeId> ids = contexts.stream().map(StatusChangeContext::id).toList();
        List<CommonCodeType> commonCodeTypes = validator.findAllExistingOrThrow(ids);

        // 비활성화 시 활성화된 하위 공통 코드 존재 여부 검증
        if (!command.active()) {
            for (CommonCodeType cct : commonCodeTypes) {
                validator.validateNoActiveCommonCodes(cct.id().value());
            }
        }

        // changedAt은 Factory에서 동일한 시간으로 생성됨
        Instant changedAt = contexts.get(0).changedAt();

        for (CommonCodeType cct : commonCodeTypes) {
            cct.changeActiveStatus(command.active(), changedAt);
        }

        commandManager.persistAll(commonCodeTypes);
    }
}
