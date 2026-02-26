package com.ryuqq.marketplace.application.selleradmin.internal;

import com.ryuqq.marketplace.application.selleradmin.dto.bundle.SellerAdminApprovalBundle;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminCommandManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 셀러 관리자 승인 Facade.
 *
 * <p>셀러 관리자 승인 시 관련 aggregate들을 저장합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminApprovalFacade {

    private final SellerAdminCommandManager sellerAdminCommandManager;
    private final SellerAdminAuthOutboxCommandManager authOutboxCommandManager;

    public SellerAdminApprovalFacade(
            SellerAdminCommandManager sellerAdminCommandManager,
            SellerAdminAuthOutboxCommandManager authOutboxCommandManager) {
        this.sellerAdminCommandManager = sellerAdminCommandManager;
        this.authOutboxCommandManager = authOutboxCommandManager;
    }

    /**
     * 셀러 관리자를 승인 처리하고 저장합니다.
     *
     * <p>1. SellerAdmin 승인 상태로 변경 (PENDING_APPROVAL → ACTIVE)
     *
     * <p>2. AuthOutbox 저장
     *
     * <p>3. SellerAdmin 저장
     *
     * <p>이벤트는 SellerAdmin.approve() 내부에서 등록됩니다.
     *
     * @param bundle 승인 Bundle (SellerAdmin + AuthOutbox + createdAt)
     */
    @Transactional
    public void approveAndPersist(SellerAdminApprovalBundle bundle) {
        SellerAdmin sellerAdmin = bundle.sellerAdmin();

        sellerAdmin.approve(bundle.createdAt());

        authOutboxCommandManager.persist(bundle.authOutbox());

        sellerAdminCommandManager.persist(sellerAdmin);
    }
}
