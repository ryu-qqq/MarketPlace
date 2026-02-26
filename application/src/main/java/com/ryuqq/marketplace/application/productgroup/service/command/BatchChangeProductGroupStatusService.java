package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchChangeProductGroupStatusUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * BatchChangeProductGroupStatusService - 상품 그룹 배치 상태 변경 Service.
 *
 * <p>sellerId 조건으로 조회하여 소유권을 검증하고, 일괄 상태 변경을 수행합니다.
 */
@Service
public class BatchChangeProductGroupStatusService implements BatchChangeProductGroupStatusUseCase {

    private final TimeProvider timeProvider;
    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;

    public BatchChangeProductGroupStatusService(
            TimeProvider timeProvider,
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager) {
        this.timeProvider = timeProvider;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(BatchChangeProductGroupStatusCommand command) {
        List<ProductGroupId> ids =
                command.productGroupIds().stream().map(ProductGroupId::of).toList();

        List<ProductGroup> productGroups = readManager.getByIdsAndSellerId(ids, command.sellerId());

        ProductGroupStatus targetStatus = ProductGroupStatus.valueOf(command.targetStatus());
        Instant changedAt = timeProvider.now();

        for (ProductGroup productGroup : productGroups) {
            productGroup.changeStatus(targetStatus, changedAt);
            commandManager.persist(productGroup);
        }
    }
}
