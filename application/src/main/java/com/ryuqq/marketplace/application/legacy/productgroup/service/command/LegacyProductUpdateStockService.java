package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateStockUseCase;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 재고 수정 서비스.
 *
 * <p>레거시 productId를 market PK로 변환 후, Coordinator에서 재고를 수정합니다.
 */
@Service
public class LegacyProductUpdateStockService implements LegacyProductUpdateStockUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    public LegacyProductUpdateStockService(
            LegacyProductIdResolveFactory resolveFactory,
            LegacyProductBulkCommandCoordinator bulkCommandCoordinator) {
        this.resolveFactory = resolveFactory;
        this.bulkCommandCoordinator = bulkCommandCoordinator;
    }

    @Override
    public void execute(long productGroupId, List<UpdateProductStockCommand> commands) {
        ResolvedLegacyProductIds resolved = resolveFactory.resolve(productGroupId);

        Map<ProductId, Integer> stockByProductId = new LinkedHashMap<>();
        for (UpdateProductStockCommand command : commands) {
            ProductId resolvedProductId = resolved.resolveProductId(command.productId());
            stockByProductId.put(resolvedProductId, command.stockQuantity());
        }

        bulkCommandCoordinator.updateStockByProductIds(
                resolved.resolvedProductGroupId(), stockByProductId, resolveFactory.now());
    }
}
