package com.ryuqq.marketplace.application.legacy.product.service.command;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacySellerOptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 옵션/SKU 수정 서비스.
 *
 * <p>표준 UpdateProductsCommand를 받아 soft delete + 재등록 패턴으로 처리합니다.
 * Update 타입을 Register 타입으로 변환하여 Coordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateOptionsService implements LegacyProductUpdateOptionsUseCase {

    private final LegacySellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final LegacyProductCommandCoordinator productCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateOptionsService(
            LegacySellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            LegacyProductCommandCoordinator productCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    @Transactional
    public void execute(UpdateProductsCommand command) {
        Instant now = Instant.now();

        // 1. 옵션 등록 → SellerOptionValueId 획득
        RegisterSellerOptionGroupsCommand optionCmd = toRegisterOptionCommand(command);
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(optionCmd);

        // 2. Product soft delete + 옵션 resolve 후 재등록
        List<RegisterProductsCommand.ProductData> productDataList = toProductDataList(command);
        productCommandCoordinator.update(
                command.productGroupId(),
                productDataList,
                optionCmd.optionGroups(),
                allOptionValueIds,
                now);

        // 3. ConversionOutbox
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), now);
    }

    private RegisterSellerOptionGroupsCommand toRegisterOptionCommand(UpdateProductsCommand command) {
        List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> groups =
                command.optionGroups().stream()
                        .map(g -> new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                g.optionGroupName(),
                                g.canonicalOptionGroupId(),
                                g.inputType(),
                                g.optionValues().stream()
                                        .map(v -> new RegisterSellerOptionGroupsCommand.OptionValueCommand(
                                                v.optionValueName(),
                                                v.canonicalOptionValueId(),
                                                v.sortOrder()))
                                        .toList()))
                        .toList();
        return new RegisterSellerOptionGroupsCommand(command.productGroupId(), "COMBINATION", groups);
    }

    private List<RegisterProductsCommand.ProductData> toProductDataList(UpdateProductsCommand command) {
        return command.products().stream()
                .map(p -> new RegisterProductsCommand.ProductData(
                        p.skuCode(),
                        p.regularPrice(),
                        p.currentPrice(),
                        p.stockQuantity(),
                        p.sortOrder(),
                        p.selectedOptions()))
                .toList();
    }
}
