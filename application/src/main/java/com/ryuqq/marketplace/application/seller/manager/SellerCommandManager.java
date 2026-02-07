package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Seller Command Manager. */
@Component
public class SellerCommandManager {

    private final SellerCommandPort commandPort;

    public SellerCommandManager(SellerCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(Seller seller) {
        return commandPort.persist(seller);
    }

    @Transactional
    public void persistAll(List<Seller> sellers) {
        commandPort.persistAll(sellers);
    }

    /**
     * 셀러 인증 정보 업데이트.
     *
     * @param sellerId 셀러 ID
     * @param tenantId 인증 서버 테넌트 ID
     * @param organizationId 인증 서버 조직 ID
     */
    @Transactional
    public void updateAuthInfo(SellerId sellerId, String tenantId, String organizationId) {
        commandPort.updateAuthInfo(sellerId, tenantId, organizationId);
    }
}
