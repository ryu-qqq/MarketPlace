package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.productgroup.dto.command.ChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.ChangeProductGroupStatusUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import org.springframework.stereotype.Service;

/**
 * ChangeProductGroupStatusService - 상품 그룹 상태 변경 Service.
 *
 * <p>targetStatus에 따라 activate/deactivate/markSoldOut/delete 도메인 메서드를 호출합니다. activate 시 THUMBNAIL
 * 이미지 필수 검증은 도메인에서 수행합니다.
 */
@Service
public class ChangeProductGroupStatusService implements ChangeProductGroupStatusUseCase {

    private final ProductGroupCommandFactory commandFactory;
    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;

    public ChangeProductGroupStatusService(
            ProductGroupCommandFactory commandFactory,
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(ChangeProductGroupStatusCommand command) {
        StatusChangeContext<ProductGroupId> context =
                commandFactory.createStatusChangeContext(command);

        ProductGroup productGroup = readManager.getById(context.id());
        ProductGroupStatus targetStatus = ProductGroupStatus.valueOf(command.targetStatus());

        switch (targetStatus) {
            case ACTIVE -> productGroup.activate(context.changedAt());
            case INACTIVE -> productGroup.deactivate(context.changedAt());
            case SOLDOUT -> productGroup.markSoldOut(context.changedAt());
            case DELETED -> productGroup.delete(context.changedAt());
            default -> throw new IllegalArgumentException("지원하지 않는 상태 변경입니다: " + targetStatus);
        }

        commandManager.persist(productGroup);
    }
}
