package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupImage Entity 단위 테스트")
class ProductGroupImageTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 ProductGroupImage를 생성한다")
        void createNewProductGroupImage() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.newProductGroupId();
            ImageUrl originUrl = ProductGroupFixtures.defaultImageUrl();
            ImageType imageType = ImageType.THUMBNAIL;
            int sortOrder = 0;

            // when
            ProductGroupImage image = ProductGroupImage.forNew(
                    productGroupId, originUrl, imageType, sortOrder);

            // then
            assertThat(image).isNotNull();
            assertThat(image.id().isNew()).isTrue();
            assertThat(image.productGroupId()).isEqualTo(productGroupId);
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.uploadedUrl()).isNull();
            assertThat(image.imageType()).isEqualTo(imageType);
            assertThat(image.sortOrder()).isEqualTo(sortOrder);
            assertThat(image.isUploaded()).isFalse();
        }

        @Test
        @DisplayName("THUMBNAIL 타입 이미지를 생성한다")
        void createThumbnailImage() {
            // given & when
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // then
            assertThat(image.imageType()).isEqualTo(ImageType.THUMBNAIL);
            assertThat(image.isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("DETAIL 타입 이미지를 생성한다")
        void createDetailImage() {
            // given & when
            ProductGroupImage image = ProductGroupFixtures.detailImage(0);

            // then
            assertThat(image.imageType()).isEqualTo(ImageType.DETAIL);
            assertThat(image.isThumbnail()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 ProductGroupImage를 복원한다")
        void reconstituteProductGroupImage() {
            // given
            ProductGroupImageId id = ProductGroupFixtures.defaultProductGroupImageId();
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ImageUrl originUrl = ProductGroupFixtures.defaultImageUrl();
            ImageUrl uploadedUrl = ProductGroupFixtures.imageUrl("https://s3.example.com/uploaded.jpg");
            ImageType imageType = ImageType.THUMBNAIL;
            int sortOrder = 0;

            // when
            ProductGroupImage image = ProductGroupImage.reconstitute(
                    id, productGroupId, originUrl, uploadedUrl, imageType, sortOrder);

            // then
            assertThat(image.id()).isEqualTo(id);
            assertThat(image.productGroupId()).isEqualTo(productGroupId);
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.uploadedUrl()).isEqualTo(uploadedUrl);
            assertThat(image.imageType()).isEqualTo(imageType);
            assertThat(image.sortOrder()).isEqualTo(sortOrder);
            assertThat(image.isUploaded()).isTrue();
        }
    }

    @Nested
    @DisplayName("업로드 URL 관리 테스트")
    class UploadUrlManagementTest {

        @Test
        @DisplayName("S3 업로드 URL을 설정한다")
        void updateUploadedUrl() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();
            ImageUrl uploadedUrl = ProductGroupFixtures.imageUrl("https://s3.example.com/uploaded.jpg");

            // when
            image.updateUploadedUrl(uploadedUrl);

            // then
            assertThat(image.uploadedUrl()).isEqualTo(uploadedUrl);
            assertThat(image.uploadedUrlValue()).isEqualTo("https://s3.example.com/uploaded.jpg");
            assertThat(image.isUploaded()).isTrue();
        }

        @Test
        @DisplayName("업로드 URL이 설정되어 있으면 isUploaded가 true다")
        void isUploadedWhenUploadedUrlExists() {
            // given
            ProductGroupImage image = ProductGroupFixtures.uploadedImage();

            // when & then
            assertThat(image.isUploaded()).isTrue();
        }

        @Test
        @DisplayName("업로드 URL이 없으면 isUploaded가 false다")
        void isNotUploadedWhenUploadedUrlIsNull() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // when & then
            assertThat(image.isUploaded()).isFalse();
        }
    }

    @Nested
    @DisplayName("정렬 순서 관리 테스트")
    class SortOrderManagementTest {

        @Test
        @DisplayName("정렬 순서를 변경한다")
        void updateSortOrder() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // when
            image.updateSortOrder(5);

            // then
            assertThat(image.sortOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("썸네일 판별 테스트")
    class ThumbnailCheckTest {

        @Test
        @DisplayName("THUMBNAIL 타입이면 isThumbnail이 true다")
        void isThumbnailWhenTypeThumbnail() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // when & then
            assertThat(image.isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("DETAIL 타입이면 isThumbnail이 false다")
        void isNotThumbnailWhenTypeDetail() {
            // given
            ProductGroupImage image = ProductGroupFixtures.detailImage(0);

            // when & then
            assertThat(image.isThumbnail()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ProductGroupImage image = ProductGroupImage.reconstitute(
                    ProductGroupImageId.of(100L),
                    ProductGroupFixtures.defaultProductGroupId(),
                    ProductGroupFixtures.defaultImageUrl(),
                    null,
                    ImageType.THUMBNAIL,
                    0);

            // when & then
            assertThat(image.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("productGroupIdValue()는 ProductGroupId의 값을 반환한다")
        void productGroupIdValueReturnsValue() {
            // given
            ProductGroupImage image = ProductGroupImage.reconstitute(
                    ProductGroupFixtures.defaultProductGroupImageId(),
                    ProductGroupId.of(200L),
                    ProductGroupFixtures.defaultImageUrl(),
                    null,
                    ImageType.THUMBNAIL,
                    0);

            // when & then
            assertThat(image.productGroupIdValue()).isEqualTo(200L);
        }

        @Test
        @DisplayName("originUrlValue()는 원본 URL 문자열을 반환한다")
        void originUrlValueReturnsValue() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // when & then
            assertThat(image.originUrlValue()).isEqualTo(ProductGroupFixtures.DEFAULT_IMAGE_URL);
        }

        @Test
        @DisplayName("uploadedUrlValue()는 업로드 URL이 null이면 null을 반환한다")
        void uploadedUrlValueReturnsNullWhenNull() {
            // given
            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();

            // when & then
            assertThat(image.uploadedUrlValue()).isNull();
        }
    }
}
