package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;

/**
 * SellerContract Query Port.
 *
 * <p>계약 정보 조회 포트입니다.
 */
public interface SellerContractQueryPort {

    Optional<SellerContract> findById(SellerContractId id);

    Optional<SellerContract> findBySellerId(SellerId sellerId);

    boolean existsBySellerId(SellerId sellerId);
}
