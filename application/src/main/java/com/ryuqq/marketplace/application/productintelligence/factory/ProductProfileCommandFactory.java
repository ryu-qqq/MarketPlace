package com.ryuqq.marketplace.application.productintelligence.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productintelligence.dto.command.AggregateAnalysisCommand;
import org.springframework.stereotype.Component;

/**
 * ProductProfile 관련 커맨드 팩토리.
 *
 * <p>APP-TIM-001: TimeProvider.now() 호출은 Factory에서만 수행합니다. Service에서는 이 팩토리를 통해 생성된 컨텍스트에서 시간을
 * 전달받습니다.
 */
@Component
public class ProductProfileCommandFactory {

    private final TimeProvider timeProvider;

    public ProductProfileCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * Aggregation 실행을 위한 상태 변경 컨텍스트 생성.
     *
     * @param command Aggregation 커맨드
     * @return profileId와 현재 시간을 담은 컨텍스트
     */
    public StatusChangeContext<Long> createAggregationContext(AggregateAnalysisCommand command) {
        return new StatusChangeContext<>(command.profileId(), timeProvider.now());
    }
}
