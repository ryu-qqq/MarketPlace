package com.ryuqq.marketplace.application.legacy.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import java.util.List;

/** 레거시 상품 재고 수정 UseCase. */
public interface LegacyProductUpdateStockUseCase {

    void execute(long productGroupId, List<UpdateProductStockCommand> commands);
}
