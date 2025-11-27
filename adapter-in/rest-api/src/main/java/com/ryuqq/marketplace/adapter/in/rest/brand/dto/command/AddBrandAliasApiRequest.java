package com.ryuqq.marketplace.adapter.in.rest.brand.dto.command;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 브랜드 별칭 추가 요청 DTO
 *
 * <p>브랜드에 별칭(Alias)을 추가할 때 사용하는 Command DTO입니다.</p>
 * <p>별칭은 셀러, 외부몰 등에서 사용하는 브랜드의 다른 이름을 매핑하기 위해 사용됩니다.</p>
 *
 * <ul>
 *   <li>aliasName: 별칭명 (필수)</li>
 *   <li>sourceType: 별칭 소스 타입 (필수, 예: SELLER, MALL, MANUAL)</li>
 *   <li>sellerId: 셀러 ID (선택, sourceType이 SELLER일 경우 필수)</li>
 *   <li>mallCode: 외부몰 코드 (선택, sourceType이 MALL일 경우 필수)</li>
 *   <li>confidence: 매핑 신뢰도 (0.0 ~ 1.0, 선택)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record AddBrandAliasApiRequest(
    @NotBlank(message = "별칭명은 필수입니다")
    @Size(max = 255, message = "별칭명은 255자 이내여야 합니다")
    String aliasName,

    @NotNull(message = "소스 타입은 필수입니다")
    String sourceType,

    Long sellerId,

    @Size(max = 50, message = "몰 코드는 50자 이내여야 합니다")
    String mallCode,

    @DecimalMin(value = "0.0", message = "신뢰도는 0.0 이상이어야 합니다")
    @DecimalMax(value = "1.0", message = "신뢰도는 1.0 이하여야 합니다")
    BigDecimal confidence
) {}
