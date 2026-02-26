package com.ryuqq.marketplace.domain.imagevariant.aggregate;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.id.ImageVariantId;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;

/**
 * 이미지 Variant Aggregate Root.
 *
 * <p>원본 이미지에서 변환된 멀티 사이즈 WEBP 이미지를 나타냅니다.
 *
 * <p>이미지 변환이 완료(COMPLETED)되면 생성되며, CDN URL과 결과 에셋 ID를 보관합니다.
 */
public class ImageVariant {

    private final ImageVariantId id;
    private final Long sourceImageId;
    private final ImageSourceType sourceType;
    private final ImageVariantType variantType;
    private final ResultAssetId resultAssetId;
    private final ImageUrl variantUrl;
    private final ImageDimension dimension;
    private final Instant createdAt;

    private ImageVariant(
            ImageVariantId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantType variantType,
            ResultAssetId resultAssetId,
            ImageUrl variantUrl,
            ImageDimension dimension,
            Instant createdAt) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.variantType = variantType;
        this.resultAssetId = resultAssetId;
        this.variantUrl = variantUrl;
        this.dimension = dimension;
        this.createdAt = createdAt;
    }

    /**
     * 새 ImageVariant 생성.
     *
     * @param sourceImageId 원본 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param variantType Variant 타입
     * @param resultAssetId FileFlow 변환 결과 에셋 ID
     * @param variantUrl 변환된 이미지 CDN URL
     * @param dimension 이미지 크기 (너비/높이)
     * @param now 생성 시각
     * @return 새 ImageVariant 인스턴스
     */
    public static ImageVariant forNew(
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantType variantType,
            ResultAssetId resultAssetId,
            ImageUrl variantUrl,
            ImageDimension dimension,
            Instant now) {
        return new ImageVariant(
                ImageVariantId.forNew(),
                sourceImageId,
                sourceType,
                variantType,
                resultAssetId,
                variantUrl,
                dimension,
                now);
    }

    /**
     * DB에서 재구성.
     *
     * @param id Variant ID
     * @param sourceImageId 원본 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param variantType Variant 타입
     * @param resultAssetId 결과 에셋 ID
     * @param variantUrl 변환된 이미지 CDN URL
     * @param dimension 이미지 크기 (너비/높이)
     * @param createdAt 생성일시
     * @return 재구성된 ImageVariant 인스턴스
     */
    public static ImageVariant reconstitute(
            ImageVariantId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantType variantType,
            ResultAssetId resultAssetId,
            ImageUrl variantUrl,
            ImageDimension dimension,
            Instant createdAt) {
        return new ImageVariant(
                id,
                sourceImageId,
                sourceType,
                variantType,
                resultAssetId,
                variantUrl,
                dimension,
                createdAt);
    }

    // Getters

    public ImageVariantId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long sourceImageId() {
        return sourceImageId;
    }

    public ImageSourceType sourceType() {
        return sourceType;
    }

    public ImageVariantType variantType() {
        return variantType;
    }

    public ResultAssetId resultAssetId() {
        return resultAssetId;
    }

    public String resultAssetIdValue() {
        return resultAssetId.value();
    }

    public ImageUrl variantUrl() {
        return variantUrl;
    }

    public String variantUrlValue() {
        return variantUrl.value();
    }

    public ImageDimension dimension() {
        return dimension;
    }

    public Integer width() {
        return dimension.width();
    }

    public Integer height() {
        return dimension.height();
    }

    public Instant createdAt() {
        return createdAt;
    }
}
