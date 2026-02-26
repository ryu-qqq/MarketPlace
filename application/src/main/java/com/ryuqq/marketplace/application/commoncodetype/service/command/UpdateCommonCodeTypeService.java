package com.ryuqq.marketplace.application.commoncodetype.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.UpdateCommonCodeTypeUseCase;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeTypeUpdateData;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import org.springframework.stereotype.Service;

/**
 * UpdateCommonCodeTypeService - 공통 코드 타입 수정 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리
 *
 * @author ryu-qqq
 */
@Service
public class UpdateCommonCodeTypeService implements UpdateCommonCodeTypeUseCase {

    private final CommonCodeTypeCommandFactory commandFactory;
    private final CommonCodeTypeCommandManager commandManager;
    private final CommonCodeTypeValidator validator;

    public UpdateCommonCodeTypeService(
            CommonCodeTypeCommandFactory commandFactory,
            CommonCodeTypeCommandManager commandManager,
            CommonCodeTypeValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(UpdateCommonCodeTypeCommand command) {
        validator.validateDisplayOrderNotDuplicate(command.displayOrder(), command.id());

        UpdateContext<CommonCodeTypeId, CommonCodeTypeUpdateData> context =
                commandFactory.createUpdateContext(command);

        CommonCodeType commonCodeType = validator.findExistingOrThrow(context.id());
        commonCodeType.update(context.updateData(), context.changedAt());

        commandManager.persist(commonCodeType);
    }
}
