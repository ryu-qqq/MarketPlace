package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductCommandFactory;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductConversionFactory;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductDiff;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductUpdateData;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundProductUpdateCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductUpdateCoordinator.class);

    private final InboundProductCommandFactory factory;
    private final InboundProductConversionFactory conversionFactory;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;
    private final FullProductGroupUpdateCoordinator updateCoordinator;
    private final ExternalSourceReadManager externalSourceReadManager;

    public InboundProductUpdateCoordinator(
            InboundProductCommandFactory factory,
            InboundProductConversionFactory conversionFactory,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver,
            FullProductGroupUpdateCoordinator updateCoordinator,
            ExternalSourceReadManager externalSourceReadManager) {
        this.factory = factory;
        this.conversionFactory = conversionFactory;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.updateCoordinator = updateCoordinator;
        this.externalSourceReadManager = externalSourceReadManager;
    }

    public InboundProductConversionResult update(
            InboundProduct existingProduct, ReceiveInboundProductCommand command) {
        Instant now = Instant.now();
        InboundProductUpdateData updateData = factory.toUpdateData(command);
        InboundProductDiff diff = existingProduct.detectChanges(updateData);

        if (!diff.hasAnyChange()) {
            log.info(
                    "인바운드 상품 변경 없음: inboundSourceId={}, code={}",
                    command.inboundSourceId(),
                    command.externalProductCode());
            return InboundProductConversionResult.noChange(
                    existingProduct.idValue(), existingProduct.internalProductGroupId());
        }

        existingProduct.applyUpdate(updateData, now);

        if (diff.requiresRemapping()) {
            InboundProductMappingResult mapping =
                    resolveMappingAndApply(existingProduct, command, now);
            if (mapping.isFullyMapped()) {
                performUpdateConversion(existingProduct, now);
            }
        } else if (existingProduct.isMapped()) {
            performUpdateConversion(existingProduct, now);
        }

        commandManager.persist(existingProduct);
        log.info(
                "인바운드 상품 업데이트: inboundSourceId={}, code={}, status={}",
                command.inboundSourceId(),
                command.externalProductCode(),
                existingProduct.status());

        if (existingProduct.status().isConverted()) {
            return InboundProductConversionResult.updated(
                    existingProduct.idValue(), existingProduct.internalProductGroupId());
        }
        return InboundProductConversionResult.pendingMapping(existingProduct.idValue());
    }

    private InboundProductMappingResult resolveMappingAndApply(
            InboundProduct product, ReceiveInboundProductCommand command, Instant now) {
        InboundProductMappingResult mapping =
                mappingResolver.resolveMapping(
                        command.inboundSourceId(),
                        command.externalBrandCode(),
                        command.externalCategoryCode());

        if (mapping.isFullyMapped()) {
            product.applyMapping(mapping.internalBrandId(), mapping.internalCategoryId(), now);
        } else {
            product.markMappingFailed(now);
            log.warn(
                    "인바운드 상품 매핑 실패: inboundSourceId={}, code={}, brandId={}, categoryId={}",
                    command.inboundSourceId(),
                    command.externalProductCode(),
                    mapping.internalBrandId(),
                    mapping.internalCategoryId());
        }
        return mapping;
    }

    private void performUpdateConversion(InboundProduct product, Instant now) {
        try {
            ExternalSource source = externalSourceReadManager.getById(product.inboundSourceId());
            ExternalSourceType sourceType = source.type();
            conversionFactory
                    .toUpdateCommand(product, sourceType)
                    .ifPresent(updateCoordinator::update);
            product.markConverted(product.internalProductGroupId(), now);
            log.info(
                    "인바운드 상품 수정 변환 완료: inboundProductId={}, productGroupId={}",
                    product.idValue(),
                    product.internalProductGroupId());
        } catch (Exception e) {
            log.error(
                    "인바운드 상품 수정 변환 실패: inboundProductId={}, externalCode={}",
                    product.idValue(),
                    product.externalProductCodeValue(),
                    e);
            product.markConvertFailed(now);
        }
    }
}
