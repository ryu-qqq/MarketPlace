package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNoThumbnailException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupImages VO 단위 테스트")
class ProductGroupImagesTest {

    @Nested
    @DisplayName("of 팩토리 메서드 테스트")
    class OfTest {

        @Test
        @DisplayName("THUMBNAIL 1개와 DETAIL 이미지들로 생성한다")
        void createWithThumbnailAndDetailImages() {
            // given
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1),
                            ProductGroupFixtures.detailImage(2));

            // when
            ProductGroupImages productGroupImages = ProductGroupImages.of(images);

            // then
            assertThat(productGroupImages.size()).isEqualTo(3);
            assertThat(productGroupImages.thumbnail().isThumbnail()).isTrue();
            assertThat(productGroupImages.detailImages()).hasSize(2);
        }

        @Test
        @DisplayName("THUMBNAIL이 sortOrder 0으로 정렬된다")
        void thumbnailIsSortedFirst() {
            // given - DETAIL을 먼저, THUMBNAIL을 뒤에 배치
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.detailImage(0),
                            ProductGroupFixtures.thumbnailImage());

            // when
            ProductGroupImages productGroupImages = ProductGroupImages.of(images);

            // then
            assertThat(productGroupImages.thumbnail().isThumbnail()).isTrue();
            assertThat(productGroupImages.thumbnail().sortOrder()).isEqualTo(0);
            assertThat(productGroupImages.detailImages().get(0).sortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("THUMBNAIL이 없으면 예외가 발생한다")
        void throwExceptionWhenNoThumbnail() {
            // given
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.detailImage(0),
                            ProductGroupFixtures.detailImage(1));

            // when & then
            assertThatThrownBy(() -> ProductGroupImages.of(images))
                    .isInstanceOf(ProductGroupNoThumbnailException.class);
        }

        @Test
        @DisplayName("THUMBNAIL이 2개 이상이면 예외가 발생한다")
        void throwExceptionWhenMultipleThumbnails() {
            // given
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.thumbnailImage());

            // when & then
            assertThatThrownBy(() -> ProductGroupImages.of(images))
                    .isInstanceOf(ProductGroupNoThumbnailException.class);
        }

        @Test
        @DisplayName("THUMBNAIL 1개만으로도 생성할 수 있다")
        void createWithOnlyThumbnail() {
            // given
            List<ProductGroupImage> images = List.of(ProductGroupFixtures.thumbnailImage());

            // when
            ProductGroupImages productGroupImages = ProductGroupImages.of(images);

            // then
            assertThat(productGroupImages.size()).isEqualTo(1);
            assertThat(productGroupImages.thumbnail().isThumbnail()).isTrue();
            assertThat(productGroupImages.detailImages()).isEmpty();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("검증 없이 영속성에서 복원한다")
        void reconstituteSkipsValidation() {
            // given - THUMBNAIL 없이 DETAIL만 있는 경우 (레거시 데이터)
            List<ProductGroupImage> images = List.of(ProductGroupFixtures.detailImage(0));

            // when - 예외 없이 생성됨
            ProductGroupImages productGroupImages = ProductGroupImages.reconstitute(images);

            // then
            assertThat(productGroupImages.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("조회 메서드 테스트")
    class QueryTest {

        @Test
        @DisplayName("toList()는 불변 리스트를 반환한다")
        void toListReturnsUnmodifiableList() {
            // given
            ProductGroupImages images = ProductGroupFixtures.defaultProductGroupImages();

            // when & then
            assertThatThrownBy(() -> images.toList().add(ProductGroupFixtures.detailImage(1)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("isEmpty()는 이미지가 없으면 true를 반환한다")
        void isEmptyReturnsTrueWhenNoImages() {
            // given
            ProductGroupImages images = ProductGroupImages.reconstitute(List.of());

            // when & then
            assertThat(images.isEmpty()).isTrue();
        }
    }
}
