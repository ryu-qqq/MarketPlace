package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.CategoryPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CategoryPresetJpaEntityMapperTest - CategoryPreset Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CategoryPresetJpaEntityMapper 단위 테스트")
class CategoryPresetJpaEntityMapperTest {

    private CategoryPresetJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryPresetJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveCategoryPreset_ConvertsCorrectly() {
            // given
            CategoryPreset domain = CategoryPresetFixtures.activeCategoryPreset();

            // when
            CategoryPresetJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getShopId()).isEqualTo(domain.shopId());
            assertThat(entity.getSalesChannelCategoryId())
                    .isEqualTo(domain.salesChannelCategoryId());
            assertThat(entity.getPresetName()).isEqualTo(domain.presetName());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveCategoryPreset_ConvertsCorrectly() {
            // given
            CategoryPreset domain = CategoryPresetFixtures.inactiveCategoryPreset();

            // when
            CategoryPresetJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewCategoryPreset_ConvertsCorrectly() {
            // given
            CategoryPreset domain = CategoryPresetFixtures.newCategoryPreset();

            // when
            CategoryPresetJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPresetName()).isEqualTo(domain.presetName());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            CategoryPresetJpaEntity entity = CategoryPresetJpaEntityFixtures.activeEntity(1L);

            // when
            CategoryPreset domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.shopId()).isEqualTo(entity.getShopId());
            assertThat(domain.salesChannelCategoryId())
                    .isEqualTo(entity.getSalesChannelCategoryId());
            assertThat(domain.presetName()).isEqualTo(entity.getPresetName());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            CategoryPresetJpaEntity entity = CategoryPresetJpaEntityFixtures.activeEntity(2L);
            // Create an inactive entity with ID
            CategoryPresetJpaEntity inactiveEntity =
                    CategoryPresetJpaEntity.create(
                            2L,
                            entity.getShopId(),
                            entity.getSalesChannelCategoryId(),
                            entity.getPresetName(),
                            "INACTIVE",
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            CategoryPreset domain = mapper.toDomain(inactiveEntity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }
    }

    // ========================================================================
    // 3. toResult 테스트
    // ========================================================================

    @Nested
    @DisplayName("toResult 메서드 테스트")
    class ToResultTest {

        @Test
        @DisplayName("CategoryPresetCompositeDto를 CategoryPresetResult로 변환합니다")
        void toResult_WithCompositeDto_ConvertsAllFieldsCorrectly() {
            // given
            Instant now = Instant.now();
            CategoryPresetCompositeDto dto =
                    new CategoryPresetCompositeDto(
                            100L,
                            10L,
                            "테스트샵",
                            "account-001",
                            20L,
                            "테스트채널",
                            30L,
                            "EXT-CAT-001",
                            "패션의류 > 상의 > 티셔츠",
                            "테스트프리셋",
                            "ACTIVE",
                            now);

            // when
            CategoryPresetResult result = mapper.toResult(dto);

            // then
            assertThat(result.id()).isEqualTo(100L);
            assertThat(result.shopId()).isEqualTo(10L);
            assertThat(result.shopName()).isEqualTo("테스트샵");
            assertThat(result.salesChannelId()).isEqualTo(20L);
            assertThat(result.salesChannelName()).isEqualTo("테스트채널");
            assertThat(result.accountId()).isEqualTo("account-001");
            assertThat(result.presetName()).isEqualTo("테스트프리셋");
            assertThat(result.categoryPath()).isEqualTo("패션의류 > 상의 > 티셔츠");
            assertThat(result.categoryCode()).isEqualTo("EXT-CAT-001");
            assertThat(result.createdAt()).isEqualTo(now);
        }
    }

    // ========================================================================
    // 4. toDomain 예외 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 예외 테스트")
    class ToDomainExceptionTest {

        @Test
        @DisplayName("Entity의 ID가 null이면 IllegalStateException이 발생합니다")
        void toDomain_WithNullId_ThrowsIllegalStateException() {
            // given
            CategoryPresetJpaEntity entityWithNullId =
                    CategoryPresetJpaEntity.create(
                            null, 10L, 30L, "테스트프리셋", "ACTIVE", Instant.now(), Instant.now());

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entityWithNullId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("null");
        }
    }

    // ========================================================================
    // 5. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            CategoryPreset original = CategoryPresetFixtures.activeCategoryPreset();

            // when
            CategoryPresetJpaEntity entity = mapper.toEntity(original);
            CategoryPreset converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.shopId()).isEqualTo(original.shopId());
            assertThat(converted.salesChannelCategoryId())
                    .isEqualTo(original.salesChannelCategoryId());
            assertThat(converted.presetName()).isEqualTo(original.presetName());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            CategoryPresetJpaEntity original = CategoryPresetJpaEntityFixtures.activeEntity(1L);

            // when
            CategoryPreset domain = mapper.toDomain(original);
            CategoryPresetJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getShopId()).isEqualTo(original.getShopId());
            assertThat(converted.getSalesChannelCategoryId())
                    .isEqualTo(original.getSalesChannelCategoryId());
            assertThat(converted.getPresetName()).isEqualTo(original.getPresetName());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
