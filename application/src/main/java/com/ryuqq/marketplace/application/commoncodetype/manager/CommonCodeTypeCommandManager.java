package com.ryuqq.marketplace.application.commoncodetype.manager;

import com.ryuqq.marketplace.application.commoncodetype.port.out.command.CommonCodeTypeCommandPort;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 공통 코드 타입 Command Manager. */
@Component
public class CommonCodeTypeCommandManager {

    private final CommonCodeTypeCommandPort commandPort;

    public CommonCodeTypeCommandManager(CommonCodeTypeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(CommonCodeType commonCodeType) {
        return commandPort.persist(commonCodeType);
    }

    @Transactional
    public void persistAll(List<CommonCodeType> commonCodeTypes) {
        commandPort.persistAll(commonCodeTypes);
    }
}
