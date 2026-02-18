package com.ryuqq.marketplace.application.product.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
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

    /** 등록 Command → Product 도메인 객체 리스트 변환. */
    public List<Product> createProducts(RegisterProductsCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        List<SellerOptionValueId> optionValueIds =
                command.allOptionValueIds().stream().map(SellerOptionValueId::of).toList();
        Instant now = timeProvider.now();

        return command.products().stream()
                .map(data -> toProductCreationData(data).toProduct(pgId, optionValueIds, now))
                .toList();
    }

    private ProductCreationData toProductCreationData(RegisterProductsCommand.ProductData data) {
        return new ProductCreationData(
                SkuCode.of(data.skuCode()),
                Money.of(data.regularPrice()),
                Money.of(data.currentPrice()),
                data.stockQuantity(),
                data.sortOrder(),
                data.optionValueIndices());
    }

    /** diff 엔트리 → ProductCreationData 변환. */
    public ProductCreationData toCreationData(ProductDiffUpdateEntry entry) {
        return new ProductCreationData(
                SkuCode.of(entry.skuCode()),
                Money.of(entry.regularPrice()),
                Money.of(entry.currentPrice()),
                entry.stockQuantity(),
                entry.sortOrder(),
                entry.optionValueIndices());
    }
}
