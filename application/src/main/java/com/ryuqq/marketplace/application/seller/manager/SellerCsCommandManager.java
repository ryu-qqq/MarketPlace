package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerCsCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerCs Command Manager.
 *
 * <p>CS 정보 저장/수정을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerCsCommandManager {

    private final SellerCsCommandPort commandPort;

    public SellerCsCommandManager(SellerCsCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * CS 정보를 저장합니다.
     *
     * @param sellerCs 저장할 CS 정보
     * @return 저장된 ID
     */
    @Transactional
    public Long persist(SellerCs sellerCs) {
        return commandPort.persist(sellerCs);
    }

    /**
     * 다건의 CS 정보를 저장합니다.
     *
     * @param sellerCsList 저장할 CS 정보 목록
     */
    @Transactional
    public void persistAll(List<SellerCs> sellerCsList) {
        commandPort.persistAll(sellerCsList);
    }
}
