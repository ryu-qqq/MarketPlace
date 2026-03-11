package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupActivationOutboxCoordinator;
import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupDeactivationOutboxCoordinator;
import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupUpdateOutboxCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchChangeProductGroupStatusUseCase;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * BatchChangeProductGroupStatusService - 상품 그룹 배치 상태 변경 Service.
 *
 * <p>sellerId 조건으로 조회하여 소유권을 검증하고, 일괄 상태 변경을 수행합니다. ACTIVE로 변경 시
 * OutboundSyncOutbox/OutboundProduct도 생성합니다.
 */
@Service
public class BatchChangeProductGroupStatusService implements BatchChangeProductGroupStatusUseCase {

    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;
    private final ProductGroupActivationOutboxCoordinator activationOutboxCoordinator;
    private final ProductGroupDeactivationOutboxCoordinator deactivationOutboxCoordinator;
    private final ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator;

    public BatchChangeProductGroupStatusService(
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager,
            ProductGroupActivationOutboxCoordinator activationOutboxCoordinator,
            ProductGroupDeactivationOutboxCoordinator deactivationOutboxCoordinator,
            ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.activationOutboxCoordinator = activationOutboxCoordinator;
        this.deactivationOutboxCoordinator = deactivationOutboxCoordinator;
        this.updateOutboxCoordinator = updateOutboxCoordinator;
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

            if (targetStatus.isActive()) {
                activationOutboxCoordinator.createOutboxAndProducts(productGroup);
            } else if (targetStatus.requiresExternalDeregistration()) {
                deactivationOutboxCoordinator.createDeleteOutboxesIfNeeded(productGroup.id());
            } else if (targetStatus.isSoldout()) {
                updateOutboxCoordinator.createUpdateOutboxesIfNeeded(
                        productGroup.id(), Set.of(ChangedArea.STATUS));
            }
        }
    }
}
