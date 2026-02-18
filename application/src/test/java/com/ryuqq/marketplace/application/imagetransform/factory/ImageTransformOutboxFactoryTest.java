package com.ryuqq.marketplace.application.imagetransform.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageTransformOutboxFactory 단위 테스트")
class ImageTransformOutboxFactoryTest {

    private ImageTransformOutboxFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ImageTransformOutboxFactory();
    }

    @Nested
    @DisplayName(
            "createOutboxes(sourceImageId, sourceType, uploadedUrl) - 전체 Variant 타입으로 Outbox 생성")
    class CreateAllOutboxesTest {

        @Test
        @DisplayName("전체 Variant 타입 수만큼 Outbox를 생성한다")
        void createOutboxes_AllVariantTypes_ReturnsOutboxesForAllTypes() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl);

            // then
            assertThat(outboxes).hasSize(ImageVariantType.values().length);
        }

        @Test
        @DisplayName("생성된 Outbox는 PENDING 상태이다")
        void createOutboxes_AllVariants_OutboxesArePending() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl);

            // then
            assertThat(outboxes).allMatch(ImageTransformOutbox::isPending);
        }

        @Test
        @DisplayName("생성된 Outbox는 모두 각기 다른 Variant 타입을 갖는다")
        void createOutboxes_AllVariants_EachOutboxHasDistinctVariantType() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl);

            // then
            List<ImageVariantType> variantTypes =
                    outboxes.stream().map(ImageTransformOutbox::variantType).toList();
            assertThat(variantTypes).containsExactlyInAnyOrder(ImageVariantType.values());
        }

        @Test
        @DisplayName("생성된 Outbox는 전달한 sourceImageId를 갖는다")
        void createOutboxes_ValidSourceImageId_OutboxesHaveCorrectSourceImageId() {
            // given
            Long sourceImageId = 42L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl);

            // then
            assertThat(outboxes).allMatch(o -> o.sourceImageId().equals(sourceImageId));
        }
    }

    @Nested
    @DisplayName(
            "createOutboxes(sourceImageId, sourceType, uploadedUrl, variantTypes) - 지정 Variant 타입으로"
                    + " Outbox 생성")
    class CreateSpecificOutboxesTest {

        @Test
        @DisplayName("지정한 Variant 타입 수만큼 Outbox를 생성한다")
        void createOutboxes_SpecificVariantTypes_ReturnsMatchingCount() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);
            List<ImageVariantType> variantTypes =
                    List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl, variantTypes);

            // then
            assertThat(outboxes).hasSize(2);
        }

        @Test
        @DisplayName("지정한 Variant 타입만 포함한 Outbox를 생성한다")
        void createOutboxes_SpecificVariantTypes_OutboxesHaveRequestedTypes() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);
            List<ImageVariantType> variantTypes =
                    List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.LARGE_WEBP);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl, variantTypes);

            // then
            List<ImageVariantType> resultTypes =
                    outboxes.stream().map(ImageTransformOutbox::variantType).toList();
            assertThat(resultTypes)
                    .containsExactlyInAnyOrder(
                            ImageVariantType.SMALL_WEBP, ImageVariantType.LARGE_WEBP);
        }

        @Test
        @DisplayName("단일 Variant 타입으로 단일 Outbox를 생성한다")
        void createOutboxes_SingleVariantType_ReturnsSingleOutbox() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);
            List<ImageVariantType> variantTypes = List.of(ImageVariantType.ORIGINAL_WEBP);

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl, variantTypes);

            // then
            assertThat(outboxes).hasSize(1);
            assertThat(outboxes.getFirst().variantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
        }

        @Test
        @DisplayName("빈 Variant 타입 목록으로 빈 Outbox 목록을 반환한다")
        void createOutboxes_EmptyVariantTypes_ReturnsEmptyList() {
            // given
            Long sourceImageId = ImageTransformFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageTransformFixtures.DEFAULT_SOURCE_TYPE;
            ImageUrl uploadedUrl = ImageUrl.of(ImageTransformFixtures.DEFAULT_UPLOADED_URL);
            List<ImageVariantType> emptyTypes = List.of();

            // when
            List<ImageTransformOutbox> outboxes =
                    sut.createOutboxes(sourceImageId, sourceType, uploadedUrl, emptyTypes);

            // then
            assertThat(outboxes).isEmpty();
        }
    }
}
