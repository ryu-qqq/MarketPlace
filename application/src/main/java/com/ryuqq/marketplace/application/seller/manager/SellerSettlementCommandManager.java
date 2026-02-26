package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerSettlementCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerSettlement Command Manager.
 *
 * <p>정산 정보 저장/수정을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerSettlementCommandManager {

    private final SellerSettlementCommandPort commandPort;

    public SellerSettlementCommandManager(SellerSettlementCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 정산 정보를 저장합니다.
     *
     * @param sellerSettlement 저장할 정산 정보
     * @return 저장된 ID
     */
    @Transactional
    public Long persist(SellerSettlement sellerSettlement) {
        return commandPort.persist(sellerSettlement);
    }

    /**
     * 다건의 정산 정보를 저장합니다.
     *
     * @param sellerSettlements 저장할 정산 정보 목록
     */
    @Transactional
    public void persistAll(List<SellerSettlement> sellerSettlements) {
        commandPort.persistAll(sellerSettlements);
    }
}
