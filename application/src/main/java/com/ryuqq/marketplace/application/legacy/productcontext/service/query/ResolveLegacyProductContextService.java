package com.ryuqq.marketplace.application.legacy.productcontext.service.query;

import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.ResolveLegacyProductContextCommand;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.result.LegacyProductContext;
import com.ryuqq.marketplace.application.legacy.productcontext.internal.LegacyProductContextReadFacade;
import com.ryuqq.marketplace.application.legacy.productcontext.port.in.query.ResolveLegacyProductContextUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 컨텍스트 리졸빙 서비스.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class ResolveLegacyProductContextService implements ResolveLegacyProductContextUseCase {

    private final LegacyProductContextReadFacade readFacade;

    public ResolveLegacyProductContextService(LegacyProductContextReadFacade readFacade) {
        this.readFacade = readFacade;
    }

    @Override
    public LegacyProductContext resolve(ResolveLegacyProductContextCommand command) {
        return readFacade.resolve(command);
    }
}
