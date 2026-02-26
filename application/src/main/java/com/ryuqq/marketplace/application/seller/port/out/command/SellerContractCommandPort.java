package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import java.util.List;

/**
 * SellerContract Command Port.
 *
 * <p>계약 정보 저장/수정 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerContractCommandPort {

    /**
     * 계약 정보를 저장합니다.
     *
     * @param sellerContract 저장할 계약 정보
     * @return 저장된 ID
     */
    Long persist(SellerContract sellerContract);

    /**
     * 다건의 계약 정보를 저장합니다.
     *
     * @param sellerContracts 저장할 계약 정보 목록
     */
    void persistAll(List<SellerContract> sellerContracts);
}
