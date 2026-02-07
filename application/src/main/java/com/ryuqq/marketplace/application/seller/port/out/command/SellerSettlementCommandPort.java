package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import java.util.List;

/**
 * SellerSettlement Command Port.
 *
 * <p>정산 정보 저장/수정 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerSettlementCommandPort {

    /**
     * 정산 정보를 저장합니다.
     *
     * @param sellerSettlement 저장할 정산 정보
     * @return 저장된 ID
     */
    Long persist(SellerSettlement sellerSettlement);

    /**
     * 다건의 정산 정보를 저장합니다.
     *
     * @param sellerSettlements 저장할 정산 정보 목록
     */
    void persistAll(List<SellerSettlement> sellerSettlements);
}
