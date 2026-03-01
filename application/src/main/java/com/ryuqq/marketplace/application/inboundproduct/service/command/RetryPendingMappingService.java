package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * PENDING_MAPPING 상태의 인바운드 상품을 재처리하는 서비스.
 *
 * <p>매핑 테이블을 재조회하여 브랜드/카테고리 매핑이 추가된 상품을 MAPPED 상태로 전이합니다. 변환(CONVERTED)은 크롤러 재수신
 * 시 {@code reReceive()}에서 처리됩니다.
 */
@Service
public class RetryPendingMappingService implements RetryPendingMappingUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryPendingMappingService.class);

    private final InboundProductReadManager readManager;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;
    private final TimeProvider timeProvider;

    public RetryPendingMappingService(
            InboundProductReadManager readManager,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(int batchSize) {
        List<InboundProduct> pendingProducts = readManager.findPendingMapping(batchSize);

        if (pendingProducts.isEmpty()) {
            return SchedulerBatchProcessingResult.empty();
        }

        Instant now = timeProvider.now();
        int success = 0;
        int failed = 0;

        for (InboundProduct product : pendingProducts) {
            InboundProductMappingResult mapping =
                    mappingResolver.resolveMappingAndApply(product, now);

            if (mapping.isFullyMapped()) {
                commandManager.persist(product);
                success++;
                log.info(
                        "PENDING_MAPPING 재처리 성공: id={}, inboundSourceId={}, code={}",
                        product.idValue(),
                        product.inboundSourceId(),
                        product.externalProductCodeValue());
            } else {
                failed++;
            }
        }

        log.info(
                "PENDING_MAPPING 재처리 완료: total={}, success={}, failed={}",
                pendingProducts.size(),
                success,
                failed);

        return SchedulerBatchProcessingResult.of(pendingProducts.size(), success, failed);
    }
}
