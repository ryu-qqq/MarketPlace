package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RetryPendingMappingService implements RetryPendingMappingUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryPendingMappingService.class);
    private static final int RETRY_BATCH_SIZE = 100;

    private final InboundProductReadManager readManager;
    private final ReceiveInboundProductUseCase receiveInboundProductUseCase;

    public RetryPendingMappingService(
            InboundProductReadManager readManager,
            ReceiveInboundProductUseCase receiveInboundProductUseCase) {
        this.readManager = readManager;
        this.receiveInboundProductUseCase = receiveInboundProductUseCase;
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
                ReceiveInboundProductCommand retryCommand = toRetryCommand(product);
                receiveInboundProductUseCase.execute(retryCommand);
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

    private ReceiveInboundProductCommand toRetryCommand(InboundProduct product) {
        return new ReceiveInboundProductCommand(
                product.inboundSourceId(),
                product.externalProductCodeValue(),
                product.productName(),
                product.externalBrandCode(),
                product.externalCategoryCode(),
                product.sellerId(),
                product.regularPrice(),
                product.currentPrice(),
                product.optionType(),
                product.descriptionHtml(),
                product.rawPayloadJson());
    }
}
