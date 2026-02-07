package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;

/** Seller Command Port. */
public interface SellerCommandPort {

    Long persist(Seller seller);

    void persistAll(List<Seller> sellers);

    /**
     * 셀러 인증 정보 업데이트.
     *
     * @param sellerId 셀러 ID
     * @param tenantId 인증 서버 테넌트 ID
     * @param organizationId 인증 서버 조직 ID
     */
    void updateAuthInfo(SellerId sellerId, String tenantId, String organizationId);
}
