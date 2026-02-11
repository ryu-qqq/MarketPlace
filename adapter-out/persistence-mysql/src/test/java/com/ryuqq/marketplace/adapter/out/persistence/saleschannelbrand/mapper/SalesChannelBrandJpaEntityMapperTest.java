package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.SalesChannelBrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SalesChannelBrandJpaEntityMapperTest - SalesChannelBrand Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("SalesChannelBrandJpaEntityMapper 단위 테스트")
class SalesChannelBrandJpaEntityMapperTest {

    private SalesChannelBrandJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelBrandJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveBrand_ConvertsCorrectly() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.activeSalesChannelBrand();

            // when
            SalesChannelBrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getExternalBrandCode()).isEqualTo(domain.externalBrandCode());
            assertThat(entity.getExternalBrandName()).isEqualTo(domain.externalBrandName());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveBrand_ConvertsCorrectly() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.inactiveSalesChannelBrand();

            // when
            SalesChannelBrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewBrand_ConvertsCorrectly() {
            // given
            SalesChannelBrand domain = SalesChannelBrandFixtures.newSalesChannelBrand();

            // when
            SalesChannelBrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getExternalBrandCode()).isEqualTo(domain.externalBrandCode());
        }

        @Test
        @DisplayName("특정 salesChannelId를 가진 Domain을 Entity로 변환합니다")
        void toEntity_WithSpecificSalesChannel_ConvertsCorrectly() {
            // given
            Long salesChannelId = 100L;
            SalesChannelBrand domain =
                    SalesChannelBrandFixtures.activeSalesChannelBrand(
                            1L, salesChannelId, "CODE-100");

            // when
            SalesChannelBrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSalesChannelId()).isEqualTo(salesChannelId);
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
            SalesChannelBrandJpaEntity entity = SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannelBrand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.externalBrandCode()).isEqualTo(entity.getExternalBrandCode());
            assertThat(domain.externalBrandName()).isEqualTo(entity.getExternalBrandName());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            SalesChannelBrandJpaEntity entity =
                    SalesChannelBrandJpaEntityFixtures.inactiveEntity(2L);

            // when
            SalesChannelBrand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("ID가 null인 Entity를 변환할 때 예외가 발생합니다")
        void toDomain_WithNullIdEntity_ThrowsException() {
            // given
            SalesChannelBrandJpaEntity entity = SalesChannelBrandJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("특정 salesChannelId를 가진 Entity를 Domain으로 변환합니다")
        void toDomain_WithSpecificSalesChannel_ConvertsCorrectly() {
            // given
            Long salesChannelId = 100L;
            SalesChannelBrandJpaEntity entity =
                    SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(salesChannelId);
            // Set ID manually for test (since it's not persisted)
            entity =
                    SalesChannelBrandJpaEntity.create(
                            1L,
                            entity.getSalesChannelId(),
                            entity.getExternalBrandCode(),
                            entity.getExternalBrandName(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            SalesChannelBrand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.salesChannelId()).isEqualTo(salesChannelId);
        }

        @Test
        @DisplayName("커스텀 외부 브랜드 코드를 가진 Entity를 Domain으로 변환합니다")
        void toDomain_WithCustomCode_ConvertsCorrectly() {
            // given
            String customCode = "CUSTOM-CODE-999";
            SalesChannelBrandJpaEntity entity =
                    SalesChannelBrandJpaEntityFixtures.activeEntityWithCode(customCode);
            entity =
                    SalesChannelBrandJpaEntity.create(
                            1L,
                            entity.getSalesChannelId(),
                            entity.getExternalBrandCode(),
                            entity.getExternalBrandName(),
                            entity.getStatus(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());

            // when
            SalesChannelBrand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.externalBrandCode()).isEqualTo(customCode);
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
            SalesChannelBrand original = SalesChannelBrandFixtures.activeSalesChannelBrand();

            // when
            SalesChannelBrandJpaEntity entity = mapper.toEntity(original);
            SalesChannelBrand converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.salesChannelId()).isEqualTo(original.salesChannelId());
            assertThat(converted.externalBrandCode()).isEqualTo(original.externalBrandCode());
            assertThat(converted.externalBrandName()).isEqualTo(original.externalBrandName());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SalesChannelBrandJpaEntity original =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannelBrand domain = mapper.toDomain(original);
            SalesChannelBrandJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getExternalBrandCode()).isEqualTo(original.getExternalBrandCode());
            assertThat(converted.getExternalBrandName()).isEqualTo(original.getExternalBrandName());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
