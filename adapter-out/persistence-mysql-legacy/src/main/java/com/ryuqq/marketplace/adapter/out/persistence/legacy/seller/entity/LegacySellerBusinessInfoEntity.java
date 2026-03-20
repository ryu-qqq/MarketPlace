package com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacySellerBusinessInfoEntity - 레거시 셀러 사업자 정보 엔티티.
 *
 * <p>레거시 DB의 seller_business_info 테이블 매핑. seller_id를 PK이자 FK로 사용 (1:1 관계).
 */
@Entity
@Table(name = "seller_business_info")
public class LegacySellerBusinessInfoEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "representative")
    private String representative;

    @Column(name = "sale_report_number")
    private String saleReportNumber;

    @Column(name = "business_address_zip_code")
    private String businessAddressZipCode;

    @Column(name = "business_address_line1")
    private String businessAddressLine1;

    @Column(name = "business_address_line2")
    private String businessAddressLine2;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "cs_number")
    private String csNumber;

    @Column(name = "cs_phone_number")
    private String csPhoneNumber;

    @Column(name = "cs_email")
    private String csEmail;

    protected LegacySellerBusinessInfoEntity() {}

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

    public String getBusinessAddressZipCode() {
        return businessAddressZipCode;
    }

    public String getBusinessAddressLine1() {
        return businessAddressLine1;
    }

    public String getBusinessAddressLine2() {
        return businessAddressLine2;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getCsNumber() {
        return csNumber;
    }

    public String getCsPhoneNumber() {
        return csPhoneNumber;
    }

    public String getCsEmail() {
        return csEmail;
    }
}
