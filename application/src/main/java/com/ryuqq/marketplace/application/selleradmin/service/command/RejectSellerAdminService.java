package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.factory.SellerAdminCommandFactory;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.RejectSellerAdminUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import org.springframework.stereotype.Service;

/**
 * RejectSellerAdminService - 셀러 관리자 가입 신청 거절 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>PENDING_APPROVAL 상태의 신청을 거절합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class RejectSellerAdminService implements RejectSellerAdminUseCase {

    private final SellerAdminCommandFactory commandFactory;
    private final SellerAdminReadManager readManager;
    private final SellerAdminCommandManager commandManager;

    public RejectSellerAdminService(
            SellerAdminCommandFactory commandFactory,
            SellerAdminReadManager readManager,
            SellerAdminCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(RejectSellerAdminCommand command) {
        StatusChangeContext<SellerAdminId> context = commandFactory.createRejectContext(command);

        SellerAdmin sellerAdmin = readManager.getById(context.id());

        sellerAdmin.reject(context.changedAt());

        commandManager.persist(sellerAdmin);
    }
}
