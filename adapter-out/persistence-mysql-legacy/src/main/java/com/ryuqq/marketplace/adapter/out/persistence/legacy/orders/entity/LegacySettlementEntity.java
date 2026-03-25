package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacySettlementEntity - 레거시 정산 엔티티.
 *
 * <p>레거시 DB의 settlement 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "settlement")
public class LegacySettlementEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private long orderId;

    @Column(name = "SELLER_COMMISSION_RATE")
    private long sellerCommissionRate;

    @Column(name = "DIRECT_DISCOUNT_PRICE")
    private long directDiscountPrice;

    @Column(name = "USE_MILEAGE_AMOUNT")
    private long useMileageAmount;

    @Column(name = "DIRECT_DISCOUNT_SELLER_BURDEN_RATIO")
    private long directDiscountSellerBurdenRatio;

    @Column(name = "MILEAGE_SELLER_BURDEN_RATIO")
    private long mileageSellerBurdenRatio;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacySettlementEntity() {}

    public static LegacySettlementEntity create(long orderId, long sellerCommissionRate, String operator) {
        LegacySettlementEntity entity = new LegacySettlementEntity();
        entity.orderId = orderId;
        entity.sellerCommissionRate = sellerCommissionRate;
        entity.directDiscountPrice = 0L;
        entity.useMileageAmount = 0L;
        entity.directDiscountSellerBurdenRatio = 0L;
        entity.mileageSellerBurdenRatio = 0L;
        entity.deleteYn = "N";
        entity.initAuditFields(operator);
        return entity;
    }

    public Long getId() {
        return id;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getSellerCommissionRate() {
        return sellerCommissionRate;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
