package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import java.util.List;

/**
 * SellerCs Command Port.
 *
 * <p>CS 정보 저장/수정 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerCsCommandPort {

    /**
     * CS 정보를 저장합니다.
     *
     * @param sellerCs 저장할 CS 정보
     * @return 저장된 ID
     */
    Long persist(SellerCs sellerCs);

    /**
     * 다건의 CS 정보를 저장합니다.
     *
     * @param sellerCsList 저장할 CS 정보 목록
     */
    void persistAll(List<SellerCs> sellerCsList);
}
