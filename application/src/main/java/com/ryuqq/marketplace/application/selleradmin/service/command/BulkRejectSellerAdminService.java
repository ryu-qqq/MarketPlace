package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.factory.SellerAdminCommandFactory;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkRejectSellerAdminUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * BulkRejectSellerAdminService - 셀러 관리자 가입 신청 일괄 거절 Service.
 *
 * <p>PENDING_APPROVAL 상태의 신청을 일괄 거절합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class BulkRejectSellerAdminService implements BulkRejectSellerAdminUseCase {

    private final SellerAdminCommandFactory commandFactory;
    private final SellerAdminReadManager readManager;
    private final SellerAdminCommandManager commandManager;

    public BulkRejectSellerAdminService(
            SellerAdminCommandFactory commandFactory,
            SellerAdminReadManager readManager,
            SellerAdminCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(BulkRejectSellerAdminCommand command) {
        BulkStatusChangeContext<SellerAdminId> context =
                commandFactory.createBulkRejectContext(command);

        List<SellerAdmin> sellerAdmins = readManager.getAllByIds(context.ids());

        for (SellerAdmin sellerAdmin : sellerAdmins) {
            sellerAdmin.reject(context.changedAt());
        }

        commandManager.persistAll(sellerAdmins);
    }
}
