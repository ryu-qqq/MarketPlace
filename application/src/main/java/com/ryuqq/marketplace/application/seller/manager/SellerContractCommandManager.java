package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerContractCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerContract Command Manager.
 *
 * <p>계약 정보 저장/수정을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerContractCommandManager {

    private final SellerContractCommandPort commandPort;

    public SellerContractCommandManager(SellerContractCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 계약 정보를 저장합니다.
     *
     * @param sellerContract 저장할 계약 정보
     * @return 저장된 ID
     */
    @Transactional
    public Long persist(SellerContract sellerContract) {
        return commandPort.persist(sellerContract);
    }

    /**
     * 다건의 계약 정보를 저장합니다.
     *
     * @param sellerContracts 저장할 계약 정보 목록
     */
    @Transactional
    public void persistAll(List<SellerContract> sellerContracts) {
        commandPort.persistAll(sellerContracts);
    }
}
