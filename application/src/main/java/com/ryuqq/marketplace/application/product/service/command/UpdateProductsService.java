package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateProductsService - 상품(SKU) + 옵션 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>옵션 그룹 diff 수정 후 resolvedActiveValueIds를 기반으로 Product diff를 수행합니다.
 */
@Service
public class UpdateProductsService implements UpdateProductsUseCase {

    private final SellerOptionCommandCoordinator sellerOptionCoordinator;
    private final ProductCommandCoordinator productCoordinator;

    public UpdateProductsService(
            SellerOptionCommandCoordinator sellerOptionCoordinator,
            ProductCommandCoordinator productCoordinator) {
        this.sellerOptionCoordinator = sellerOptionCoordinator;
        this.productCoordinator = productCoordinator;
    }

    @Override
    @Transactional
    public void execute(UpdateProductsCommand command) {
        // 1. 옵션 수정 → resolvedActiveValueIds 획득
        UpdateSellerOptionGroupsCommand optionCmd = toOptionCommand(command);
        SellerOptionUpdateResult optionResult = sellerOptionCoordinator.update(optionCmd);

        // 2. Product diff 수정 (retained/added/removed)
        List<ProductDiffUpdateEntry> entries = toEntries(command.products());
        productCoordinator.updateWithDiff(
                ProductGroupId.of(command.productGroupId()), entries, optionResult);
    }

    private UpdateSellerOptionGroupsCommand toOptionCommand(UpdateProductsCommand command) {
        List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> groups =
                command.optionGroups().stream()
                        .map(
                                g ->
                                        new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                                                g.sellerOptionGroupId(),
                                                g.optionGroupName(),
                                                g.canonicalOptionGroupId(),
                                                g.optionValues().stream()
                                                        .map(
                                                                v ->
                                                                        new UpdateSellerOptionGroupsCommand
                                                                                .OptionValueCommand(
                                                                                v
                                                                                        .sellerOptionValueId(),
                                                                                v.optionValueName(),
                                                                                v
                                                                                        .canonicalOptionValueId(),
                                                                                v.sortOrder()))
                                                        .toList()))
                        .toList();
        return new UpdateSellerOptionGroupsCommand(command.productGroupId(), groups);
    }

    private List<ProductDiffUpdateEntry> toEntries(
            List<UpdateProductsCommand.ProductData> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductDiffUpdateEntry(
                                        p.productId(),
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.optionValueIndices()))
                .toList();
    }
}
