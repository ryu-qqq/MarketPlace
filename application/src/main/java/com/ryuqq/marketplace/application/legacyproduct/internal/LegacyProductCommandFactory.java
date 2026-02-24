package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Component;

/**
 * Legacy Product Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class LegacyProductCommandFactory {

    private final TimeProvider timeProvider;

    public LegacyProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 가격 수정 컨텍스트 생성. */
    public StatusChangeContext<ProductGroupId> createPriceUpdateContext(
            long internalProductGroupId) {
        return new StatusChangeContext<>(
                ProductGroupId.of(internalProductGroupId), timeProvider.now());
    }

    /** 전시 상태 변경 컨텍스트 생성. */
    public StatusChangeContext<ProductGroupId> createDisplayStatusChangeContext(
            long internalProductGroupId) {
        return new StatusChangeContext<>(
                ProductGroupId.of(internalProductGroupId), timeProvider.now());
    }

    /** 품절 처리 컨텍스트 생성. */
    public StatusChangeContext<ProductGroupId> createOutOfStockContext(
            long internalProductGroupId) {
        return new StatusChangeContext<>(
                ProductGroupId.of(internalProductGroupId), timeProvider.now());
    }
}
