package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.inboundorder.internal.InboundOrderReceiveCoordinator;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderReadManager;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.RetryInboundOrderMappingUseCase;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** PENDING_MAPPING 상태 인바운드 주문 재시도 서비스. */
@Service
public class RetryInboundOrderMappingService implements RetryInboundOrderMappingUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RetryInboundOrderMappingService.class);

    private final InboundOrderReadManager readManager;
    private final InboundOrderReceiveCoordinator coordinator;

    public RetryInboundOrderMappingService(
            InboundOrderReadManager readManager, InboundOrderReceiveCoordinator coordinator) {
        this.readManager = readManager;
        this.coordinator = coordinator;
    }

    @Override
    public SchedulerBatchProcessingResult execute(int batchSize) {
        Instant now = Instant.now();
        List<InboundOrder> pendingOrders =
                readManager.findByStatus(InboundOrderStatus.PENDING_MAPPING, batchSize);

        if (pendingOrders.isEmpty()) {
            return SchedulerBatchProcessingResult.of(0, 0, 0);
        }

        int success = 0;
        int fail = 0;

        for (InboundOrder order : pendingOrders) {
            boolean converted = coordinator.retryMapping(order, now);
            if (converted) {
                success++;
            } else {
                fail++;
            }
        }

        log.info(
                "InboundOrder 매핑 재시도 완료: total={}, success={}, fail={}",
                pendingOrders.size(),
                success,
                fail);

        return SchedulerBatchProcessingResult.of(pendingOrders.size(), success, fail);
    }
}
