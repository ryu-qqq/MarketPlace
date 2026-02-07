package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerAdmin Command Manager.
 *
 * <p>셀러 관리자 저장/수정을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminCommandManager {

    private final SellerAdminCommandPort commandPort;

    public SellerAdminCommandManager(SellerAdminCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 셀러 관리자를 저장합니다.
     *
     * @param sellerAdmin 셀러 관리자 도메인 객체
     * @return 저장된 셀러 관리자 ID (UUIDv7 String)
     */
    @Transactional
    public String persist(SellerAdmin sellerAdmin) {
        return commandPort.persist(sellerAdmin);
    }

    /**
     * 셀러 관리자를 일괄 저장합니다.
     *
     * @param sellerAdmins 셀러 관리자 도메인 객체 목록
     */
    @Transactional
    public void persistAll(List<SellerAdmin> sellerAdmins) {
        commandPort.persistAll(sellerAdmins);
    }
}
