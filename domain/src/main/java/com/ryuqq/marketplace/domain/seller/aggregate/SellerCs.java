package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import java.time.Instant;

/**
 * 셀러 CS 정보 Entity.
 *
 * <p>Seller와 1:1 관계. CS 연락처, 운영시간 등을 관리합니다.
 */
public class SellerCs {

    private final SellerCsId id;
    private SellerId sellerId;
    private CsContact csContact;
    private OperatingHours operatingHours;
    private String operatingDays;
    private String kakaoChannelUrl;
    private final Instant createdAt;
    private Instant updatedAt;

    private SellerCs(
            SellerCsId id,
            SellerId sellerId,
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.csContact = csContact;
        this.operatingHours = operatingHours;
        this.operatingDays = operatingDays;
        this.kakaoChannelUrl = kakaoChannelUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** SellerId 없이 새 SellerCs 생성. */
    public static SellerCs forNew(
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl,
            Instant now) {
        return new SellerCs(
                SellerCsId.forNew(),
                null,
                csContact,
                operatingHours,
                operatingDays,
                kakaoChannelUrl,
                now,
                now);
    }

    /** SellerId와 함께 새 SellerCs 생성. */
    public static SellerCs forNew(
            SellerId sellerId,
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl,
            Instant now) {
        return new SellerCs(
                SellerCsId.forNew(),
                sellerId,
                csContact,
                operatingHours,
                operatingDays,
                kakaoChannelUrl,
                now,
                now);
    }

    /** 기본 CS 정보 생성 (연락처만 지정). */
    public static SellerCs defaultCs(SellerId sellerId, CsContact csContact, Instant now) {
        return new SellerCs(
                SellerCsId.forNew(),
                sellerId,
                csContact,
                OperatingHours.businessHours(),
                "MON,TUE,WED,THU,FRI",
                null,
                now,
                now);
    }

    /** DB에서 재구성. */
    public static SellerCs reconstitute(
            SellerCsId id,
            SellerId sellerId,
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl,
            Instant createdAt,
            Instant updatedAt) {
        return new SellerCs(
                id,
                sellerId,
                csContact,
                operatingHours,
                operatingDays,
                kakaoChannelUrl,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /** SellerId 설정. */
    public void assignSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    /** CS 연락처 변경. */
    public void updateContact(CsContact csContact, Instant now) {
        this.csContact = csContact;
        this.updatedAt = now;
    }

    /** 운영 시간 변경. */
    public void updateOperatingHours(
            OperatingHours operatingHours, String operatingDays, Instant now) {
        this.operatingHours = operatingHours;
        this.operatingDays = operatingDays;
        this.updatedAt = now;
    }

    /** 카카오 채널 URL 변경. */
    public void updateKakaoChannelUrl(String kakaoChannelUrl, Instant now) {
        this.kakaoChannelUrl = kakaoChannelUrl;
        this.updatedAt = now;
    }

    /** 전체 CS 정보 업데이트. */
    public void update(
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl,
            Instant now) {
        this.csContact = csContact;
        this.operatingHours = operatingHours;
        this.operatingDays = operatingDays;
        this.kakaoChannelUrl = kakaoChannelUrl;
        this.updatedAt = now;
    }

    // Getters
    public SellerCsId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId != null ? sellerId.value() : null;
    }

    public CsContact csContact() {
        return csContact;
    }

    public String csPhone() {
        return csContact.phoneValue();
    }

    public String csMobile() {
        return csContact.mobileValue();
    }

    public String csEmail() {
        return csContact.emailValue();
    }

    public OperatingHours operatingHours() {
        return operatingHours;
    }

    public String operatingDays() {
        return operatingDays;
    }

    public String kakaoChannelUrl() {
        return kakaoChannelUrl;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
