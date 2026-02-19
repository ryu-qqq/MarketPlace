package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.ImageVariantJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ImageVariantJpaEntityMapperTest - 이미지 Variant Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ImageVariantJpaEntityMapper 단위 테스트")
class ImageVariantJpaEntityMapperTest {

    private ImageVariantJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ImageVariantJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("SMALL_WEBP 타입 Domain을 Entity로 변환합니다")
        void toEntity_WithSmallWebpVariant_ConvertsCorrectly() {
            // given
            ImageVariant domain = ImageVariantFixtures.newSmallWebpVariant();

            // when
            ImageVariantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSourceImageId()).isEqualTo(domain.sourceImageId());
            assertThat(entity.getSourceType()).isEqualTo(domain.sourceType());
            assertThat(entity.getVariantType()).isEqualTo(ImageVariantType.SMALL_WEBP);
            assertThat(entity.getResultAssetId()).isEqualTo(domain.resultAssetIdValue());
            assertThat(entity.getVariantUrl()).isEqualTo(domain.variantUrlValue());
            assertThat(entity.getWidth()).isEqualTo(domain.width());
            assertThat(entity.getHeight()).isEqualTo(domain.height());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("MEDIUM_WEBP 타입 Domain을 Entity로 변환합니다")
        void toEntity_WithMediumWebpVariant_ConvertsCorrectly() {
            // given
            ImageVariant domain = ImageVariantFixtures.newMediumWebpVariant();

            // when
            ImageVariantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getVariantType()).isEqualTo(ImageVariantType.MEDIUM_WEBP);
            assertThat(entity.getWidth()).isEqualTo(600);
            assertThat(entity.getHeight()).isEqualTo(600);
        }

        @Test
        @DisplayName("ORIGINAL_WEBP 타입 Domain을 Entity로 변환합니다 - 크기가 null입니다")
        void toEntity_WithOriginalWebpVariant_ConvertsCorrectly() {
            // given
            ImageVariant domain = ImageVariantFixtures.newOriginalWebpVariant();

            // when
            ImageVariantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getVariantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
            assertThat(entity.getWidth()).isNull();
            assertThat(entity.getHeight()).isNull();
        }

        @Test
        @DisplayName("reconstituted Domain을 Entity로 변환합니다 - ID가 보존됩니다")
        void toEntity_WithReconstituedVariant_PreservesId() {
            // given
            ImageVariant domain = ImageVariantFixtures.reconstitutedVariant(42L);

            // when
            ImageVariantJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(42L);
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("SMALL_WEBP Entity를 Domain으로 변환합니다")
        void toDomain_WithSmallWebpEntity_ConvertsCorrectly() {
            // given
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.newSmallWebpEntity();

            // when
            ImageVariant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.sourceImageId()).isEqualTo(entity.getSourceImageId());
            assertThat(domain.sourceType()).isEqualTo(entity.getSourceType());
            assertThat(domain.variantType()).isEqualTo(entity.getVariantType());
            assertThat(domain.resultAssetIdValue()).isEqualTo(entity.getResultAssetId());
            assertThat(domain.variantUrlValue()).isEqualTo(entity.getVariantUrl());
            assertThat(domain.width()).isEqualTo(entity.getWidth());
            assertThat(domain.height()).isEqualTo(entity.getHeight());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("ORIGINAL_WEBP Entity를 Domain으로 변환합니다 - 크기가 null입니다")
        void toDomain_WithOriginalWebpEntity_NullDimensionConvertsCorrectly() {
            // given
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.newOriginalWebpEntity();

            // when
            ImageVariant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.variantType()).isEqualTo(ImageVariantType.ORIGINAL_WEBP);
            assertThat(domain.width()).isNull();
            assertThat(domain.height()).isNull();
            assertThat(domain.dimension().hasValues()).isFalse();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다 - 신규 ID가 할당됩니다")
        void toDomain_WithNullIdEntity_AssignsNewId() {
            // given
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.newSmallWebpEntity();

            // when
            ImageVariant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("ID가 있는 Entity를 Domain으로 변환합니다 - ID가 보존됩니다")
        void toDomain_WithExistingIdEntity_PreservesId() {
            // given
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.entityWithId(99L);

            // when
            ImageVariant domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(99L);
            assertThat(domain.id().isNew()).isFalse();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ImageVariantJpaEntity original = ImageVariantJpaEntityFixtures.entityWithId(10L);

            // when
            ImageVariant domain = mapper.toDomain(original);
            ImageVariantJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSourceImageId()).isEqualTo(original.getSourceImageId());
            assertThat(converted.getSourceType()).isEqualTo(original.getSourceType());
            assertThat(converted.getVariantType()).isEqualTo(original.getVariantType());
            assertThat(converted.getResultAssetId()).isEqualTo(original.getResultAssetId());
            assertThat(converted.getVariantUrl()).isEqualTo(original.getVariantUrl());
            assertThat(converted.getWidth()).isEqualTo(original.getWidth());
            assertThat(converted.getHeight()).isEqualTo(original.getHeight());
        }

        @Test
        @DisplayName("ORIGINAL_WEBP Entity 양방향 변환 시 null 크기가 보존됩니다")
        void roundTrip_OriginalWebpEntity_NullDimensionPreserved() {
            // given
            ImageVariantJpaEntity original = ImageVariantJpaEntityFixtures.newOriginalWebpEntity();

            // when
            ImageVariant domain = mapper.toDomain(original);
            ImageVariantJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getWidth()).isNull();
            assertThat(converted.getHeight()).isNull();
        }
    }
}
