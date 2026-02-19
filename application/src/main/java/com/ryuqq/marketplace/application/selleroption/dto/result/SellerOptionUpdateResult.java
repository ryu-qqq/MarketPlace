package com.ryuqq.marketplace.application.selleroption.dto.result;

import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;

/**
 * SellerOption 수정 결과.
 *
 * <p>DB persist 후 실제 생성된 ID가 반영된 활성 SellerOptionValueId 목록과 diff 발생 시각을 포함합니다.
 *
 * @param resolvedActiveValueIds persist 후 null ID가 실제 생성 ID로 치환된 활성 값 목록
 * @param occurredAt diff 발생 시각
 */
public record SellerOptionUpdateResult(
        List<SellerOptionValueId> resolvedActiveValueIds, Instant occurredAt) {}
