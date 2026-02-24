package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductConversionCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryConvertFailedUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * CONVERT_FAILED 상태 인바운드 상품 변환 재시도 서비스.
 *
 * <p>최대 3회까지 변환을 재시도하며, 초과 시 해당 상품은 조회 대상에서 제외됩니다.
 */
@Service
public class RetryConvertFailedService implements RetryConvertFailedUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryConvertFailedService.class);
    private static final int MAX_RETRY_COUNT = 3;
    private static final int RETRY_BATCH_SIZE = 50;

    private final InboundProductReadManager readManager;
    private final InboundProductConversionCoordinator conversionCoordinator;
    private final InboundProductCommandManager commandManager;

    public RetryConvertFailedService(
            InboundProductReadManager readManager,
            InboundProductConversionCoordinator conversionCoordinator,
            InboundProductCommandManager commandManager) {
        this.readManager = readManager;
        this.conversionCoordinator = conversionCoordinator;
        this.commandManager = commandManager;
    }

    @Override
    public int execute() {
        List<InboundProduct> failedProducts =
                readManager.findConvertFailedProducts(MAX_RETRY_COUNT, RETRY_BATCH_SIZE);

        if (failedProducts.isEmpty()) {
            return 0;
        }

        log.info("CONVERT_FAILED 재처리 시작: count={}", failedProducts.size());

        int successCount = 0;
        for (InboundProduct product : failedProducts) {
            try {
                retryConversion(product);
                if (product.status().isConverted()) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("CONVERT_FAILED 재처리 실패: inboundProductId={}", product.idValue(), e);
            }
        }

        log.info(
                "CONVERT_FAILED 재처리 완료: total={}, success={}", failedProducts.size(), successCount);
        return successCount;
    }

    private void retryConversion(InboundProduct product) {
        Instant now = Instant.now();
        conversionCoordinator.convert(product, now);
        commandManager.persist(product);
    }
}
