package com.ryuqq.marketplace.domain.imagevariant.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.id.ImageVariantId;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariant Aggregate 테스트")
class ImageVariantTest {

    @Nested
    @DisplayName("forNew() - 신규 ImageVariant 생성")
    class ForNewTest {

        @Test
        @DisplayName("SMALL_WEBP 타입으로 신규 ImageVariant를 생성한다")
        void createNewSmallWebpVariant() {
            // given
            Long sourceImageId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageVariantType variantType = ImageVariantType.SMALL_WEBP;
            ResultAssetId resultAssetId = ResultAssetId.of("asset-001");
            ImageUrl variantUrl = ImageUrl.of("https://cdn.example.com/small.webp");
            ImageDimension dimension = ImageDimension.of(300, 300);
            Instant now = CommonVoFixtures.now();

            // when
            ImageVariant variant =
                    ImageVariant.forNew(
                            sourceImageId,
                            sourceType,
                            variantType,
                            resultAssetId,
                            variantUrl,
                            dimension,
                            now);

            // then
            assertThat(variant.id().isNew()).isTrue();
            assertThat(variant.sourceImageId()).isEqualTo(sourceImageId);
            assertThat(variant.sourceType()).isEqualTo(sourceType);
            assertThat(variant.variantType()).isEqualTo(variantType);
            assertThat(variant.resultAssetIdValue()).isEqualTo("asset-001");
            assertThat(variant.variantUrlValue()).isEqualTo("https://cdn.example.com/small.webp");
            assertThat(variant.width()).isEqualTo(300);
            assertThat(variant.height()).isEqualTo(300);
            assertThat(variant.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ORIGINAL_WEBP 타입은 dimension이 null이어도 생성된다")
        void createNewOriginalWebpVariant() {
            // given
            ImageDimension dimension = ImageDimension.of(null, null);
            Instant now = CommonVoFixtures.now();

            // when
            ImageVariant variant =
                    ImageVariant.forNew(
                            100L,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageVariantType.ORIGINAL_WEBP,
                            ResultAssetId.of("asset-002"),
                            ImageUrl.of("https://cdn.example.com/original.webp"),
                            dimension,
                            now);

            // then
            assertThat(variant.variantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
            assertThat(variant.width()).isNull();
            assertThat(variant.height()).isNull();
            assertThat(variant.dimension().hasValues()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("모든 필드를 포함하여 재구성한다")
        void reconstituteFromDb() {
            // given
            ImageVariantId id = ImageVariantId.of(1L);
            Instant createdAt = CommonVoFixtures.yesterday();

            // when
            ImageVariant variant =
                    ImageVariant.reconstitute(
                            id,
                            200L,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.LARGE_WEBP,
                            ResultAssetId.of("asset-003"),
                            ImageUrl.of("https://cdn.example.com/large.webp"),
                            ImageDimension.of(1200, 1200),
                            createdAt);

            // then
            assertThat(variant.idValue()).isEqualTo(1L);
            assertThat(variant.sourceImageId()).isEqualTo(200L);
            assertThat(variant.variantType()).isEqualTo(ImageVariantType.LARGE_WEBP);
            assertThat(variant.width()).isEqualTo(1200);
            assertThat(variant.height()).isEqualTo(1200);
            assertThat(variant.createdAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("Fixtures를 사용한 생성 테스트")
    class FixturesTest {

        @Test
        @DisplayName("Fixtures로 생성된 객체가 올바르다")
        void fixturesCreateValidObjects() {
            ImageVariant small = ImageVariantFixtures.newSmallWebpVariant();
            assertThat(small.variantType()).isEqualTo(ImageVariantType.SMALL_WEBP);
            assertThat(small.width()).isEqualTo(300);

            ImageVariant medium = ImageVariantFixtures.newMediumWebpVariant();
            assertThat(medium.variantType()).isEqualTo(ImageVariantType.MEDIUM_WEBP);
            assertThat(medium.width()).isEqualTo(600);

            ImageVariant original = ImageVariantFixtures.newOriginalWebpVariant();
            assertThat(original.variantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
            assertThat(original.width()).isNull();
        }
    }
}
