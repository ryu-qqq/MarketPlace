package com.ryuqq.marketplace.application.outboundproductimage;

import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImage;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.util.List;

/**
 * OutboundProductImage Application 테스트 Fixtures.
 *
 * <p>OutboundProductImage 관련 Application DTO 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ResolvedExternalImageFixtures {

    private ResolvedExternalImageFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_THUMBNAIL_EXTERNAL_URL =
            "https://shop-phinf.pstatic.net/thumbnail.jpg";
    public static final String DEFAULT_DETAIL_EXTERNAL_URL_1 =
            "https://shop-phinf.pstatic.net/detail1.jpg";
    public static final String DEFAULT_DETAIL_EXTERNAL_URL_2 =
            "https://shop-phinf.pstatic.net/detail2.jpg";
    public static final String DEFAULT_CHANNEL_CODE = "NAVER";
    public static final String UNSUPPORTED_CHANNEL_CODE = "UNKNOWN_CHANNEL";

    // ===== ResolvedExternalImage Fixtures =====

    /** 썸네일 ResolvedExternalImage. */
    public static ResolvedExternalImage thumbnailImage() {
        return new ResolvedExternalImage(DEFAULT_THUMBNAIL_EXTERNAL_URL, ImageType.THUMBNAIL, 0);
    }

    /** 상세 ResolvedExternalImage. */
    public static ResolvedExternalImage detailImage(int sortOrder) {
        return new ResolvedExternalImage(
                "https://shop-phinf.pstatic.net/detail" + sortOrder + ".jpg",
                ImageType.DETAIL,
                sortOrder);
    }

    // ===== ResolvedExternalImages Fixtures =====

    /** 빈 ResolvedExternalImages. */
    public static ResolvedExternalImages emptyResolvedImages() {
        return ResolvedExternalImages.empty();
    }

    /** 썸네일 1개만 있는 ResolvedExternalImages. */
    public static ResolvedExternalImages thumbnailOnlyResolvedImages() {
        return ResolvedExternalImages.of(List.of(thumbnailImage()));
    }

    /** 썸네일 + 상세 이미지 2개를 포함한 ResolvedExternalImages. */
    public static ResolvedExternalImages fullResolvedImages() {
        return ResolvedExternalImages.of(List.of(thumbnailImage(), detailImage(1), detailImage(2)));
    }

    /** 상세 이미지만 있는 ResolvedExternalImages (sortOrder 역순 입력). */
    public static ResolvedExternalImages detailOnlyResolvedImages() {
        return ResolvedExternalImages.of(List.of(detailImage(2), detailImage(1)));
    }
}
