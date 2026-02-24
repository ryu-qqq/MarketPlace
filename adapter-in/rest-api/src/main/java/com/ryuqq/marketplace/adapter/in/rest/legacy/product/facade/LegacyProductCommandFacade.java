package com.ryuqq.marketplace.adapter.in.rest.legacy.product.facade;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductIdResolver.ResolvedLegacyProductId;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 수정 Facade (Adapter-In 레이어).
 *
 * <p>세토프 PK → 내부 ID 변환 + 기존 Coordinator/Manager 위임을 담당하는 어댑터 로직입니다. Application 레이어의
 * LegacyProductCommandUseCase/Service를 대체합니다.
 *
 * <p>세토프 마이그레이션 완료 후 레거시 컨트롤러와 함께 제거될 임시 컴포넌트입니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class LegacyProductCommandFacade {

    private final LegacyProductIdResolver idResolver;
    private final LegacyNoticeCategoryResolver legacyNoticeCategoryResolver;
    private final FullProductGroupUpdateCoordinator fullProductGroupUpdateCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final ProductCommandCoordinator productCommandCoordinator;
    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final TimeProvider timeProvider;

    public LegacyProductCommandFacade(
            LegacyProductIdResolver idResolver,
            LegacyNoticeCategoryResolver legacyNoticeCategoryResolver,
            FullProductGroupUpdateCoordinator fullProductGroupUpdateCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            ProductCommandCoordinator productCommandCoordinator,
            ProductReadManager productReadManager,
            ProductCommandManager productCommandManager,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager,
            UpdateProductStockUseCase updateProductStockUseCase,
            TimeProvider timeProvider) {
        this.idResolver = idResolver;
        this.legacyNoticeCategoryResolver = legacyNoticeCategoryResolver;
        this.fullProductGroupUpdateCoordinator = fullProductGroupUpdateCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public void updateFull(long setofProductGroupId, ProductGroupUpdateBundle bundle) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        long internalId = resolved.internalProductGroupId();

        ProductGroupUpdateBundle resolvedBundle = replaceProductGroupId(bundle, internalId);
        fullProductGroupUpdateCoordinator.update(resolvedBundle);
    }

    @Transactional
    public void updateNotice(long setofProductGroupId, UpdateProductNoticeCommand command) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        UpdateProductNoticeCommand resolvedCommand =
                new UpdateProductNoticeCommand(
                        resolved.internalProductGroupId(),
                        command.noticeCategoryId(),
                        command.entries());
        noticeCommandCoordinator.update(resolvedCommand);
    }

    @Transactional
    public void updateImages(long setofProductGroupId, UpdateProductGroupImagesCommand command) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        UpdateProductGroupImagesCommand resolvedCommand =
                new UpdateProductGroupImagesCommand(
                        resolved.internalProductGroupId(), command.images());
        imageCommandCoordinator.update(resolvedCommand);
    }

    @Transactional
    public void updateDescription(
            long setofProductGroupId, UpdateProductGroupDescriptionCommand command) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        UpdateProductGroupDescriptionCommand resolvedCommand =
                new UpdateProductGroupDescriptionCommand(
                        resolved.internalProductGroupId(), command.content());
        descriptionCommandCoordinator.update(resolvedCommand);
    }

    @Transactional
    public void updateOptions(
            long setofProductGroupId,
            UpdateSellerOptionGroupsCommand optionCmd,
            List<ProductDiffUpdateEntry> productEntries,
            List<UpdateProductsCommand.OptionGroupData> optionGroupData) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        long internalId = resolved.internalProductGroupId();
        ProductGroupId pgId = ProductGroupId.of(internalId);

        UpdateSellerOptionGroupsCommand resolvedOptionCmd =
                new UpdateSellerOptionGroupsCommand(internalId, optionCmd.optionGroups());

        SellerOptionUpdateResult optionResult =
                sellerOptionCommandCoordinator.update(resolvedOptionCmd);
        productCommandCoordinator.updateWithDiff(
                pgId, productEntries, optionResult, optionGroupData);
    }

    @Transactional
    public void updatePrice(long setofProductGroupId, int regularPrice, int currentPrice) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        List<Product> products =
                productReadManager.findByProductGroupId(
                        ProductGroupId.of(resolved.internalProductGroupId()));
        Instant now = timeProvider.now();
        for (Product product : products) {
            product.updatePrice(Money.of(regularPrice), Money.of(currentPrice), now);
        }
        productCommandManager.persistAll(products);
    }

    @Transactional
    public void updateDisplayStatus(long setofProductGroupId, String displayYn) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        ProductGroup pg =
                productGroupReadManager.getById(
                        ProductGroupId.of(resolved.internalProductGroupId()));
        Instant now = timeProvider.now();
        if ("Y".equalsIgnoreCase(displayYn)) {
            pg.activate(now);
        } else {
            pg.deactivate(now);
        }
        productGroupCommandManager.persist(pg);
    }

    @Transactional
    public void markOutOfStock(long setofProductGroupId) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        ProductGroup pg =
                productGroupReadManager.getById(
                        ProductGroupId.of(resolved.internalProductGroupId()));
        pg.markSoldOut(timeProvider.now());
        productGroupCommandManager.persist(pg);
    }

    @Transactional
    public void updateStock(List<UpdateProductStockCommand> commands) {
        for (UpdateProductStockCommand cmd : commands) {
            updateProductStockUseCase.execute(cmd);
        }
    }

    private ProductGroupUpdateBundle replaceProductGroupId(
            ProductGroupUpdateBundle bundle, long internalId) {
        ProductGroupUpdateData originalData = bundle.basicInfoUpdateData();
        ProductGroupUpdateData resolvedData =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(internalId),
                        originalData.productGroupName(),
                        originalData.brandId(),
                        originalData.categoryId(),
                        originalData.shippingPolicyId(),
                        originalData.refundPolicyId(),
                        originalData.updatedAt());

        UpdateProductGroupImagesCommand resolvedImageCmd =
                bundle.imageCommand() != null
                        ? new UpdateProductGroupImagesCommand(
                                internalId, bundle.imageCommand().images())
                        : null;

        UpdateSellerOptionGroupsCommand resolvedOptionCmd =
                bundle.optionGroupCommand() != null
                        ? new UpdateSellerOptionGroupsCommand(
                                internalId, bundle.optionGroupCommand().optionGroups())
                        : null;

        UpdateProductGroupDescriptionCommand resolvedDescCmd =
                bundle.descriptionCommand() != null
                        ? new UpdateProductGroupDescriptionCommand(
                                internalId, bundle.descriptionCommand().content())
                        : null;

        UpdateProductNoticeCommand resolvedNoticeCmd =
                bundle.noticeCommand() != null
                        ? new UpdateProductNoticeCommand(
                                internalId,
                                legacyNoticeCategoryResolver
                                        .resolveByProductGroupId(internalId)
                                        .id()
                                        .value(),
                                bundle.noticeCommand().entries())
                        : null;

        return new ProductGroupUpdateBundle(
                resolvedData,
                resolvedImageCmd,
                resolvedOptionCmd,
                resolvedDescCmd,
                resolvedNoticeCmd,
                bundle.productEntries());
    }
}
