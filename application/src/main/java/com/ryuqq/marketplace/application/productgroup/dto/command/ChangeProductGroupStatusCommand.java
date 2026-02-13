package com.ryuqq.marketplace.application.productgroup.dto.command;

/**
 * 상품 그룹 상태 변경 Command.
 *
 * @param productGroupId 상품 그룹 ID
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLDOUT", "DELETED")
 */
public record ChangeProductGroupStatusCommand(long productGroupId, String targetStatus) {}
