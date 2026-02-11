package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupDescription Entity 단위 테스트")
class ProductGroupDescriptionTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 ProductGroupDescription을 생성한다")
        void createNewProductGroupDescription() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.newProductGroupId();
            DescriptionHtml content = ProductGroupFixtures.defaultDescriptionHtml();

            // when
            ProductGroupDescription description =
                    ProductGroupDescription.forNew(productGroupId, content);

            // then
            assertThat(description).isNotNull();
            assertThat(description.id().isNew()).isTrue();
            assertThat(description.productGroupId()).isEqualTo(productGroupId);
            assertThat(description.content()).isEqualTo(content);
            assertThat(description.cdnPath()).isNull();
            assertThat(description.images()).isEmpty();
            assertThat(description.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 ProductGroupDescription을 복원한다")
        void reconstituteProductGroupDescription() {
            // given
            ProductGroupDescriptionId id = ProductGroupFixtures.defaultProductGroupDescriptionId();
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            DescriptionHtml content = ProductGroupFixtures.defaultDescriptionHtml();
            CdnPath cdnPath = ProductGroupFixtures.defaultCdnPath();
            List<DescriptionImage> images = List.of(ProductGroupFixtures.defaultDescriptionImage());

            // when
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            id, productGroupId, content, cdnPath, images);

            // then
            assertThat(description.id()).isEqualTo(id);
            assertThat(description.productGroupId()).isEqualTo(productGroupId);
            assertThat(description.content()).isEqualTo(content);
            assertThat(description.cdnPath()).isEqualTo(cdnPath);
            assertThat(description.images()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("내용 수정 메서드 테스트")
    class UpdateContentTest {

        @Test
        @DisplayName("상세설명 내용을 수정한다")
        void updateContent() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionHtml newContent = ProductGroupFixtures.descriptionHtml("<p>수정된 설명</p>");

            // when
            description.updateContent(newContent);

            // then
            assertThat(description.content()).isEqualTo(newContent);
            assertThat(description.contentValue()).isEqualTo("<p>수정된 설명</p>");
        }

        @Test
        @DisplayName("CDN 경로를 설정한다")
        void updateCdnPath() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            CdnPath cdnPath = ProductGroupFixtures.defaultCdnPath();

            // when
            description.updateCdnPath(cdnPath);

            // then
            assertThat(description.cdnPath()).isEqualTo(cdnPath);
            assertThat(description.cdnPathValue()).isEqualTo(ProductGroupFixtures.DEFAULT_CDN_PATH);
        }
    }

    @Nested
    @DisplayName("이미지 관리 메서드 테스트")
    class ImageManagementTest {

        @Test
        @DisplayName("상세설명 이미지를 추가한다")
        void addImage() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();

            // when
            description.addImage(image);

            // then
            assertThat(description.images()).hasSize(1);
            assertThat(description.imageCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("상세설명 이미지 전체를 교체한다")
        void replaceImages() {
            // given
            ProductGroupDescription description = ProductGroupFixtures.descriptionWithImages();
            List<DescriptionImage> newImages =
                    List.of(
                            ProductGroupFixtures.defaultDescriptionImage(),
                            ProductGroupFixtures.uploadedDescriptionImage());

            // when
            description.replaceImages(newImages);

            // then
            assertThat(description.images()).hasSize(2);
        }

        @Test
        @DisplayName("모든 이미지가 업로드되었는지 확인한다")
        void isAllImagesUploaded() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionImage uploadedImage = ProductGroupFixtures.uploadedDescriptionImage();
            description.addImage(uploadedImage);

            // when & then
            assertThat(description.isAllImagesUploaded()).isTrue();
        }

        @Test
        @DisplayName("업로드되지 않은 이미지가 있으면 false를 반환한다")
        void isNotAllImagesUploaded() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            description.addImage(ProductGroupFixtures.defaultDescriptionImage());

            // when & then
            assertThat(description.isAllImagesUploaded()).isFalse();
        }

        @Test
        @DisplayName("이미지가 없으면 모두 업로드된 것으로 간주한다")
        void emptyImagesAreAllUploaded() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();

            // when & then
            assertThat(description.isAllImagesUploaded()).isTrue();
        }
    }

    @Nested
    @DisplayName("isEmpty 메서드 테스트")
    class IsEmptyTest {

        @Test
        @DisplayName("content가 null이면 empty로 판단한다")
        void isEmptyWhenContentIsNull() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.defaultProductGroupId(),
                            null,
                            null,
                            List.of());

            // when & then
            assertThat(description.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("content가 empty면 empty로 판단한다")
        void isEmptyWhenContentIsEmpty() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.defaultProductGroupId(),
                            ProductGroupFixtures.emptyDescriptionHtml(),
                            null,
                            List.of());

            // when & then
            assertThat(description.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("content가 있으면 empty가 아니다")
        void isNotEmptyWhenContentExists() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();

            // when & then
            assertThat(description.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupDescriptionId.of(100L),
                            ProductGroupFixtures.defaultProductGroupId(),
                            ProductGroupFixtures.defaultDescriptionHtml(),
                            null,
                            List.of());

            // when & then
            assertThat(description.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("productGroupIdValue()는 ProductGroupId의 값을 반환한다")
        void productGroupIdValueReturnsValue() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupId.of(200L),
                            ProductGroupFixtures.defaultDescriptionHtml(),
                            null,
                            List.of());

            // when & then
            assertThat(description.productGroupIdValue()).isEqualTo(200L);
        }

        @Test
        @DisplayName("contentValue()는 content가 null이면 null을 반환한다")
        void contentValueReturnsNullWhenContentIsNull() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.defaultProductGroupId(),
                            null,
                            null,
                            List.of());

            // when & then
            assertThat(description.contentValue()).isNull();
        }

        @Test
        @DisplayName("cdnPathValue()는 cdnPath가 null이면 null을 반환한다")
        void cdnPathValueReturnsNullWhenCdnPathIsNull() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();

            // when & then
            assertThat(description.cdnPathValue()).isNull();
        }
    }
}
