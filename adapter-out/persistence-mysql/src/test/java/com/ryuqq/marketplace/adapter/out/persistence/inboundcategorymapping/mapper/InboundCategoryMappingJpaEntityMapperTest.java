package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.InboundCategoryMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundCategoryMappingJpaEntityMapperTest - InboundCategoryMapping Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundCategoryMappingJpaEntityMapper 단위 테스트")
class InboundCategoryMappingJpaEntityMapperTest {

    private InboundCategoryMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundCategoryMappingJpaEntityMapper();
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
            InboundCategoryMapping domain = InboundCategoryMappingFixtures.activeMapping();

            // when
            InboundCategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getInboundSourceId()).isEqualTo(domain.inboundSourceId());
            assertThat(entity.getExternalCategoryCode()).isEqualTo(domain.externalCategoryCode());
            assertThat(entity.getExternalCategoryName()).isEqualTo(domain.externalCategoryName());
            assertThat(entity.getInternalCategoryId()).isEqualTo(domain.internalCategoryId());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveMapping_ConvertsCorrectly() {
            // given
            InboundCategoryMapping domain = InboundCategoryMappingFixtures.inactiveMapping();

            // when
            InboundCategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("신규 Domain을 Entity로 변환합니다")
        void toEntity_WithNewMapping_ConvertsCorrectly() {
            // given
            InboundCategoryMapping domain = InboundCategoryMappingFixtures.newMapping();

            // when
            InboundCategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getInboundSourceId()).isEqualTo(domain.inboundSourceId());
            assertThat(entity.getExternalCategoryCode()).isEqualTo(domain.externalCategoryCode());
            assertThat(entity.getInternalCategoryId()).isEqualTo(domain.internalCategoryId());
        }

        @Test
        @DisplayName("특정 파라미터를 가진 신규 Domain을 Entity로 변환합니다")
        void toEntity_WithSpecificParams_ConvertsCorrectly() {
            // given
            InboundCategoryMapping domain =
                    InboundCategoryMappingFixtures.newMapping(2L, "CAT_SPEC", 200L);

            // when
            InboundCategoryMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getInboundSourceId()).isEqualTo(2L);
            assertThat(entity.getExternalCategoryCode()).isEqualTo("CAT_SPEC");
            assertThat(entity.getInternalCategoryId()).isEqualTo(200L);
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
            InboundCategoryMappingJpaEntity entity =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);

            // when
            InboundCategoryMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.inboundSourceId()).isEqualTo(entity.getInboundSourceId());
            assertThat(domain.externalCategoryCode()).isEqualTo(entity.getExternalCategoryCode());
            assertThat(domain.externalCategoryName()).isEqualTo(entity.getExternalCategoryName());
            assertThat(domain.internalCategoryId()).isEqualTo(entity.getInternalCategoryId());
            assertThat(domain.status().name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            InboundCategoryMappingJpaEntity inactiveBase =
                    InboundCategoryMappingJpaEntityFixtures.inactiveEntity();
            InboundCategoryMappingJpaEntity entity =
                    InboundCategoryMappingJpaEntity.create(
                            1L,
                            inactiveBase.getInboundSourceId(),
                            inactiveBase.getExternalCategoryCode(),
                            inactiveBase.getExternalCategoryName(),
                            inactiveBase.getInternalCategoryId(),
                            "INACTIVE",
                            inactiveBase.getCreatedAt(),
                            inactiveBase.getUpdatedAt());

            // when
            InboundCategoryMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            InboundCategoryMappingJpaEntity entity =
                    InboundCategoryMappingJpaEntityFixtures.newEntity();

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
            InboundCategoryMapping original = InboundCategoryMappingFixtures.activeMapping();

            // when
            InboundCategoryMappingJpaEntity entity = mapper.toEntity(original);
            InboundCategoryMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.inboundSourceId()).isEqualTo(original.inboundSourceId());
            assertThat(converted.externalCategoryCode()).isEqualTo(original.externalCategoryCode());
            assertThat(converted.externalCategoryName()).isEqualTo(original.externalCategoryName());
            assertThat(converted.internalCategoryId()).isEqualTo(original.internalCategoryId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            InboundCategoryMappingJpaEntity original =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);

            // when
            InboundCategoryMapping domain = mapper.toDomain(original);
            InboundCategoryMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getInboundSourceId()).isEqualTo(original.getInboundSourceId());
            assertThat(converted.getExternalCategoryCode())
                    .isEqualTo(original.getExternalCategoryCode());
            assertThat(converted.getExternalCategoryName())
                    .isEqualTo(original.getExternalCategoryName());
            assertThat(converted.getInternalCategoryId())
                    .isEqualTo(original.getInternalCategoryId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
