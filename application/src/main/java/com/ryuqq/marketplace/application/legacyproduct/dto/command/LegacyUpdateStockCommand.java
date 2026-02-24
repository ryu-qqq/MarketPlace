package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import java.util.List;

/**
 * 레거시 상품 재고 수정 Command.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK (조회 결과 반환용)
 * @param commands 재고 수정 Command 목록
 */
public record LegacyUpdateStockCommand(
        long setofProductGroupId, List<UpdateProductStockCommand> commands) {}
