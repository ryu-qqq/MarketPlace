package com.ryuqq.marketplace.application.productnotice.service.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.factory.ProductNoticeCommandFactory;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.application.productnotice.port.in.command.UpdateProductNoticeUseCase;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * UpdateProductNoticeService - 상품 그룹 고시정보 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>APP-TRX-001: @Transactional은 Port-Out (Adapter)에서 처리
 */
@Service
public class UpdateProductNoticeService implements UpdateProductNoticeUseCase {

    private final ProductNoticeCommandFactory commandFactory;
    private final ProductNoticeReadManager readManager;
    private final ProductNoticeCommandManager commandManager;

    public UpdateProductNoticeService(
            ProductNoticeCommandFactory commandFactory,
            ProductNoticeReadManager readManager,
            ProductNoticeCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateProductNoticeCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        Optional<ProductNotice> existingOpt = readManager.findByProductGroupId(productGroupId);

        ProductNotice notice = commandFactory.createOrUpdateNotice(command, existingOpt);

        commandManager.persist(notice);
    }
}
