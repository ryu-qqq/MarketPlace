package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.CategoryMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.categorymapping.CategoryMappingFixtures;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CategoryMappingJpaEntityMapperTest - CategoryMapping Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CategoryMappingJpaEntityMapper 단위 테스트")
class CategoryMappingJpaEntityMapperTest {

    private CategoryMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveCategoryMapping_ConvertsCorrectly() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.activeCategoryMapping();

            // when
            CategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getPresetId()).isEqualTo(domain.presetId());
            assertThat(entity.getSalesChannelCategoryId())
                    .isEqualTo(domain.salesChannelCategoryId());
            assertThat(entity.getInternalCategoryId()).isEqualTo(domain.internalCategoryId());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveCategoryMapping_ConvertsCorrectly() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.inactiveCategoryMapping();

            // when
            CategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewCategoryMapping_ConvertsCorrectly() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.newCategoryMapping();

            // when
            CategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPresetId()).isEqualTo(domain.presetId());
            assertThat(entity.getSalesChannelCategoryId())
                    .isEqualTo(domain.salesChannelCategoryId());
            assertThat(entity.getInternalCategoryId()).isEqualTo(domain.internalCategoryId());
        }

        @Test
        @DisplayName("PresetId가 null인 Domain을 Entity로 변환합니다")
        void toEntity_WithNullPresetId_ConvertsCorrectly() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.newCategoryMapping(null, 200L, 20L);

            // when
            CategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getPresetId()).isNull();
            assertThat(entity.getSalesChannelCategoryId()).isEqualTo(200L);
            assertThat(entity.getInternalCategoryId()).isEqualTo(20L);
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
            CategoryMappingJpaEntity entity = CategoryMappingJpaEntityFixtures.activeEntity(1L);

            // when
            CategoryMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.presetId()).isEqualTo(entity.getPresetId());
            assertThat(domain.salesChannelCategoryId())
                    .isEqualTo(entity.getSalesChannelCategoryId());
            assertThat(domain.internalCategoryId()).isEqualTo(entity.getInternalCategoryId());
            assertThat(domain.status().name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            CategoryMappingJpaEntity entity = CategoryMappingJpaEntityFixtures.inactiveEntity();
            entity =
                    CategoryMappingJpaEntity.create(
                            1L,
                            entity.getPresetId(),
                            entity.getSalesChannelCategoryId(),
                            entity.getInternalCategoryId(),
                            "INACTIVE",
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            CategoryMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("PresetId가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutPresetId_ConvertsCorrectly() {
            // given
            CategoryMappingJpaEntity entity =
                    CategoryMappingJpaEntityFixtures.entityWithoutPresetId();
            entity =
                    CategoryMappingJpaEntity.create(
                            1L,
                            null,
                            entity.getSalesChannelCategoryId(),
                            entity.getInternalCategoryId(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            CategoryMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.presetId()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            CategoryMappingJpaEntity entity = CategoryMappingJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            CategoryMapping original = CategoryMappingFixtures.activeCategoryMapping();

            // when
            CategoryMappingJpaEntity entity = mapper.toEntity(original);
            CategoryMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.presetId()).isEqualTo(original.presetId());
            assertThat(converted.salesChannelCategoryId())
                    .isEqualTo(original.salesChannelCategoryId());
            assertThat(converted.internalCategoryId()).isEqualTo(original.internalCategoryId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            CategoryMappingJpaEntity original = CategoryMappingJpaEntityFixtures.activeEntity(1L);

            // when
            CategoryMapping domain = mapper.toDomain(original);
            CategoryMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getPresetId()).isEqualTo(original.getPresetId());
            assertThat(converted.getSalesChannelCategoryId())
                    .isEqualTo(original.getSalesChannelCategoryId());
            assertThat(converted.getInternalCategoryId())
                    .isEqualTo(original.getInternalCategoryId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
