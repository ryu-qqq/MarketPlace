package com.ryuqq.marketplace.adapter.in.rest.brand.dto.command;

import jakarta.validation.constraints.NotNull;

/**
 * 브랜드 상태 변경 요청 DTO
 *
 * <p>브랜드의 활성화/비활성화 상태를 변경할 때 사용하는 Command DTO입니다.</p>
 *
 * <ul>
 *   <li>status: 변경할 브랜드 상태 (예: ACTIVE, INACTIVE, DELETED)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record ChangeBrandStatusApiRequest(
    @NotNull(message = "상태는 필수입니다")
    String status
) {}
