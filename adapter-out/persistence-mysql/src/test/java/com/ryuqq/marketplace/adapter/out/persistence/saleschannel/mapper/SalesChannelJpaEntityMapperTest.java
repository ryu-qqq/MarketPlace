package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SalesChannelJpaEntityMapperTest - 판매 채널 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("SalesChannelJpaEntityMapper 단위 테스트")
class SalesChannelJpaEntityMapperTest {

    private SalesChannelJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveSalesChannel_ConvertsCorrectly() {
            // given
            SalesChannel domain = SalesChannelFixtures.activeSalesChannel();

            // when
            SalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getChannelName()).isEqualTo(domain.channelName());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveSalesChannel_ConvertsCorrectly() {
            // given
            SalesChannel domain = SalesChannelFixtures.inactiveSalesChannel();

            // when
            SalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewSalesChannel_ConvertsCorrectly() {
            // given
            SalesChannel domain = SalesChannelFixtures.newSalesChannel();

            // when
            SalesChannelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getChannelName()).isEqualTo(domain.channelName());
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
            SalesChannelJpaEntity entity = SalesChannelJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.channelName()).isEqualTo(entity.getChannelName());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            SalesChannelJpaEntity entity = SalesChannelJpaEntityFixtures.inactiveEntity(2L);

            // when
            SalesChannel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("INACTIVE");
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
            SalesChannel original = SalesChannelFixtures.activeSalesChannel();

            // when
            SalesChannelJpaEntity entity = mapper.toEntity(original);
            SalesChannel converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.channelName()).isEqualTo(original.channelName());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SalesChannelJpaEntity original = SalesChannelJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannel domain = mapper.toDomain(original);
            SalesChannelJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getChannelName()).isEqualTo(original.getChannelName());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
