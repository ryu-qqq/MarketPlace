package com.ryuqq.marketplace.adapter.out.persistence.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;

/**
 * SellerCsJpaEntity - 셀러 CS 정보 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 */
@Entity
@Table(name = "seller_cs")
public class SellerCsJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "cs_phone", nullable = false, length = 20)
    private String csPhone;

    @Column(name = "cs_mobile", length = 20)
    private String csMobile;

    @Column(name = "cs_email", nullable = false, length = 100)
    private String csEmail;

    @Column(name = "operating_start_time")
    private LocalTime operatingStartTime;

    @Column(name = "operating_end_time")
    private LocalTime operatingEndTime;

    @Column(name = "operating_days", length = 50)
    private String operatingDays;

    @Column(name = "kakao_channel_url", length = 500)
    private String kakaoChannelUrl;

    protected SellerCsJpaEntity() {
        super();
    }

    private SellerCsJpaEntity(
            Long id,
            Long sellerId,
            String csPhone,
            String csMobile,
            String csEmail,
            LocalTime operatingStartTime,
            LocalTime operatingEndTime,
            String operatingDays,
            String kakaoChannelUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.csPhone = csPhone;
        this.csMobile = csMobile;
        this.csEmail = csEmail;
        this.operatingStartTime = operatingStartTime;
        this.operatingEndTime = operatingEndTime;
        this.operatingDays = operatingDays;
        this.kakaoChannelUrl = kakaoChannelUrl;
    }

    public static SellerCsJpaEntity create(
            Long id,
            Long sellerId,
            String csPhone,
            String csMobile,
            String csEmail,
            LocalTime operatingStartTime,
            LocalTime operatingEndTime,
            String operatingDays,
            String kakaoChannelUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerCsJpaEntity(
                id,
                sellerId,
                csPhone,
                csMobile,
                csEmail,
                operatingStartTime,
                operatingEndTime,
                operatingDays,
                kakaoChannelUrl,
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

    public String getCsPhone() {
        return csPhone;
    }

    public String getCsMobile() {
        return csMobile;
    }

    public String getCsEmail() {
        return csEmail;
    }

    public LocalTime getOperatingStartTime() {
        return operatingStartTime;
    }

    public LocalTime getOperatingEndTime() {
        return operatingEndTime;
    }

    public String getOperatingDays() {
        return operatingDays;
    }

    public String getKakaoChannelUrl() {
        return kakaoChannelUrl;
    }
}
