package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.InboundBrandMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundBrandMappingJpaEntityMapperTest - InboundBrandMapping Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundBrandMappingJpaEntityMapper 단위 테스트")
class InboundBrandMappingJpaEntityMapperTest {

    private InboundBrandMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundBrandMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveMapping_ConvertsCorrectly() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.activeMapping();

            // when
            InboundBrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getInboundSourceId()).isEqualTo(domain.inboundSourceId());
            assertThat(entity.getExternalBrandCode()).isEqualTo(domain.externalBrandCode());
            assertThat(entity.getExternalBrandName()).isEqualTo(domain.externalBrandName());
            assertThat(entity.getInternalBrandId()).isEqualTo(domain.internalBrandId());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveMapping_ConvertsCorrectly() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.inactiveMapping();

            // when
            InboundBrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("신규 Domain을 Entity로 변환합니다")
        void toEntity_WithNewMapping_ConvertsCorrectly() {
            // given
            InboundBrandMapping domain = InboundBrandMappingFixtures.newMapping();

            // when
            InboundBrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getInboundSourceId()).isEqualTo(domain.inboundSourceId());
            assertThat(entity.getExternalBrandCode()).isEqualTo(domain.externalBrandCode());
            assertThat(entity.getInternalBrandId()).isEqualTo(domain.internalBrandId());
        }

        @Test
        @DisplayName("특정 파라미터를 가진 신규 Domain을 Entity로 변환합니다")
        void toEntity_WithSpecificParams_ConvertsCorrectly() {
            // given
            InboundBrandMapping domain =
                    InboundBrandMappingFixtures.newMapping(2L, "BR_SPEC", 200L);

            // when
            InboundBrandMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getInboundSourceId()).isEqualTo(2L);
            assertThat(entity.getExternalBrandCode()).isEqualTo("BR_SPEC");
            assertThat(entity.getInternalBrandId()).isEqualTo(200L);
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
            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);

            // when
            InboundBrandMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.inboundSourceId()).isEqualTo(entity.getInboundSourceId());
            assertThat(domain.externalBrandCode()).isEqualTo(entity.getExternalBrandCode());
            assertThat(domain.externalBrandName()).isEqualTo(entity.getExternalBrandName());
            assertThat(domain.internalBrandId()).isEqualTo(entity.getInternalBrandId());
            assertThat(domain.status().name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            InboundBrandMappingJpaEntity inactiveBase =
                    InboundBrandMappingJpaEntityFixtures.inactiveEntity();
            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntity.create(
                            1L,
                            inactiveBase.getInboundSourceId(),
                            inactiveBase.getExternalBrandCode(),
                            inactiveBase.getExternalBrandName(),
                            inactiveBase.getInternalBrandId(),
                            "INACTIVE",
                            inactiveBase.getCreatedAt(),
                            inactiveBase.getUpdatedAt());

            // when
            InboundBrandMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            InboundBrandMappingJpaEntity entity = InboundBrandMappingJpaEntityFixtures.newEntity();

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
            InboundBrandMapping original = InboundBrandMappingFixtures.activeMapping();

            // when
            InboundBrandMappingJpaEntity entity = mapper.toEntity(original);
            InboundBrandMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.inboundSourceId()).isEqualTo(original.inboundSourceId());
            assertThat(converted.externalBrandCode()).isEqualTo(original.externalBrandCode());
            assertThat(converted.externalBrandName()).isEqualTo(original.externalBrandName());
            assertThat(converted.internalBrandId()).isEqualTo(original.internalBrandId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            InboundBrandMappingJpaEntity original =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);

            // when
            InboundBrandMapping domain = mapper.toDomain(original);
            InboundBrandMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getInboundSourceId()).isEqualTo(original.getInboundSourceId());
            assertThat(converted.getExternalBrandCode()).isEqualTo(original.getExternalBrandCode());
            assertThat(converted.getExternalBrandName()).isEqualTo(original.getExternalBrandName());
            assertThat(converted.getInternalBrandId()).isEqualTo(original.getInternalBrandId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
