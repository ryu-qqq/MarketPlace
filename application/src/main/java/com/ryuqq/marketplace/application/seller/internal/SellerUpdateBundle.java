package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContractUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlementUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * 셀러 전체 수정 Bundle.
 *
 * <p>Seller + BusinessInfo + CS + Contract + Settlement 수정 데이터를 묶어서 관리합니다. (모두 1:1 관계)
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 수정합니다.
 *
 * <p>검증 후 Domain 객체가 설정되며, Facade에서 일괄 수정합니다.
 */
public class SellerUpdateBundle {

    private final SellerId sellerId;
    private final SellerUpdateData sellerUpdateData;
    private final SellerBusinessInfoUpdateData businessInfoUpdateData;
    private final SellerCsUpdateData csUpdateData;
    private final SellerContractUpdateData contractUpdateData;
    private final SellerSettlementUpdateData settlementUpdateData;
    private final Instant changedAt;

    // 검증 후 설정되는 Domain 객체들
    private Seller seller;
    private SellerBusinessInfo businessInfo;
    private SellerCs sellerCs;
    private SellerContract sellerContract;
    private SellerSettlement sellerSettlement;

    public SellerUpdateBundle(
            SellerId sellerId,
            SellerUpdateData sellerUpdateData,
            SellerBusinessInfoUpdateData businessInfoUpdateData,
            SellerCsUpdateData csUpdateData,
            SellerContractUpdateData contractUpdateData,
            SellerSettlementUpdateData settlementUpdateData,
            Instant changedAt) {
        this.sellerId = sellerId;
        this.sellerUpdateData = sellerUpdateData;
        this.businessInfoUpdateData = businessInfoUpdateData;
        this.csUpdateData = csUpdateData;
        this.contractUpdateData = contractUpdateData;
        this.settlementUpdateData = settlementUpdateData;
        this.changedAt = changedAt;
    }

    /**
     * 검증된 Domain 객체들을 설정합니다.
     *
     * @param seller 검증된 Seller
     * @param businessInfo 검증된 SellerBusinessInfo
     * @param sellerCs 검증된 SellerCs
     * @param sellerContract 검증된 SellerContract
     * @param sellerSettlement 검증된 SellerSettlement
     */
    public void withValidatedEntities(
            Seller seller,
            SellerBusinessInfo businessInfo,
            SellerCs sellerCs,
            SellerContract sellerContract,
            SellerSettlement sellerSettlement) {
        this.seller = seller;
        this.businessInfo = businessInfo;
        this.sellerCs = sellerCs;
        this.sellerContract = sellerContract;
        this.sellerSettlement = sellerSettlement;
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public SellerUpdateData sellerUpdateData() {
        return sellerUpdateData;
    }

    public SellerBusinessInfoUpdateData businessInfoUpdateData() {
        return businessInfoUpdateData;
    }

    public SellerCsUpdateData csUpdateData() {
        return csUpdateData;
    }

    public SellerContractUpdateData contractUpdateData() {
        return contractUpdateData;
    }

    public SellerSettlementUpdateData settlementUpdateData() {
        return settlementUpdateData;
    }

    public Instant changedAt() {
        return changedAt;
    }

    public Seller seller() {
        return seller;
    }

    public SellerBusinessInfo businessInfo() {
        return businessInfo;
    }

    public SellerCs sellerCs() {
        return sellerCs;
    }

    public SellerContract sellerContract() {
        return sellerContract;
    }

    public SellerSettlement sellerSettlement() {
        return sellerSettlement;
    }
}
