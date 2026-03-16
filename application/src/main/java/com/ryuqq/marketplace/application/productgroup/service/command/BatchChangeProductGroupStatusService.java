package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupStatusChangeOutboxCoordinator;
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
 * <p>sellerId 조건으로 조회하여 소유권을 검증하고, 일괄 상태 변경을 수행합니다. 상태 변경 후 외부 연동 Outbox
 * 처리는 ProductGroupStatusChangeOutboxCoordinator에 위임합니다.
 */
@Service
public class BatchChangeProductGroupStatusService implements BatchChangeProductGroupStatusUseCase {

    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;
    private final ProductGroupStatusChangeOutboxCoordinator statusChangeOutboxCoordinator;

    public BatchChangeProductGroupStatusService(
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager,
            ProductGroupStatusChangeOutboxCoordinator statusChangeOutboxCoordinator) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.statusChangeOutboxCoordinator = statusChangeOutboxCoordinator;
    }

    @Override
    public void execute(BatchChangeProductGroupStatusCommand command) {
        List<ProductGroupId> ids =
                command.productGroupIds().stream().map(ProductGroupId::of).toList();

        List<ProductGroup> productGroups =
                command.sellerId() != null
                        ? readManager.getByIdsAndSellerId(ids, command.sellerId())
                        : readManager.findByIds(ids);

        ProductGroupStatus targetStatus = ProductGroupStatus.valueOf(command.targetStatus());
        Instant changedAt = Instant.now();

        for (ProductGroup productGroup : productGroups) {
            productGroup.changeStatus(targetStatus, changedAt);
            commandManager.persist(productGroup);
        }

        statusChangeOutboxCoordinator.processOutboxes(productGroups, targetStatus);
    }
}
