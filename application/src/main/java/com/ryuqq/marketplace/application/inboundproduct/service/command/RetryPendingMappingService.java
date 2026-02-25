package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResult;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PENDING_MAPPING 상태 인바운드 상품 재처리 서비스.
 *
 * <p>MappingResolver를 호출하여 매핑 재시도 후, 성공 시 PENDING_CONVERSION으로 전이합니다. 실제 변환은
 * InboundConversionScheduler가 비동기로 수행합니다.
 */
@Service
public class RetryPendingMappingService implements RetryPendingMappingUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryPendingMappingService.class);
    private static final int RETRY_BATCH_SIZE = 100;

    private final InboundProductReadManager readManager;
    private final InboundProductMappingResolver mappingResolver;
    private final InboundProductCommandManager commandManager;

    public RetryPendingMappingService(
            InboundProductReadManager readManager,
            InboundProductMappingResolver mappingResolver,
            InboundProductCommandManager commandManager) {
        this.readManager = readManager;
        this.mappingResolver = mappingResolver;
        this.commandManager = commandManager;
    }

    @Override
    public int execute() {
        List<InboundProduct> pendingProducts =
                readManager.findPendingMappingProducts(RETRY_BATCH_SIZE);

        if (pendingProducts.isEmpty()) {
            return 0;
        }

        log.info("PENDING_MAPPING 재처리 시작: count={}", pendingProducts.size());

        int successCount = 0;
        for (InboundProduct product : pendingProducts) {
            try {
                retryMapping(product);
                successCount++;
            } catch (Exception e) {
                log.warn("PENDING_MAPPING 재처리 실패: inboundProductId={}", product.idValue(), e);
            }
        }

        log.info(
                "PENDING_MAPPING 재처리 완료: total={}, success={}",
                pendingProducts.size(),
                successCount);
        return successCount;
    }

    private void retryMapping(InboundProduct product) {
        Instant now = Instant.now();
        InboundProductMappingResult mapping = mappingResolver.resolveMappingAndApply(product, now);

        if (mapping.isFullyMapped()) {
            product.markPendingConversion(now);
        }

        commandManager.persist(product);
    }
}
