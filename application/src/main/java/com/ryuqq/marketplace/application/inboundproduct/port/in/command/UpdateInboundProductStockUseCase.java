package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import java.util.List;

/** 인바운드 상품 재고 수정 UseCase. */
public interface UpdateInboundProductStockUseCase {

    void execute(
            long inboundSourceId,
            String externalProductCode,
            List<UpdateProductStockCommand> stockCommands);
}
