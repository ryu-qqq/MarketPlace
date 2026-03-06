package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 연동 이력 Composition 매퍼.
 *
 * <p>SyncHistoryCompositeDto → SyncHistoryListResult 변환.
 */
@Component
public class OmsSyncHistoryCompositionMapper {

    private static final String DEFAULT_PRESET_NAME = "디폴트 프리셋";

    /**
     * composite 목록을 SyncHistoryListResult 목록으로 변환.
     *
     * @param composites composite 조회 결과
     * @return SyncHistoryListResult 목록
     */
    public List<SyncHistoryListResult> toResults(List<SyncHistoryCompositeDto> composites) {
        return composites.stream().map(this::toResult).toList();
    }

    private SyncHistoryListResult toResult(SyncHistoryCompositeDto c) {
        String statusName = c.status() != null ? c.status() : "";
        String statusLabel = resolveStatusLabel(statusName);

        return new SyncHistoryListResult(
                c.outboxId(),
                c.shopName() != null ? c.shopName() : "",
                c.accountId() != null ? c.accountId() : "",
                DEFAULT_PRESET_NAME,
                statusName,
                statusLabel,
                c.retryCount(),
                c.errorMessage(),
                c.externalProductId(),
                c.createdAt(),
                c.processedAt());
    }

    private String resolveStatusLabel(String status) {
        if (status.isBlank()) {
            return "";
        }
        try {
            return SyncStatus.valueOf(status).description();
        } catch (IllegalArgumentException e) {
            return status;
        }
    }
}
