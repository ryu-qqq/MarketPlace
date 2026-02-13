package com.ryuqq.marketplace.application.product.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.ChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import org.springframework.stereotype.Component;

/**
 * Product Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductCommandFactory {

    private final TimeProvider timeProvider;

    public ProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 가격 수정 컨텍스트 생성. */
    public StatusChangeContext<ProductId> createPriceUpdateContext(
            UpdateProductPriceCommand command) {
        return new StatusChangeContext<>(ProductId.of(command.productId()), timeProvider.now());
    }

    /** 재고 수정 컨텍스트 생성. */
    public StatusChangeContext<ProductId> createStockUpdateContext(
            UpdateProductStockCommand command) {
        return new StatusChangeContext<>(ProductId.of(command.productId()), timeProvider.now());
    }

    /** 상태 변경 컨텍스트 생성. */
    public StatusChangeContext<ProductId> createStatusChangeContext(
            ChangeProductStatusCommand command) {
        return new StatusChangeContext<>(ProductId.of(command.productId()), timeProvider.now());
    }
}
