package com.ryuqq.marketplace.application.commoncode.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeCommandFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeCommandManager;
import com.ryuqq.marketplace.application.commoncode.port.in.command.UpdateCommonCodeUseCase;
import com.ryuqq.marketplace.application.commoncode.validator.CommonCodeValidator;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCodeUpdateData;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import org.springframework.stereotype.Service;

/**
 * UpdateCommonCodeService - 공통 코드 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class UpdateCommonCodeService implements UpdateCommonCodeUseCase {

    private final CommonCodeCommandFactory commandFactory;
    private final CommonCodeCommandManager commandManager;
    private final CommonCodeValidator validator;

    public UpdateCommonCodeService(
            CommonCodeCommandFactory commandFactory,
            CommonCodeCommandManager commandManager,
            CommonCodeValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(UpdateCommonCodeCommand command) {
        UpdateContext<CommonCodeId, CommonCodeUpdateData> context =
                commandFactory.createUpdateContext(command);

        CommonCode commonCode = validator.findExistingOrThrow(context.id());
        commonCode.update(context.updateData(), context.changedAt());

        commandManager.persist(commonCode);
    }
}
