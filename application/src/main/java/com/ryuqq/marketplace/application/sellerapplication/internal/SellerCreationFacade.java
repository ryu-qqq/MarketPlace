package com.ryuqq.marketplace.application.sellerapplication.internal;

import com.ryuqq.marketplace.application.seller.manager.SellerAuthOutboxCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerContractCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCsCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerSettlementCommandManager;
import com.ryuqq.marketplace.application.sellerapplication.dto.bundle.SellerCreationBundle;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationCommandManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seller 생성 Facade.
 *
 * <p>입점 신청 승인 시 Seller 관련 aggregate들과 Application 상태를 저장합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerCreationFacade {

    private final SellerCommandManager sellerCommandManager;
    private final SellerBusinessInfoCommandManager businessInfoCommandManager;
    private final SellerCsCommandManager csCommandManager;
    private final SellerContractCommandManager contractCommandManager;
    private final SellerSettlementCommandManager settlementCommandManager;
    private final SellerAuthOutboxCommandManager authOutboxCommandManager;
    private final SellerApplicationCommandManager applicationCommandManager;

    public SellerCreationFacade(
            SellerCommandManager sellerCommandManager,
            SellerBusinessInfoCommandManager businessInfoCommandManager,
            SellerCsCommandManager csCommandManager,
            SellerContractCommandManager contractCommandManager,
            SellerSettlementCommandManager settlementCommandManager,
            SellerAuthOutboxCommandManager authOutboxCommandManager,
            SellerApplicationCommandManager applicationCommandManager) {
        this.sellerCommandManager = sellerCommandManager;
        this.businessInfoCommandManager = businessInfoCommandManager;
        this.csCommandManager = csCommandManager;
        this.contractCommandManager = contractCommandManager;
        this.settlementCommandManager = settlementCommandManager;
        this.authOutboxCommandManager = authOutboxCommandManager;
        this.applicationCommandManager = applicationCommandManager;
    }

    /**
     * Seller 관련 aggregate들을 저장하고 Application을 승인 처리합니다.
     *
     * @param bundle Seller 생성에 필요한 데이터 번들
     * @param application SellerApplication
     * @param processedBy 처리자 식별자
     * @param now 처리 시각
     */
    @Transactional
    public void approveAndPersist(
            SellerCreationBundle bundle,
            SellerApplication application,
            String processedBy,
            Instant now) {
        Long sellerId = persistSeller(bundle);
        SellerId sid = SellerId.of(sellerId);
        application.approve(sid, processedBy, now);
        applicationCommandManager.persist(application);
    }

    /**
     * Seller 관련 aggregate들만 저장합니다.
     *
     * @param bundle Seller 생성에 필요한 데이터 번들
     * @return 생성된 Seller ID
     */
    public Long persistSeller(SellerCreationBundle bundle) {
        Long sellerId = sellerCommandManager.persist(bundle.seller());
        bundle.withSellerId(SellerId.of(sellerId));

        businessInfoCommandManager.persist(bundle.businessInfo());
        csCommandManager.persist(bundle.sellerCs());
        contractCommandManager.persist(bundle.sellerContract());
        settlementCommandManager.persist(bundle.sellerSettlement());
        authOutboxCommandManager.persist(bundle.authOutbox());

        return sellerId;
    }
}
