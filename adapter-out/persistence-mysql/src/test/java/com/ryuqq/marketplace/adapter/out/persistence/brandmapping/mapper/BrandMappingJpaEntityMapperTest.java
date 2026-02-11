package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.BrandMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.brandmapping.BrandMappingFixtures;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * BrandMappingJpaEntityMapperTest - BrandMapping Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("BrandMappingJpaEntityMapper 단위 테스트")
class BrandMappingJpaEntityMapperTest {

    private BrandMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BrandMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveBrandMapping_ConvertsCorrectly() {
            // given
            BrandMapping domain = BrandMappingFixtures.activeBrandMapping();

            // when
            BrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getPresetId()).isEqualTo(domain.presetId());
            assertThat(entity.getSalesChannelBrandId()).isEqualTo(domain.salesChannelBrandId());
            assertThat(entity.getInternalBrandId()).isEqualTo(domain.internalBrandId());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveBrandMapping_ConvertsCorrectly() {
            // given
            BrandMapping domain = BrandMappingFixtures.inactiveBrandMapping();

            // when
            BrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewBrandMapping_ConvertsCorrectly() {
            // given
            BrandMapping domain = BrandMappingFixtures.newBrandMapping();

            // when
            BrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPresetId()).isEqualTo(domain.presetId());
            assertThat(entity.getSalesChannelBrandId()).isEqualTo(domain.salesChannelBrandId());
            assertThat(entity.getInternalBrandId()).isEqualTo(domain.internalBrandId());
        }

        @Test
        @DisplayName("PresetId가 null인 Domain을 Entity로 변환합니다")
        void toEntity_WithNullPresetId_ConvertsCorrectly() {
            // given
            BrandMapping domain = BrandMappingFixtures.newBrandMapping(null, 100L, 10L);

            // when
            BrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getPresetId()).isNull();
            assertThat(entity.getSalesChannelBrandId()).isEqualTo(100L);
            assertThat(entity.getInternalBrandId()).isEqualTo(10L);
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
            BrandMappingJpaEntity entity = BrandMappingJpaEntityFixtures.activeEntity(1L);

            // when
            BrandMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.presetId()).isEqualTo(entity.getPresetId());
            assertThat(domain.salesChannelBrandId()).isEqualTo(entity.getSalesChannelBrandId());
            assertThat(domain.internalBrandId()).isEqualTo(entity.getInternalBrandId());
            assertThat(domain.status().name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            BrandMappingJpaEntity entity = BrandMappingJpaEntityFixtures.inactiveEntity();
            entity =
                    BrandMappingJpaEntity.create(
                            1L,
                            entity.getPresetId(),
                            entity.getSalesChannelBrandId(),
                            entity.getInternalBrandId(),
                            "INACTIVE",
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            BrandMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("PresetId가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutPresetId_ConvertsCorrectly() {
            // given
            BrandMappingJpaEntity entity = BrandMappingJpaEntityFixtures.entityWithoutPresetId();
            entity =
                    BrandMappingJpaEntity.create(
                            1L,
                            null,
                            entity.getSalesChannelBrandId(),
                            entity.getInternalBrandId(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            BrandMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.presetId()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            BrandMappingJpaEntity entity = BrandMappingJpaEntityFixtures.newEntity();

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
            BrandMapping original = BrandMappingFixtures.activeBrandMapping();

            // when
            BrandMappingJpaEntity entity = mapper.toEntity(original);
            BrandMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.presetId()).isEqualTo(original.presetId());
            assertThat(converted.salesChannelBrandId()).isEqualTo(original.salesChannelBrandId());
            assertThat(converted.internalBrandId()).isEqualTo(original.internalBrandId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            BrandMappingJpaEntity original = BrandMappingJpaEntityFixtures.activeEntity(1L);

            // when
            BrandMapping domain = mapper.toDomain(original);
            BrandMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getPresetId()).isEqualTo(original.getPresetId());
            assertThat(converted.getSalesChannelBrandId())
                    .isEqualTo(original.getSalesChannelBrandId());
            assertThat(converted.getInternalBrandId()).isEqualTo(original.getInternalBrandId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
