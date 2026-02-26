package com.ryuqq.marketplace.application.legacyconversion.service.command;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.RecoverLegacyConversionTimeoutUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PROCESSING 타임아웃 복구 서비스.
 *
 * <p>PROCESSING 상태에서 10분 이상 머문 Outbox를 PENDING으로 복구하여 재처리 대상에 포함시킵니다.
 */
@Service
public class RecoverLegacyConversionTimeoutService
        implements RecoverLegacyConversionTimeoutUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(RecoverLegacyConversionTimeoutService.class);
    private static final Duration TIMEOUT_THRESHOLD = Duration.ofMinutes(10);
    private static final int BATCH_SIZE = 50;

    private final LegacyConversionOutboxReadManager readManager;
    private final LegacyConversionOutboxCommandManager commandManager;

    public RecoverLegacyConversionTimeoutService(
            LegacyConversionOutboxReadManager readManager,
            LegacyConversionOutboxCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public int execute() {
        Instant timeoutThreshold = Instant.now().minus(TIMEOUT_THRESHOLD);
        List<LegacyConversionOutbox> timeoutOutboxes =
                readManager.findProcessingTimeoutOutboxes(timeoutThreshold, BATCH_SIZE);

        if (timeoutOutboxes.isEmpty()) {
            return 0;
        }

        log.info("PROCESSING 타임아웃 복구 시작: count={}", timeoutOutboxes.size());

        Instant now = Instant.now();
        int recoveredCount = 0;
        for (LegacyConversionOutbox outbox : timeoutOutboxes) {
            try {
                outbox.recoverFromTimeout(now);
                commandManager.persist(outbox);
                recoveredCount++;
            } catch (Exception e) {
                log.warn("타임아웃 복구 실패: legacyProductGroupId={}", outbox.legacyProductGroupId(), e);
            }
        }

        log.info(
                "PROCESSING 타임아웃 복구 완료: total={}, recovered={}",
                timeoutOutboxes.size(),
                recoveredCount);
        return recoveredCount;
    }
}
