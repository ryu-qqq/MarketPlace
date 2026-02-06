package com.ryuqq.marketplace.application.commoncodetype.service.command;

import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.port.in.command.RegisterCommonCodeTypeUseCase;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import org.springframework.stereotype.Service;

/**
 * RegisterCommonCodeTypeService - 공통 코드 타입 등록 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증은 Validator에 위임
 *
 * @author ryu-qqq
 */
@Service
public class RegisterCommonCodeTypeService implements RegisterCommonCodeTypeUseCase {

    private final CommonCodeTypeCommandFactory commandFactory;
    private final CommonCodeTypeCommandManager commandManager;
    private final CommonCodeTypeValidator validator;

    public RegisterCommonCodeTypeService(
            CommonCodeTypeCommandFactory commandFactory,
            CommonCodeTypeCommandManager commandManager,
            CommonCodeTypeValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public Long execute(RegisterCommonCodeTypeCommand command) {
        validator.validateCodeNotDuplicate(command.code());

        CommonCodeType commonCodeType = commandFactory.create(command);
        return commandManager.persist(commonCodeType);
    }
}
