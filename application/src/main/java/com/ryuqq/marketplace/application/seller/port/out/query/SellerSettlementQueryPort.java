package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.id.SellerSettlementId;
import java.util.Optional;

/**
 * SellerSettlement Query Port.
 *
 * <p>정산 정보 조회 포트입니다.
 */
public interface SellerSettlementQueryPort {

    Optional<SellerSettlement> findById(SellerSettlementId id);

    Optional<SellerSettlement> findBySellerId(SellerId sellerId);

    boolean existsBySellerId(SellerId sellerId);
}
