package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductCommandFactory;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupReadManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 인바운드 상품 등록/갱신 Coordinator.
 *
 * <p>동기 변환 방식: 수신 시점에 즉시 매핑 → 정책 해석 → 내부 ProductGroup 등록/수정까지 완료합니다. 매핑 실패 시 PENDING_MAPPING 상태로
 * 저장하고, 이후 재수신 시 다시 시도합니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class InboundProductRegisterCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductRegisterCoordinator.class);

    private final InboundProductCommandFactory factory;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;
    private final InboundProductRegistrationResolver registrationResolver;
    private final InboundProductCommandConverter converter;
    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;
    private final FullProductGroupUpdateCoordinator updateCoordinator;
    private final ProductReadManager productReadManager;
    private final SellerOptionGroupReadManager sellerOptionGroupReadManager;
    private final TimeProvider timeProvider;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public InboundProductRegisterCoordinator(
            InboundProductCommandFactory factory,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver,
            InboundProductRegistrationResolver registrationResolver,
            InboundProductCommandConverter converter,
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator,
            FullProductGroupUpdateCoordinator updateCoordinator,
            ProductReadManager productReadManager,
            SellerOptionGroupReadManager sellerOptionGroupReadManager,
            TimeProvider timeProvider) {
        this.factory = factory;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.registrationResolver = registrationResolver;
        this.converter = converter;
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
        this.updateCoordinator = updateCoordinator;
        this.productReadManager = productReadManager;
        this.sellerOptionGroupReadManager = sellerOptionGroupReadManager;
        this.timeProvider = timeProvider;
    }

    /** 신규 인바운드 상품 등록 (최초 수신). */
    public InboundProductConversionResult register(ReceiveInboundProductCommand command) {
        InboundProduct newProduct = factory.create(command);
        Instant now = newProduct.createdAt();

        InboundProductMappingResult mapping =
                mappingResolver.resolveMappingAndApply(newProduct, now);

        if (!mapping.isFullyMapped()) {
            newProduct.markPendingMapping(now);
            commandManager.persist(newProduct);
            log.info(
                    "인바운드 상품 매핑 대기: inboundSourceId={}, code={}",
                    command.inboundSourceId(),
                    command.externalProductCode());
            return InboundProductConversionResult.pendingMapping(newProduct.idValue());
        }

        return convertAndPersist(newProduct, command, now);
    }

    /** 기존 인바운드 상품 재수신 (갱신). */
    public InboundProductConversionResult reReceive(
            InboundProduct existingProduct, ReceiveInboundProductCommand command) {
        Instant now = timeProvider.now();

        if (!existingProduct.isConverted()) {
            if (!existingProduct.isMapped()) {
                InboundProductMappingResult mapping =
                        mappingResolver.resolveMappingAndApply(existingProduct, now);
                if (!mapping.isFullyMapped()) {
                    existingProduct.markPendingMapping(now);
                    commandManager.persist(existingProduct);
                    return InboundProductConversionResult.pendingMapping(existingProduct.idValue());
                }
            }
            return convertAndPersist(existingProduct, command, now);
        }

        return updateExisting(existingProduct, command, now);
    }

    /** 매핑 완료 후 동기 변환 (신규 등록). */
    private InboundProductConversionResult convertAndPersist(
            InboundProduct product, ReceiveInboundProductCommand command, Instant now) {

        SellerId sellerId = SellerId.of(product.sellerId());
        ResolvedPolicies resolved =
                registrationResolver.resolve(sellerId, product.internalCategoryId(), command);
        product.applyResolution(
                resolved.shippingPolicyId(),
                resolved.refundPolicyId(),
                resolved.noticeCategoryId(),
                now);

        RegisterProductGroupCommand registerCommand =
                converter.toRegisterCommand(command, product, resolved);
        ProductGroupRegistrationBundle bundle =
                bundleFactory.createProductGroupBundle(registerCommand);
        ProductGroupRegistrationResult result = registrationCoordinator.register(bundle);

        product.markConverted(result.productGroupId(), now);
        commandManager.persist(product);

        log.info(
                "인바운드 상품 신규 변환 완료: inboundSourceId={}, code={}, productGroupId={}",
                product.inboundSourceId(),
                product.externalProductCodeValue(),
                result.productGroupId());

        return InboundProductConversionResult.created(product.idValue(), result.productGroupId());
    }

    /** 이미 변환 완료된 상품의 재수신 (갱신). */
    private InboundProductConversionResult updateExisting(
            InboundProduct product, ReceiveInboundProductCommand command, Instant now) {

        SellerId sellerId = SellerId.of(product.sellerId());
        ResolvedPolicies resolved =
                registrationResolver.resolve(sellerId, product.internalCategoryId(), command);
        product.applyResolution(
                resolved.shippingPolicyId(),
                resolved.refundPolicyId(),
                resolved.noticeCategoryId(),
                now);

        ProductGroupId productGroupId = ProductGroupId.of(product.internalProductGroupId());
        InboundIdMaps idMaps = buildIdMaps(productGroupId);

        UpdateProductGroupFullCommand updateCommand =
                converter.toUpdateCommand(command, product, resolved, idMaps);
        ProductGroupUpdateBundle bundle = bundleFactory.createUpdateBundle(updateCommand);
        updateCoordinator.update(bundle);

        product.markConverted(product.internalProductGroupId(), now);
        commandManager.persist(product);

        log.info(
                "인바운드 상품 갱신 완료: inboundSourceId={}, code={}, productGroupId={}",
                product.inboundSourceId(),
                product.externalProductCodeValue(),
                product.internalProductGroupId());

        return InboundProductConversionResult.updated(
                product.idValue(), product.internalProductGroupId());
    }

    private InboundIdMaps buildIdMaps(ProductGroupId productGroupId) {
        List<Product> existingProducts = productReadManager.findByProductGroupId(productGroupId);
        SellerOptionGroups existingOptions =
                sellerOptionGroupReadManager.getByProductGroupId(productGroupId);
        return InboundIdMaps.from(existingProducts, existingOptions);
    }
}
