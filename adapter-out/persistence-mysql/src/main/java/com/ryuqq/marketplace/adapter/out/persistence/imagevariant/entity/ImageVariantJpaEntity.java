package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * ImageVariantJpaEntity - 이미지 Variant JPA 엔티티.
 *
 * <p>원본 이미지에서 변환된 멀티 사이즈 WEBP 이미지를 저장합니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "image_variants")
public class ImageVariantJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_image_id", nullable = false)
    private Long sourceImageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private ImageSourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "variant_type", nullable = false, length = 30)
    private ImageVariantType variantType;

    @Column(name = "result_asset_id", length = 100)
    private String resultAssetId;

    @Column(name = "variant_url", nullable = false, length = 500)
    private String variantUrl;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ImageVariantJpaEntity() {}

    private ImageVariantJpaEntity(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantType variantType,
            String resultAssetId,
            String variantUrl,
            Integer width,
            Integer height,
            Instant createdAt) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.variantType = variantType;
        this.resultAssetId = resultAssetId;
        this.variantUrl = variantUrl;
        this.width = width;
        this.height = height;
        this.createdAt = createdAt;
    }

    public static ImageVariantJpaEntity create(
            Long id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantType variantType,
            String resultAssetId,
            String variantUrl,
            Integer width,
            Integer height,
            Instant createdAt) {
        return new ImageVariantJpaEntity(
                id,
                sourceImageId,
                sourceType,
                variantType,
                resultAssetId,
                variantUrl,
                width,
                height,
                createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSourceImageId() {
        return sourceImageId;
    }

    public ImageSourceType getSourceType() {
        return sourceType;
    }

    public ImageVariantType getVariantType() {
        return variantType;
    }

    public String getResultAssetId() {
        return resultAssetId;
    }

    public String getVariantUrl() {
        return variantUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
