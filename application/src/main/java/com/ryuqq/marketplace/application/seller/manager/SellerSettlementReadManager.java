package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerSettlementQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.exception.SellerErrorCode;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.id.SellerSettlementId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerSettlement Read Manager.
 *
 * <p>정산 정보 조회를 담당합니다.
 */
@Component
public class SellerSettlementReadManager {

    private final SellerSettlementQueryPort queryPort;

    public SellerSettlementReadManager(SellerSettlementQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerSettlement getById(SellerSettlementId id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.SETTLEMENT_NOT_FOUND,
                                        String.format("정산 ID %d에 해당하는 정보를 찾을 수 없습니다", id.value())));
    }

    @Transactional(readOnly = true)
    public SellerSettlement getBySellerId(SellerId sellerId) {
        return queryPort
                .findBySellerId(sellerId)
                .orElseThrow(
                        () ->
                                new SellerException(
                                        SellerErrorCode.SETTLEMENT_NOT_FOUND,
                                        String.format(
                                                "셀러 ID %d에 해당하는 정산 정보를 찾을 수 없습니다",
                                                sellerId.value())));
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerId(SellerId sellerId) {
        return queryPort.existsBySellerId(sellerId);
    }
}
