package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerContractQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.exception.SellerErrorCode;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerContract Read Manager.
 *
 * <p>계약 정보 조회를 담당합니다.
 */
@Component
public class SellerContractReadManager {

    private final SellerContractQueryPort queryPort;

    public SellerContractReadManager(SellerContractQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerContract getById(SellerContractId id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.CONTRACT_NOT_FOUND,
                                        String.format("계약 ID %d에 해당하는 정보를 찾을 수 없습니다", id.value())));
    }

    @Transactional(readOnly = true)
    public SellerContract getBySellerId(SellerId sellerId) {
        return queryPort
                .findBySellerId(sellerId)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.CONTRACT_NOT_FOUND,
                                        String.format(
                                                "셀러 ID %d에 해당하는 계약 정보를 찾을 수 없습니다",
                                                sellerId.value())));
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerId(SellerId sellerId) {
        return queryPort.existsBySellerId(sellerId);
    }
}
