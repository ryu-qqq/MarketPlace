package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ResetSellerAdminPasswordUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * ResetSellerAdminPasswordService - 셀러 관리자 비밀번호 초기화 Service.
 *
 * <p>ACTIVE 상태이며 인증 서버에 등록된 관리자의 비밀번호를 초기화합니다. 비밀번호 초기화는 외부 인증 서버(AuthHub)에 동기적으로 요청합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
@ConditionalOnBean(SellerAdminIdentityClient.class)
public class ResetSellerAdminPasswordService implements ResetSellerAdminPasswordUseCase {

    private final SellerAdminReadManager readManager;
    private final SellerAdminIdentityClient identityClient;

    public ResetSellerAdminPasswordService(
            SellerAdminReadManager readManager, SellerAdminIdentityClient identityClient) {
        this.readManager = readManager;
        this.identityClient = identityClient;
    }

    @Override
    public void execute(ResetSellerAdminPasswordCommand command) {
        SellerAdminId sellerAdminId = SellerAdminId.of(command.sellerAdminId());
        SellerAdmin sellerAdmin = readManager.getById(sellerAdminId);

        sellerAdmin.validatePasswordResetEligibility();

        identityClient.resetSellerAdminPassword(sellerAdmin.authUserId());
    }
}
