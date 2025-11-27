package com.ryuqq.marketplace.adapter.out.persistence.brand.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.DataQualityLevel;
import com.ryuqq.marketplace.domain.brand.vo.Department;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

/**
 * BrandJpaEntity - Brand Aggregate Root의 JPA Entity
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java 클래스</li>
 *   <li>Long FK 전략 - JPA 관계 어노테이션 (@OneToMany, @ManyToOne) 금지</li>
 *   <li>Setter 금지 - Getter Only, 정적 팩토리 메서드 사용</li>
 *   <li>protected 기본 생성자 - JPA 스펙 요구사항</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Entity
@Table(name = "brand")
public class BrandJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "canonical_name", nullable = false, unique = true, length = 255)
    private String canonicalName;

    @Column(name = "name_ko", length = 255)
    private String nameKo;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(name = "country", length = 10)
    private String country;

    @Column(name = "department", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Department department;

    @Column(name = "is_luxury", nullable = false)
    private boolean isLuxury;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BrandStatus status;

    @Column(name = "official_website", length = 500)
    private String officialWebsite;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "data_quality_level", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DataQualityLevel dataQualityLevel;

    @Column(name = "data_quality_score", nullable = false)
    private int dataQualityScore;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected BrandJpaEntity() {
        super();
    }

    private BrandJpaEntity(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        String officialWebsite,
        String logoUrl,
        String description,
        DataQualityLevel dataQualityLevel,
        int dataQualityScore,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.canonicalName = canonicalName;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.shortName = shortName;
        this.country = country;
        this.department = department;
        this.isLuxury = isLuxury;
        this.status = status;
        this.officialWebsite = officialWebsite;
        this.logoUrl = logoUrl;
        this.description = description;
        this.dataQualityLevel = dataQualityLevel;
        this.dataQualityScore = dataQualityScore;
        this.version = version;
    }

    public static BrandJpaEntity from(Brand brand) {
        LocalDateTime now = LocalDateTime.now();
        return new BrandJpaEntity(
            brand.id() != null ? brand.id().value() : null,
            brand.code().value(),
            brand.canonicalName().value(),
            brand.nameKo(),
            brand.nameEn(),
            brand.shortName(),
            brand.country() != null ? brand.country().code() : null,
            brand.department(),
            brand.isLuxury(),
            brand.status(),
            brand.officialWebsite(),
            brand.logoUrl(),
            brand.description(),
            brand.dataQuality().level(),
            brand.dataQuality().score(),
            brand.version(),
            now,
            now
        );
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCanonicalName() {
        return canonicalName;
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

    public String getCountry() {
        return country;
    }

    public Department getDepartment() {
        return department;
    }

    public boolean isLuxury() {
        return isLuxury;
    }

    public BrandStatus getStatus() {
        return status;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public DataQualityLevel getDataQualityLevel() {
        return dataQualityLevel;
    }

    public int getDataQualityScore() {
        return dataQualityScore;
    }

    public Long getVersion() {
        return version;
    }
}
