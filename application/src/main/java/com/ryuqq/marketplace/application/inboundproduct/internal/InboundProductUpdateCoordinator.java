package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductCommandFactory;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductDiff;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductUpdateData;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 인바운드 상품 도메인 갱신 전용 Coordinator.
 *
 * <p>InboundProduct Aggregate의 데이터 갱신 + 재매핑만 담당합니다. 내부 ProductGroup 수정은 이 Coordinator의 범위 밖이며, 레거시
 * 컨트롤러에서 기존 Coordinator를 직접 호출하여 처리합니다.
 */
@Component
public class InboundProductUpdateCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductUpdateCoordinator.class);

    private final InboundProductCommandFactory factory;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;

    public InboundProductUpdateCoordinator(
            InboundProductCommandFactory factory,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver) {
        this.factory = factory;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
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
            mappingResolver.resolveMappingAndApply(existingProduct, now);
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
}
