package com.ryuqq.marketplace.application.outboundproductimage.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.outboundproductimage.ResolvedExternalImageFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ResolvedExternalImages 단위 테스트")
class ResolvedExternalImagesTest {

    @Nested
    @DisplayName("empty() - 빈 인스턴스 생성")
    class EmptyTest {

        @Test
        @DisplayName("empty()는 이미지가 없는 인스턴스를 반환한다")
        void empty_ReturnsEmptyInstance() {
            ResolvedExternalImages sut = ResolvedExternalImages.empty();

            assertThat(sut.isEmpty()).isTrue();
            assertThat(sut.images()).isEmpty();
        }

        @Test
        @DisplayName("empty()의 thumbnailUrl은 null을 반환한다")
        void empty_ThumbnailUrl_ReturnsNull() {
            ResolvedExternalImages sut = ResolvedExternalImages.empty();

            assertThat(sut.thumbnailUrl()).isNull();
        }

        @Test
        @DisplayName("empty()의 detailUrls는 빈 목록을 반환한다")
        void empty_DetailUrls_ReturnsEmptyList() {
            ResolvedExternalImages sut = ResolvedExternalImages.empty();

            assertThat(sut.detailUrls()).isEmpty();
        }
    }

    @Nested
    @DisplayName("thumbnailUrl() - 썸네일 URL 반환")
    class ThumbnailUrlTest {

        @Test
        @DisplayName("썸네일 이미지가 있으면 external URL을 반환한다")
        void thumbnailUrl_WithThumbnail_ReturnsExternalUrl() {
            ResolvedExternalImages sut =
                    ResolvedExternalImageFixtures.thumbnailOnlyResolvedImages();

            assertThat(sut.thumbnailUrl())
                    .isEqualTo(ResolvedExternalImageFixtures.DEFAULT_THUMBNAIL_EXTERNAL_URL);
        }

        @Test
        @DisplayName("썸네일 이미지가 없으면 null을 반환한다")
        void thumbnailUrl_WithoutThumbnail_ReturnsNull() {
            ResolvedExternalImages sut = ResolvedExternalImageFixtures.detailOnlyResolvedImages();

            assertThat(sut.thumbnailUrl()).isNull();
        }

        @Test
        @DisplayName("썸네일과 상세 이미지가 혼재할 때 썸네일 URL만 반환한다")
        void thumbnailUrl_WithMixedImages_ReturnsThumbnailOnly() {
            ResolvedExternalImages sut = ResolvedExternalImageFixtures.fullResolvedImages();

            assertThat(sut.thumbnailUrl())
                    .isEqualTo(ResolvedExternalImageFixtures.DEFAULT_THUMBNAIL_EXTERNAL_URL);
        }
    }

    @Nested
    @DisplayName("detailUrls() - 상세 이미지 URL 목록 반환")
    class DetailUrlsTest {

        @Test
        @DisplayName("상세 이미지가 있으면 sortOrder 오름차순으로 정렬된 URL 목록을 반환한다")
        void detailUrls_WithDetails_ReturnsSortedUrls() {
            ResolvedExternalImages sut = ResolvedExternalImageFixtures.detailOnlyResolvedImages();

            List<String> urls = sut.detailUrls();

            assertThat(urls).hasSize(2);
            assertThat(urls.get(0)).isEqualTo("https://shop-phinf.pstatic.net/detail1.jpg");
            assertThat(urls.get(1)).isEqualTo("https://shop-phinf.pstatic.net/detail2.jpg");
        }

        @Test
        @DisplayName("썸네일만 있으면 detailUrls는 빈 목록을 반환한다")
        void detailUrls_WithThumbnailOnly_ReturnsEmptyList() {
            ResolvedExternalImages sut =
                    ResolvedExternalImageFixtures.thumbnailOnlyResolvedImages();

            assertThat(sut.detailUrls()).isEmpty();
        }

        @Test
        @DisplayName("sortOrder가 역순으로 입력되어도 오름차순 정렬 결과를 반환한다")
        void detailUrls_ReverseOrderInput_ReturnsSortedAscending() {
            ResolvedExternalImage detail2 =
                    new ResolvedExternalImage(
                            "https://shop-phinf.pstatic.net/detail2.jpg", ImageType.DETAIL, 2);
            ResolvedExternalImage detail1 =
                    new ResolvedExternalImage(
                            "https://shop-phinf.pstatic.net/detail1.jpg", ImageType.DETAIL, 1);
            ResolvedExternalImages sut = ResolvedExternalImages.of(List.of(detail2, detail1));

            List<String> urls = sut.detailUrls();

            assertThat(urls)
                    .containsExactly(
                            "https://shop-phinf.pstatic.net/detail1.jpg",
                            "https://shop-phinf.pstatic.net/detail2.jpg");
        }
    }

    @Nested
    @DisplayName("isEmpty() - 비어있는지 확인")
    class IsEmptyTest {

        @Test
        @DisplayName("이미지가 없으면 isEmpty는 true를 반환한다")
        void isEmpty_WithNoImages_ReturnsTrue() {
            ResolvedExternalImages sut = ResolvedExternalImages.empty();

            assertThat(sut.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("이미지가 있으면 isEmpty는 false를 반환한다")
        void isEmpty_WithImages_ReturnsFalse() {
            ResolvedExternalImages sut =
                    ResolvedExternalImageFixtures.thumbnailOnlyResolvedImages();

            assertThat(sut.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("of() - 생성자 불변성 확인")
    class OfTest {

        @Test
        @DisplayName("of()로 생성 시 내부 목록은 불변 복사본이다")
        void of_CreatesImmutableCopy() {
            ResolvedExternalImage thumbnail = ResolvedExternalImageFixtures.thumbnailImage();
            List<ResolvedExternalImage> mutableList = new java.util.ArrayList<>();
            mutableList.add(thumbnail);

            ResolvedExternalImages sut = ResolvedExternalImages.of(mutableList);
            mutableList.clear();

            assertThat(sut.images()).hasSize(1);
        }
    }
}
