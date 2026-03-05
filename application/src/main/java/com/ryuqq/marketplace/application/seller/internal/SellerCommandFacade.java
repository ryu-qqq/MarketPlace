package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.application.seller.dto.bundle.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.dto.bundle.SellerUpdateBundle;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerContractCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCsCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerSettlementCommandManager;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 셀러 Command Facade.
 *
 * <p>Seller + BusinessInfo + CS + Contract + Settlement를 하나의 트랜잭션으로 처리합니다. (모두 1:1 관계)
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 처리합니다.
 */
@Component
public class SellerCommandFacade {

    private final SellerCommandManager sellerCommandManager;
    private final SellerBusinessInfoCommandManager businessInfoCommandManager;
    private final SellerCsCommandManager csCommandManager;
    private final SellerContractCommandManager contractCommandManager;
    private final SellerSettlementCommandManager settlementCommandManager;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public SellerCommandFacade(
            SellerCommandManager sellerCommandManager,
            SellerBusinessInfoCommandManager businessInfoCommandManager,
            SellerCsCommandManager csCommandManager,
            SellerContractCommandManager contractCommandManager,
            SellerSettlementCommandManager settlementCommandManager,
            Optional<SetofSyncOutboxCommandManager> setofSyncOutboxCommandManager) {
        this.sellerCommandManager = sellerCommandManager;
        this.businessInfoCommandManager = businessInfoCommandManager;
        this.csCommandManager = csCommandManager;
        this.contractCommandManager = contractCommandManager;
        this.settlementCommandManager = settlementCommandManager;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager.orElse(null);
    }

    /**
     * 셀러 등록 번들을 저장합니다.
     *
     * <p>Seller → BusinessInfo 순서로 저장하며, 모든 저장이 하나의 트랜잭션으로 처리됩니다. (모두 1:1 관계, 필수)
     *
     * @param bundle 셀러 등록 번들
     * @return 생성된 셀러 ID
     */
    @Transactional
    public Long registerSeller(SellerRegistrationBundle bundle) {
        Long sellerId = sellerCommandManager.persist(bundle.seller());
        bundle.withSellerId(SellerId.of(sellerId));

        businessInfoCommandManager.persist(bundle.businessInfo());
        createSetofSyncOutbox(
                SellerId.of(sellerId),
                sellerId,
                SetofSyncEntityType.SELLER,
                SetofSyncOperationType.CREATE,
                bundle.seller().createdAt());

        return sellerId;
    }

    /**
     * 셀러 수정 번들을 저장합니다.
     *
     * <p>Seller → BusinessInfo → CS → Contract → Settlement 순서로 수정하며, 모든 수정이 하나의 트랜잭션으로 처리됩니다. (모두
     * 1:1 관계, 필수)
     *
     * @param bundle 셀러 수정 번들 (검증된 Domain 객체 포함)
     */
    @Transactional
    public void updateSeller(SellerUpdateBundle bundle) {
        Seller seller = bundle.seller();
        SellerBusinessInfo businessInfo = bundle.businessInfo();
        SellerCs sellerCs = bundle.sellerCs();
        SellerContract sellerContract = bundle.sellerContract();
        SellerSettlement sellerSettlement = bundle.sellerSettlement();
        SellerCsUpdateData csUpdateData = bundle.csUpdateData();

        // 도메인 업데이트
        seller.update(bundle.sellerUpdateData(), bundle.changedAt());
        businessInfo.update(bundle.businessInfoUpdateData(), bundle.changedAt());
        sellerCs.update(
                csUpdateData.csContact(),
                csUpdateData.operatingHours(),
                csUpdateData.operatingDays(),
                csUpdateData.kakaoChannelUrl(),
                bundle.changedAt());
        sellerContract.update(bundle.contractUpdateData(), bundle.changedAt());
        sellerSettlement.update(bundle.settlementUpdateData(), bundle.changedAt());

        // 영속화
        sellerCommandManager.persist(seller);
        businessInfoCommandManager.persist(businessInfo);
        csCommandManager.persist(sellerCs);
        contractCommandManager.persist(sellerContract);
        settlementCommandManager.persist(sellerSettlement);
        createSetofSyncOutbox(
                seller.id(),
                seller.idValue(),
                SetofSyncEntityType.SELLER,
                SetofSyncOperationType.UPDATE,
                bundle.changedAt());
    }

    private void createSetofSyncOutbox(
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            java.time.Instant now) {
        if (setofSyncOutboxCommandManager != null) {
            SetofSyncOutbox outbox =
                    SetofSyncOutbox.forNew(sellerId, entityId, entityType, operationType, now);
            setofSyncOutboxCommandManager.persist(outbox);
        }
    }
}
