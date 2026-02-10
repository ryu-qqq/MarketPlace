package com.ryuqq.marketplace.application.saleschannelbrand.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SalesChannelBrand Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class SalesChannelBrandCommandFactory {

    private final TimeProvider timeProvider;

    public SalesChannelBrandCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 SalesChannelBrand 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return SalesChannelBrand 도메인 객체
     */
    public SalesChannelBrand create(RegisterSalesChannelBrandCommand command) {
        Instant now = timeProvider.now();
        return SalesChannelBrand.forNew(
                command.salesChannelId(),
                command.externalBrandCode(),
                command.externalBrandName(),
                now);
    }
}
