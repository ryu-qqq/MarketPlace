package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** OrderExchange JPA 엔티티. 교환 상세 (클레임의 자식). */
@Entity
@Table(name = "order_exchanges")
public class OrderExchangeJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "claim_id", nullable = false)
    private long claimId;

    @Column(name = "new_product_group_id")
    private Long newProductGroupId;

    @Column(name = "new_product_id")
    private Long newProductId;

    @Column(name = "new_option_name", length = 500)
    private String newOptionName;

    @Column(name = "price_difference", nullable = false)
    private int priceDifference;

    @Column(name = "new_order_id", length = 36)
    private String newOrderId;

    @Column(name = "new_order_number", length = 50)
    private String newOrderNumber;

    protected OrderExchangeJpaEntity() {
        super();
    }

    private OrderExchangeJpaEntity(
            Long id,
            long claimId,
            Long newProductGroupId,
            Long newProductId,
            String newOptionName,
            int priceDifference,
            String newOrderId,
            String newOrderNumber,
            java.time.Instant createdAt,
            java.time.Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.claimId = claimId;
        this.newProductGroupId = newProductGroupId;
        this.newProductId = newProductId;
        this.newOptionName = newOptionName;
        this.priceDifference = priceDifference;
        this.newOrderId = newOrderId;
        this.newOrderNumber = newOrderNumber;
    }

    public static OrderExchangeJpaEntity create(
            Long id,
            long claimId,
            Long newProductGroupId,
            Long newProductId,
            String newOptionName,
            int priceDifference,
            String newOrderId,
            String newOrderNumber,
            java.time.Instant createdAt,
            java.time.Instant updatedAt) {
        return new OrderExchangeJpaEntity(
                id,
                claimId,
                newProductGroupId,
                newProductId,
                newOptionName,
                priceDifference,
                newOrderId,
                newOrderNumber,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public long getClaimId() {
        return claimId;
    }

    public Long getNewProductGroupId() {
        return newProductGroupId;
    }

    public Long getNewProductId() {
        return newProductId;
    }

    public String getNewOptionName() {
        return newOptionName;
    }

    public int getPriceDifference() {
        return priceDifference;
    }

    public String getNewOrderId() {
        return newOrderId;
    }

    public String getNewOrderNumber() {
        return newOrderNumber;
    }
}
