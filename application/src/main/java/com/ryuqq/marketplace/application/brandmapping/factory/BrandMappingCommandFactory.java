package com.ryuqq.marketplace.application.brandmapping.factory;

import com.ryuqq.marketplace.application.brandmapping.dto.command.DeleteBrandMappingCommand;
import com.ryuqq.marketplace.application.brandmapping.dto.command.RegisterBrandMappingCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * BrandMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class BrandMappingCommandFactory {

    private final TimeProvider timeProvider;

    public BrandMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 BrandMapping 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return BrandMapping 도메인 객체
     */
    public BrandMapping create(RegisterBrandMappingCommand command) {
        Instant now = timeProvider.now();
        return BrandMapping.forNew(command.salesChannelBrandId(), command.internalBrandId(), now);
    }

    /**
     * 비활성화 StatusChangeContext 생성.
     *
     * @param command 삭제(비활성화) Command
     * @return StatusChangeContext
     */
    public StatusChangeContext<BrandMappingId> createDeactivateContext(
            DeleteBrandMappingCommand command) {
        return new StatusChangeContext<>(
                BrandMappingId.of(command.brandMappingId()), timeProvider.now());
    }
}
