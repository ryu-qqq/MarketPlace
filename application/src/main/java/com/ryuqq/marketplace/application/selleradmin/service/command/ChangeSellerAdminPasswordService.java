package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ChangeSellerAdminPasswordUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * ChangeSellerAdminPasswordService - 셀러 관리자 비밀번호 변경 Service.
 *
 * <p>외부 본인인증 완료 후 호출됩니다. ACTIVE 상태이며 인증 서버에 등록된 관리자의 비밀번호를 새 비밀번호로 변경합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
@ConditionalOnBean(SellerAdminIdentityClient.class)
public class ChangeSellerAdminPasswordService implements ChangeSellerAdminPasswordUseCase {

    private final SellerAdminReadManager readManager;
    private final SellerAdminIdentityClient identityClient;

    public ChangeSellerAdminPasswordService(
            SellerAdminReadManager readManager, SellerAdminIdentityClient identityClient) {
        this.readManager = readManager;
        this.identityClient = identityClient;
    }

    @Override
    public void execute(ChangeSellerAdminPasswordCommand command) {
        SellerAdminId sellerAdminId = SellerAdminId.of(command.sellerAdminId());
        SellerAdmin sellerAdmin = readManager.getById(sellerAdminId);

        sellerAdmin.validatePasswordChangeEligibility();

        identityClient.changeSellerAdminPassword(sellerAdmin.authUserId(), command.newPassword());
    }
}
