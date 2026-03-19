package com.ryuqq.marketplace.application.legacyconversion.service.command;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyOrderConversionResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.SeedLegacyOrderConversionUseCase;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderIdScanPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderScanEntry;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 주문 벌크 변환 시딩 서비스.
 *
 * <p>luxurydb의 활성 주문을 커서 기반으로 스캔하고, 아직 Outbox에 등록되지 않은 건에 대해 PENDING 엔트리를 생성합니다.
 *
 * <p>APP-SVC-001: Service는 UseCase 구현.
 *
 * <p>APP-SVC-002: Service에서 Manager/Port 조합으로 비즈니스 로직 수행.
 */
@Service
public class SeedLegacyOrderConversionService implements SeedLegacyOrderConversionUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(SeedLegacyOrderConversionService.class);

    private final LegacyOrderIdScanPort scanPort;
    private final LegacyOrderIdMappingReadManager mappingReadManager;
    private final LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    public SeedLegacyOrderConversionService(
            LegacyOrderIdScanPort scanPort,
            LegacyOrderIdMappingReadManager mappingReadManager,
            LegacyOrderConversionOutboxCommandManager outboxCommandManager) {
        this.scanPort = scanPort;
        this.mappingReadManager = mappingReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    public SeedLegacyOrderConversionResult execute(long cursorAfterOrderId, int batchSize) {
        List<LegacyOrderScanEntry> entries =
                scanPort.findActiveOrderEntries(cursorAfterOrderId, batchSize);

        if (entries.isEmpty()) {
            log.info("전체 스캔 완료, 더 이상 활성 주문 없음");
            return SeedLegacyOrderConversionResult.allCompleted();
        }

        List<LegacyOrderScanEntry> newEntries = new ArrayList<>();
        for (LegacyOrderScanEntry entry : entries) {
            if (!mappingReadManager.existsByLegacyOrderId(entry.orderId())) {
                newEntries.add(entry);
            }
        }

        Instant now = Instant.now();
        for (LegacyOrderScanEntry entry : newEntries) {
            LegacyOrderConversionOutbox outbox =
                    LegacyOrderConversionOutbox.forNew(entry.orderId(), entry.paymentId(), now);
            outboxCommandManager.persist(outbox);
        }

        long lastCursor = entries.getLast().orderId();
        int skipped = entries.size() - newEntries.size();

        log.info(
                "주문 시딩 결과: scanned={}, created={}, skipped={}, lastCursor={}",
                entries.size(),
                newEntries.size(),
                skipped,
                lastCursor);

        return SeedLegacyOrderConversionResult.of(
                entries.size(), newEntries.size(), skipped, lastCursor);
    }
}
