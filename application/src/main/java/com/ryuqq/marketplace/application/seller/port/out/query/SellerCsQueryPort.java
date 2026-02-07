package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;

/**
 * SellerCs Query Port.
 *
 * <p>CS 정보 조회 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerCsQueryPort {

    /**
     * ID로 CS 정보를 조회합니다.
     *
     * @param id CS ID
     * @return CS 정보 Optional
     */
    Optional<SellerCs> findById(SellerCsId id);

    /**
     * 셀러 ID로 CS 정보를 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @return CS 정보 Optional
     */
    Optional<SellerCs> findBySellerId(SellerId sellerId);

    /**
     * 셀러 ID로 CS 정보 존재 여부를 확인합니다.
     *
     * @param sellerId 셀러 ID
     * @return 존재 여부
     */
    boolean existsBySellerId(SellerId sellerId);
}
