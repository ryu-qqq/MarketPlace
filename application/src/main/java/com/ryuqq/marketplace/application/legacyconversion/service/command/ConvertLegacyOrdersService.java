package com.ryuqq.marketplace.application.legacyconversion.service.command;

import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyOrderConversionCoordinator;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.ConvertLegacyOrdersUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PENDING 상태 레거시 주문 변환 Outbox 처리 서비스.
 *
 * <p>스케줄러에 의해 주기적으로 호출되며, PENDING 상태의 Outbox를 배치 조회하여 {@link LegacyOrderConversionCoordinator}에 위임합니다.
 */
@Service
public class ConvertLegacyOrdersService implements ConvertLegacyOrdersUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConvertLegacyOrdersService.class);
    private static final int BATCH_SIZE = 100;

    private final LegacyOrderConversionOutboxReadManager readManager;
    private final LegacyOrderConversionCoordinator conversionCoordinator;

    public ConvertLegacyOrdersService(
            LegacyOrderConversionOutboxReadManager readManager,
            LegacyOrderConversionCoordinator conversionCoordinator) {
        this.readManager = readManager;
        this.conversionCoordinator = conversionCoordinator;
    }

    @Override
    public int execute() {
        Instant now = Instant.now();
        List<LegacyOrderConversionOutbox> pendingOutboxes =
                readManager.findPendingOutboxes(now, BATCH_SIZE);

        if (pendingOutboxes.isEmpty()) {
            return 0;
        }

        log.info("레거시 주문 변환 시작: count={}", pendingOutboxes.size());

        int successCount = 0;
        for (LegacyOrderConversionOutbox outbox : pendingOutboxes) {
            try {
                conversionCoordinator.convert(outbox);
                successCount++;
            } catch (Exception e) {
                log.warn("레거시 주문 변환 실패: legacyOrderId={}", outbox.legacyOrderId(), e);
            }
        }

        log.info("레거시 주문 변환 완료: total={}, success={}", pendingOutboxes.size(), successCount);
        return successCount;
    }
}
