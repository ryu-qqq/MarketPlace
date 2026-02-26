package com.ryuqq.marketplace.application.saleschannelcategory.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SalesChannelCategory Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class SalesChannelCategoryCommandFactory {

    private final TimeProvider timeProvider;

    public SalesChannelCategoryCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 SalesChannelCategory 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return SalesChannelCategory 도메인 객체
     */
    public SalesChannelCategory create(RegisterSalesChannelCategoryCommand command) {
        Instant now = timeProvider.now();
        return SalesChannelCategory.forNew(
                command.salesChannelId(),
                command.externalCategoryCode(),
                command.externalCategoryName(),
                command.parentId(),
                command.depth(),
                command.path(),
                command.sortOrder(),
                command.leaf(),
                command.displayPath(),
                now);
    }
}
