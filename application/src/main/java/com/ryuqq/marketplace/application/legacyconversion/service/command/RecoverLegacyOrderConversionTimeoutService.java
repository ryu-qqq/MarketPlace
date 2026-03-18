package com.ryuqq.marketplace.application.legacyconversion.service.command;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.RecoverLegacyOrderConversionTimeoutUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 주문 변환 PROCESSING 타임아웃 복구 서비스.
 *
 * <p>PROCESSING 상태에서 10분 이상 머문 Outbox를 PENDING으로 복구하여 재처리 대상에 포함시킵니다.
 */
@Service
public class RecoverLegacyOrderConversionTimeoutService
        implements RecoverLegacyOrderConversionTimeoutUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverLegacyOrderConversionTimeoutService.class);
    private static final Duration TIMEOUT_THRESHOLD = Duration.ofMinutes(10);
    private static final int BATCH_SIZE = 50;

    private final LegacyOrderConversionOutboxReadManager readManager;
    private final LegacyOrderConversionOutboxCommandManager commandManager;

    public RecoverLegacyOrderConversionTimeoutService(
            LegacyOrderConversionOutboxReadManager readManager,
            LegacyOrderConversionOutboxCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public int execute() {
        Instant timeoutThreshold = Instant.now().minus(TIMEOUT_THRESHOLD);
        List<LegacyOrderConversionOutbox> timeoutOutboxes =
                readManager.findProcessingTimeoutOutboxes(timeoutThreshold, BATCH_SIZE);

        if (timeoutOutboxes.isEmpty()) {
            return 0;
        }

        log.info("레거시 주문 PROCESSING 타임아웃 복구 시작: count={}", timeoutOutboxes.size());

        Instant now = Instant.now();
        int recoveredCount = 0;
        for (LegacyOrderConversionOutbox outbox : timeoutOutboxes) {
            try {
                outbox.recoverFromTimeout(now);
                commandManager.persist(outbox);
                recoveredCount++;
            } catch (Exception e) {
                log.warn("타임아웃 복구 실패: legacyOrderId={}", outbox.legacyOrderId(), e);
            }
        }

        log.info(
                "레거시 주문 PROCESSING 타임아웃 복구 완료: total={}, recovered={}",
                timeoutOutboxes.size(),
                recoveredCount);
        return recoveredCount;
    }
}
