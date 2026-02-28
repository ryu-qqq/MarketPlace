package com.ryuqq.marketplace.application.legacyconversion.service.command;

import com.ryuqq.marketplace.application.legacyconversion.dto.command.SeedLegacyConversionCommand;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.SeedLegacyConversionResult;
import com.ryuqq.marketplace.application.legacyconversion.port.in.command.SeedLegacyConversionUseCase;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyConversionOutboxCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductGroupIdScanPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 벌크 변환 시딩 서비스.
 *
 * <p>luxurydb의 활성 상품그룹 ID를 커서 기반으로 스캔하고, 아직 Outbox에 등록되지 않은 건에 대해 PENDING 엔트리를 생성합니다.
 *
 * <p>APP-SVC-001: Service는 UseCase 구현.
 *
 * <p>APP-SVC-002: Service에서 Manager/Port 조합으로 비즈니스 로직 수행.
 */
@Service
public class SeedLegacyConversionService implements SeedLegacyConversionUseCase {

    private static final Logger log = LoggerFactory.getLogger(SeedLegacyConversionService.class);

    private final LegacyProductGroupIdScanPort scanPort;
    private final LegacyConversionOutboxQueryPort outboxQueryPort;
    private final LegacyConversionOutboxCommandPort outboxCommandPort;

    public SeedLegacyConversionService(
            LegacyProductGroupIdScanPort scanPort,
            LegacyConversionOutboxQueryPort outboxQueryPort,
            LegacyConversionOutboxCommandPort outboxCommandPort) {
        this.scanPort = scanPort;
        this.outboxQueryPort = outboxQueryPort;
        this.outboxCommandPort = outboxCommandPort;
    }

    @Override
    @Transactional
    public SeedLegacyConversionResult execute(SeedLegacyConversionCommand command) {
        if (isMaxTotalReached(command.maxTotal())) {
            log.info("max-total 도달, 시딩 건너뜀. maxTotal={}", command.maxTotal());
            return SeedLegacyConversionResult.allCompleted();
        }

        List<Long> scannedIds =
                scanPort.findActiveProductGroupIdsAfter(
                        command.cursorAfterProductGroupId(), command.batchSize());

        if (scannedIds.isEmpty()) {
            log.info("전체 스캔 완료, 더 이상 활성 상품그룹 없음");
            return SeedLegacyConversionResult.allCompleted();
        }

        Set<Long> existingIds = outboxQueryPort.findExistingLegacyProductGroupIds(scannedIds);

        List<Long> newIds = new ArrayList<>();
        for (Long id : scannedIds) {
            if (!existingIds.contains(id)) {
                newIds.add(id);
            }
        }

        Instant now = Instant.now();
        for (Long newId : newIds) {
            LegacyConversionOutbox outbox = LegacyConversionOutbox.forNew(newId, now);
            outboxCommandPort.persist(outbox);
        }

        long lastCursor = scannedIds.getLast();
        int skipped = scannedIds.size() - newIds.size();

        log.info(
                "시딩 결과: scanned={}, created={}, skipped={}, lastCursor={}",
                scannedIds.size(),
                newIds.size(),
                skipped,
                lastCursor);

        return SeedLegacyConversionResult.of(scannedIds.size(), newIds.size(), skipped, lastCursor);
    }

    private boolean isMaxTotalReached(int maxTotal) {
        if (maxTotal <= 0) {
            return false;
        }
        long currentCount = outboxQueryPort.countDistinctLegacyProductGroupIds();
        return currentCount >= maxTotal;
    }
}
