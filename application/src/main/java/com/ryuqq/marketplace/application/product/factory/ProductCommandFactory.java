package com.ryuqq.marketplace.application.product.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.List;
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

    /**
     * diff 엔트리 + resolved ID → ProductCreationData 변환.
     *
     * @param entry diff 엔트리
     * @param resolvedOptionValueIds 이름 기반으로 resolve된 SellerOptionValueId 목록
     * @return ProductCreationData
     */
    public ProductCreationData toCreationData(
            ProductDiffUpdateEntry entry, List<SellerOptionValueId> resolvedOptionValueIds) {
        return new ProductCreationData(
                SkuCode.of(entry.skuCode()),
                Money.of(entry.regularPrice()),
                Money.of(entry.currentPrice()),
                entry.stockQuantity(),
                entry.sortOrder(),
                resolvedOptionValueIds);
    }
}
