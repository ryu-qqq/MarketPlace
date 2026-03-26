package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImage;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverImageMapper 단위 테스트")
class NaverImageMapperTest {

    // ── 헬퍼 메서드 ──

    private ProductGroupImageResult imageResult(
            String originUrl, String uploadedUrl, String type, int sort) {
        return new ProductGroupImageResult(1L, originUrl, uploadedUrl, type, sort, List.of());
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("mapImages (ProductGroupImageResult)")
    class MapImagesTest {

        @Test
        @DisplayName("THUMBNAIL 타입이 representativeImage로 매핑된다")
        void thumbnailIsRepresentative() {
            var images = List.of(imageResult("origin.jpg", "uploaded.jpg", "THUMBNAIL", 1));

            Images result = NaverImageMapper.mapImages(images);

            assertThat(result.representativeImage()).isNotNull();
            assertThat(result.representativeImage().url()).isEqualTo("uploaded.jpg");
        }

        @Test
        @DisplayName("uploadedUrl이 null이면 originUrl 사용")
        void fallbackToOriginUrl() {
            var images = List.of(imageResult("origin.jpg", null, "THUMBNAIL", 1));

            Images result = NaverImageMapper.mapImages(images);

            assertThat(result.representativeImage().url()).isEqualTo("origin.jpg");
        }

        @Test
        @DisplayName("THUMBNAIL이 없으면 첫 번째 이미지가 대표 이미지")
        void firstImageAsRepresentativeWhenNoThumbnail() {
            var images =
                    List.of(
                            imageResult("detail1.jpg", null, "DETAIL", 1),
                            imageResult("detail2.jpg", null, "DETAIL", 2));

            Images result = NaverImageMapper.mapImages(images);

            assertThat(result.representativeImage()).isNotNull();
            assertThat(result.representativeImage().url()).isEqualTo("detail1.jpg");
        }

        @Test
        @DisplayName("DETAIL 타입은 optionalImages로 매핑된다")
        void detailIsOptional() {
            var images =
                    List.of(
                            imageResult("thumb.jpg", null, "THUMBNAIL", 1),
                            imageResult("detail1.jpg", null, "DETAIL", 2),
                            imageResult("detail2.jpg", null, "DETAIL", 3));

            Images result = NaverImageMapper.mapImages(images);

            assertThat(result.optionalImages()).hasSize(2);
        }

        @Test
        @DisplayName("옵셔널 이미지가 없으면 null")
        void noOptionalImagesReturnsNull() {
            var images = List.of(imageResult("thumb.jpg", null, "THUMBNAIL", 1));

            Images result = NaverImageMapper.mapImages(images);

            assertThat(result.optionalImages()).isNull();
        }
    }

    @Nested
    @DisplayName("mapExternalImages (ResolvedExternalImages)")
    class MapExternalImagesTest {

        @Test
        @DisplayName("썸네일 URL이 representativeImage로 매핑된다")
        void thumbnailUrlIsRepresentative() {
            var resolved =
                    ResolvedExternalImages.of(
                            List.of(
                                    new ResolvedExternalImage(
                                            "naver-thumb.jpg", ImageType.THUMBNAIL, 1)));

            Images result = NaverImageMapper.mapExternalImages(resolved);

            assertThat(result.representativeImage()).isNotNull();
            assertThat(result.representativeImage().url()).isEqualTo("naver-thumb.jpg");
        }

        @Test
        @DisplayName("상세 이미지 URL이 optionalImages로 매핑된다")
        void detailUrlsAreOptional() {
            var resolved =
                    ResolvedExternalImages.of(
                            List.of(
                                    new ResolvedExternalImage(
                                            "naver-thumb.jpg", ImageType.THUMBNAIL, 1),
                                    new ResolvedExternalImage(
                                            "naver-detail1.jpg", ImageType.DETAIL, 2),
                                    new ResolvedExternalImage(
                                            "naver-detail2.jpg", ImageType.DETAIL, 3)));

            Images result = NaverImageMapper.mapExternalImages(resolved);

            assertThat(result.optionalImages()).hasSize(2);
        }

        @Test
        @DisplayName("썸네일이 없으면 representativeImage는 null")
        void noThumbnailReturnsNullRepresentative() {
            var resolved =
                    ResolvedExternalImages.of(
                            List.of(
                                    new ResolvedExternalImage(
                                            "naver-detail.jpg", ImageType.DETAIL, 1)));

            Images result = NaverImageMapper.mapExternalImages(resolved);

            assertThat(result.representativeImage()).isNull();
        }
    }
}
