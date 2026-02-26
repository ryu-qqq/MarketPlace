package com.ryuqq.marketplace.application.sellerapplication.manager;

import com.ryuqq.marketplace.application.sellerapplication.port.out.command.SellerApplicationCommandPort;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerApplication Command Manager.
 *
 * <p>입점 신청 저장/수정을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationCommandManager {

    private final SellerApplicationCommandPort commandPort;

    public SellerApplicationCommandManager(SellerApplicationCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 입점 신청을 저장합니다.
     *
     * @param sellerApplication 저장할 입점 신청
     * @return 저장된 신청 ID
     */
    @Transactional
    public Long persist(SellerApplication sellerApplication) {
        return commandPort.persist(sellerApplication);
    }

    /**
     * 다건의 입점 신청을 저장합니다.
     *
     * @param sellerApplications 저장할 입점 신청 목록
     */
    @Transactional
    public void persistAll(List<SellerApplication> sellerApplications) {
        commandPort.persistAll(sellerApplications);
    }
}
