package com.ryuqq.marketplace.application.productgroup.dto.command;

import java.util.List;

/**
 * 상품 그룹 배치 상태 변경 Command.
 *
 * @param sellerId 셀러 ID (소유권 검증용)
 * @param productGroupIds 상품 그룹 ID 목록
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLD_OUT", "DELETED")
 */
public record BatchChangeProductGroupStatusCommand(
        Long sellerId, List<Long> productGroupIds, String targetStatus) {}
