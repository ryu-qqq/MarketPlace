package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductConversionCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ConvertPendingInboundProductsUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PENDING_CONVERSION 상태 인바운드 상품 비동기 변환 서비스.
 *
 * <p>스케줄러에 의해 주기적으로 호출되며, PENDING_CONVERSION 상태의 상품을 배치로 조회하여 ProductGroup으로 변환합니다.
 */
@Service
public class ConvertPendingInboundProductsService implements ConvertPendingInboundProductsUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ConvertPendingInboundProductsService.class);
    private static final int CONVERSION_BATCH_SIZE = 30;

    private final InboundProductReadManager readManager;
    private final InboundProductConversionCoordinator conversionCoordinator;
    private final InboundProductCommandManager commandManager;

    public ConvertPendingInboundProductsService(
            InboundProductReadManager readManager,
            InboundProductConversionCoordinator conversionCoordinator,
            InboundProductCommandManager commandManager) {
        this.readManager = readManager;
        this.conversionCoordinator = conversionCoordinator;
        this.commandManager = commandManager;
    }

    @Override
    public int execute() {
        List<InboundProduct> pendingProducts =
                readManager.findPendingConversionProducts(CONVERSION_BATCH_SIZE);

        if (pendingProducts.isEmpty()) {
            return 0;
        }

        log.info("PENDING_CONVERSION 변환 시작: count={}", pendingProducts.size());

        int successCount = 0;
        for (InboundProduct product : pendingProducts) {
            try {
                conversionCoordinator.convert(product, Instant.now());
                commandManager.persist(product);
                if (product.isConverted()) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("PENDING_CONVERSION 변환 실패: inboundProductId={}", product.idValue(), e);
            }
        }

        log.info(
                "PENDING_CONVERSION 변환 완료: total={}, success={}",
                pendingProducts.size(),
                successCount);
        return successCount;
    }
}
