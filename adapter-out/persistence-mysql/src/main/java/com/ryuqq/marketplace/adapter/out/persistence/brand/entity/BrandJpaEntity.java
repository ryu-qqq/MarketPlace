package com.ryuqq.marketplace.adapter.out.persistence.brand.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Brand JPA 엔티티. */
@Entity
@Table(name = "brand")
public class BrandJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 100, unique = true)
    private String code;

    @Column(name = "name_ko", length = 255)
    private String nameKo;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    protected BrandJpaEntity() {
        super();
    }

    private BrandJpaEntity(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            String shortName,
            String status,
            String logoUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.shortName = shortName;
        this.status = status;
        this.logoUrl = logoUrl;
    }

    public static BrandJpaEntity create(
            Long id,
            String code,
            String nameKo,
            String nameEn,
            String shortName,
            String status,
            String logoUrl,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new BrandJpaEntity(
                id, code, nameKo, nameEn, shortName, status, logoUrl, createdAt, updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameKo() {
        return nameKo;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getShortName() {
        return shortName;
    }

    public String getStatus() {
        return status;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
