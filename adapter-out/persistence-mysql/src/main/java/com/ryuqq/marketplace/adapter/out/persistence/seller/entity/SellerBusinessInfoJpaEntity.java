package com.ryuqq.marketplace.adapter.out.persistence.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * SellerBusinessInfoJpaEntity - 셀러 사업자 정보 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "seller_business_infos")
public class SellerBusinessInfoJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "registration_number", nullable = false, length = 20, unique = true)
    private String registrationNumber;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "representative", nullable = false, length = 50)
    private String representative;

    @Column(name = "sale_report_number", length = 50)
    private String saleReportNumber;

    @Column(name = "business_zipcode", length = 10)
    private String businessZipcode;

    @Column(name = "business_address", length = 200)
    private String businessAddress;

    @Column(name = "business_address_detail", length = 200)
    private String businessAddressDetail;

    protected SellerBusinessInfoJpaEntity() {
        super();
    }

    private SellerBusinessInfoJpaEntity(
            Long id,
            Long sellerId,
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            String businessZipcode,
            String businessAddress,
            String businessAddressDetail,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.registrationNumber = registrationNumber;
        this.companyName = companyName;
        this.representative = representative;
        this.saleReportNumber = saleReportNumber;
        this.businessZipcode = businessZipcode;
        this.businessAddress = businessAddress;
        this.businessAddressDetail = businessAddressDetail;
    }

    public static SellerBusinessInfoJpaEntity create(
            Long id,
            Long sellerId,
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            String businessZipcode,
            String businessAddress,
            String businessAddressDetail,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerBusinessInfoJpaEntity(
                id,
                sellerId,
                registrationNumber,
                companyName,
                representative,
                saleReportNumber,
                businessZipcode,
                businessAddress,
                businessAddressDetail,
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRepresentative() {
        return representative;
    }

    public String getSaleReportNumber() {
        return saleReportNumber;
    }

    public String getBusinessZipcode() {
        return businessZipcode;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public String getBusinessAddressDetail() {
        return businessAddressDetail;
    }
}
