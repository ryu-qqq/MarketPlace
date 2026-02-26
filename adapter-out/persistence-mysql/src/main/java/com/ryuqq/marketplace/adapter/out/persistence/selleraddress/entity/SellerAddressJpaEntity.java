package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * SellerAddressJpaEntity - 셀러 주소 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "seller_addresses")
public class SellerAddressJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "address_type", nullable = false, length = 20)
    private String addressType;

    @Column(name = "address_name", nullable = false, length = 50)
    private String addressName;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "address_detail", length = 200)
    private String addressDetail;

    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress;

    protected SellerAddressJpaEntity() {
        super();
    }

    private SellerAddressJpaEntity(
            Long id,
            Long sellerId,
            String addressType,
            String addressName,
            String zipcode,
            String address,
            String addressDetail,
            boolean defaultAddress,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.addressType = addressType;
        this.addressName = addressName;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.defaultAddress = defaultAddress;
    }

    public static SellerAddressJpaEntity create(
            Long id,
            Long sellerId,
            String addressType,
            String addressName,
            String zipcode,
            String address,
            String addressDetail,
            boolean defaultAddress,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerAddressJpaEntity(
                id,
                sellerId,
                addressType,
                addressName,
                zipcode,
                address,
                addressDetail,
                defaultAddress,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }
}
