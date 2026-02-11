package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.DescriptionImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DescriptionImage Entity 단위 테스트")
class DescriptionImageTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 DescriptionImage를 생성한다")
        void createNewDescriptionImage() {
            // given
            ImageUrl originUrl = ProductGroupFixtures.defaultImageUrl();
            int sortOrder = 0;

            // when
            DescriptionImage image = DescriptionImage.forNew(originUrl, sortOrder);

            // then
            assertThat(image).isNotNull();
            assertThat(image.id().isNew()).isTrue();
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.uploadedUrl()).isNull();
            assertThat(image.sortOrder()).isEqualTo(sortOrder);
            assertThat(image.isUploaded()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 DescriptionImage를 복원한다")
        void reconstituteDescriptionImage() {
            // given
            DescriptionImageId id = ProductGroupFixtures.defaultDescriptionImageId();
            ImageUrl originUrl = ProductGroupFixtures.defaultImageUrl();
            ImageUrl uploadedUrl =
                    ProductGroupFixtures.imageUrl("https://s3.example.com/uploaded.jpg");
            int sortOrder = 0;

            // when
            DescriptionImage image =
                    DescriptionImage.reconstitute(id, originUrl, uploadedUrl, sortOrder);

            // then
            assertThat(image.id()).isEqualTo(id);
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.uploadedUrl()).isEqualTo(uploadedUrl);
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
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();
            ImageUrl uploadedUrl =
                    ProductGroupFixtures.imageUrl("https://s3.example.com/uploaded.jpg");

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
            DescriptionImage image = ProductGroupFixtures.uploadedDescriptionImage();

            // when & then
            assertThat(image.isUploaded()).isTrue();
        }

        @Test
        @DisplayName("업로드 URL이 없으면 isUploaded가 false다")
        void isNotUploadedWhenUploadedUrlIsNull() {
            // given
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();

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
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();

            // when
            image.updateSortOrder(5);

            // then
            assertThat(image.sortOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            DescriptionImage image =
                    DescriptionImage.reconstitute(
                            DescriptionImageId.of(100L),
                            ProductGroupFixtures.defaultImageUrl(),
                            null,
                            0);

            // when & then
            assertThat(image.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("originUrlValue()는 원본 URL 문자열을 반환한다")
        void originUrlValueReturnsValue() {
            // given
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();

            // when & then
            assertThat(image.originUrlValue()).isEqualTo(ProductGroupFixtures.DEFAULT_IMAGE_URL);
        }

        @Test
        @DisplayName("uploadedUrlValue()는 업로드 URL이 null이면 null을 반환한다")
        void uploadedUrlValueReturnsNullWhenNull() {
            // given
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();

            // when & then
            assertThat(image.uploadedUrlValue()).isNull();
        }
    }
}
