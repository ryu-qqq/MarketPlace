package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyProductIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductIdMappingJpaEntityMapperTest - ID 매핑 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyProductIdMappingJpaEntityMapper 단위 테스트")
class LegacyProductIdMappingJpaEntityMapperTest {

    private LegacyProductIdMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyProductIdMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환합니다")
        void toEntity_WithMapping_ConvertsCorrectly() {
            // given
            LegacyProductIdMapping domain = LegacyConversionFixtures.mapping();

            // when
            LegacyProductIdMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getLegacyProductId()).isEqualTo(domain.legacyProductId());
            assertThat(entity.getInternalProductId()).isEqualTo(domain.internalProductId());
            assertThat(entity.getLegacyProductGroupId()).isEqualTo(domain.legacyProductGroupId());
            assertThat(entity.getInternalProductGroupId())
                    .isEqualTo(domain.internalProductGroupId());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("신규 Domain(ID 없음)을 Entity로 변환합니다")
        void toEntity_WithNewMapping_ConvertsCorrectly() {
            // given
            LegacyProductIdMapping domain = LegacyConversionFixtures.newMapping();

            // when
            LegacyProductIdMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getLegacyProductId()).isEqualTo(domain.legacyProductId());
            assertThat(entity.getInternalProductId()).isEqualTo(domain.internalProductId());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환합니다")
        void toDomain_WithEntity_ConvertsCorrectly() {
            // given
            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.entity();

            // when
            LegacyProductIdMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.legacyProductId()).isEqualTo(entity.getLegacyProductId());
            assertThat(domain.internalProductId()).isEqualTo(entity.getInternalProductId());
            assertThat(domain.legacyProductGroupId()).isEqualTo(entity.getLegacyProductGroupId());
            assertThat(domain.internalProductGroupId())
                    .isEqualTo(entity.getInternalProductGroupId());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithNullIdEntity_CreatesNewDomain() {
            // given
            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.newEntity();

            // when
            LegacyProductIdMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.legacyProductId()).isEqualTo(entity.getLegacyProductId());
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
            LegacyProductIdMapping original = LegacyConversionFixtures.mapping();

            // when
            LegacyProductIdMappingJpaEntity entity = mapper.toEntity(original);
            LegacyProductIdMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.legacyProductId()).isEqualTo(original.legacyProductId());
            assertThat(converted.internalProductId()).isEqualTo(original.internalProductId());
            assertThat(converted.legacyProductGroupId()).isEqualTo(original.legacyProductGroupId());
            assertThat(converted.internalProductGroupId())
                    .isEqualTo(original.internalProductGroupId());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            LegacyProductIdMappingJpaEntity original =
                    LegacyProductIdMappingJpaEntityFixtures.entity();

            // when
            LegacyProductIdMapping domain = mapper.toDomain(original);
            LegacyProductIdMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getLegacyProductId()).isEqualTo(original.getLegacyProductId());
            assertThat(converted.getInternalProductId()).isEqualTo(original.getInternalProductId());
            assertThat(converted.getLegacyProductGroupId())
                    .isEqualTo(original.getLegacyProductGroupId());
            assertThat(converted.getInternalProductGroupId())
                    .isEqualTo(original.getInternalProductGroupId());
        }
    }
}
