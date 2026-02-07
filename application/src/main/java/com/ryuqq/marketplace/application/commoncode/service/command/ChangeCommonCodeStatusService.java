package com.ryuqq.marketplace.application.commoncode.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeCommandFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeCommandManager;
import com.ryuqq.marketplace.application.commoncode.port.in.command.ChangeCommonCodeStatusUseCase;
import com.ryuqq.marketplace.application.commoncode.validator.CommonCodeValidator;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ChangeCommonCodeStatusService - 공통 코드 활성화 상태 변경 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>APP-FAC-001: 상태변경은 StatusChangeContext 사용.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class ChangeCommonCodeStatusService implements ChangeCommonCodeStatusUseCase {

    private final CommonCodeCommandFactory commandFactory;
    private final CommonCodeCommandManager commandManager;
    private final CommonCodeValidator validator;

    public ChangeCommonCodeStatusService(
            CommonCodeCommandFactory commandFactory,
            CommonCodeCommandManager commandManager,
            CommonCodeValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(ChangeCommonCodeStatusCommand command) {
        List<StatusChangeContext<CommonCodeId>> contexts =
                commandFactory.createStatusChangeContexts(command);

        if (contexts.isEmpty()) {
            return;
        }

        List<CommonCodeId> ids = contexts.stream().map(StatusChangeContext::id).toList();
        List<CommonCode> commonCodes = validator.findAllExistingOrThrow(ids);

        // changedAt은 Factory에서 동일한 시간으로 생성됨
        Instant changedAt = contexts.get(0).changedAt();

        for (CommonCode cc : commonCodes) {
            if (command.active()) {
                cc.activate(changedAt);
            } else {
                cc.deactivate(changedAt);
            }
        }

        commandManager.persistAll(commonCodes);
    }
}
