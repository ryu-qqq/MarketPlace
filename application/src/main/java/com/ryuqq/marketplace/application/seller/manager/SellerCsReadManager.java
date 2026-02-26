package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerCsQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.exception.SellerErrorCode;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerCs Read Manager.
 *
 * <p>CS 정보 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerCsReadManager {

    private final SellerCsQueryPort queryPort;

    public SellerCsReadManager(SellerCsQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 CS 정보를 조회합니다.
     *
     * @param id CS ID
     * @return CS 정보
     * @throws SellerException CS 정보가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SellerCs getById(SellerCsId id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.SELLER_NOT_FOUND,
                                        String.format("CS ID %d에 해당하는 정보를 찾을 수 없습니다", id.value())));
    }

    /**
     * 셀러 ID로 CS 정보를 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @return CS 정보
     * @throws SellerException CS 정보가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SellerCs getBySellerId(SellerId sellerId) {
        return queryPort
                .findBySellerId(sellerId)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.SELLER_NOT_FOUND,
                                        String.format(
                                                "셀러 ID %d에 해당하는 CS 정보를 찾을 수 없습니다",
                                                sellerId.value())));
    }

    /**
     * 셀러 ID로 CS 정보 존재 여부를 확인합니다.
     *
     * @param sellerId 셀러 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsBySellerId(SellerId sellerId) {
        return queryPort.existsBySellerId(sellerId);
    }
}
