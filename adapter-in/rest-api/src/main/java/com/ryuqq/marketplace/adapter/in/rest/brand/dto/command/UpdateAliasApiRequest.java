package com.ryuqq.marketplace.adapter.in.rest.brand.dto.command;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 브랜드 별칭 수정 요청 DTO
 *
 * <p>브랜드 별칭(Alias)의 정보를 수정할 때 사용하는 Command DTO입니다.</p>
 * <p>모든 필드는 Optional이며, 제공된 필드만 업데이트됩니다.</p>
 *
 * <ul>
 *   <li>confidence: 매핑 신뢰도 (0.0 ~ 1.0, 선택)</li>
 *   <li>status: 별칭 상태 (선택, 예: ACTIVE, INACTIVE, DELETED)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record UpdateAliasApiRequest(
    @DecimalMin(value = "0.0", message = "신뢰도는 0.0 이상이어야 합니다")
    @DecimalMax(value = "1.0", message = "신뢰도는 1.0 이하여야 합니다")
    BigDecimal confidence,

    String status
) {}
