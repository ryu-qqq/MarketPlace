package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyOrderIdMappingJpaEntityMapperTest - 주문 ID 매핑 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyOrderIdMappingJpaEntityMapper 단위 테스트")
class LegacyOrderIdMappingJpaEntityMapperTest {

    private LegacyOrderIdMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyOrderIdMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 올바르게 변환합니다")
        void toEntity_WithValidDomain_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            LegacyOrderIdMapping domain =
                    LegacyOrderIdMapping.reconstitute(
                            LegacyOrderIdMappingId.of(1L),
                            LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_ORDER_ID,
                            LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_PAYMENT_ID,
                            LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_INTERNAL_ORDER_ID,
                            LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_CHANNEL_NAME,
                            now);

            // when
            LegacyOrderIdMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getLegacyOrderId()).isEqualTo(domain.legacyOrderId());
            assertThat(entity.getLegacyPaymentId()).isEqualTo(domain.legacyPaymentId());
            assertThat(entity.getInternalOrderId()).isEqualTo(domain.internalOrderId());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getChannelName()).isEqualTo(domain.channelName());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("신규 Domain(ID 없음)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_SetsNullId() {
            // given
            LegacyOrderIdMapping domain =
                    LegacyOrderIdMapping.forNew(
                            10001L, 20001L, "order-uuid", 1L, "NAVER", Instant.now());

            // when
            LegacyOrderIdMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getLegacyOrderId()).isEqualTo(domain.legacyOrderId());
            assertThat(entity.getInternalOrderId()).isEqualTo(domain.internalOrderId());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 올바르게 변환합니다")
        void toDomain_WithValidEntity_ConvertsCorrectly() {
            // given
            LegacyOrderIdMappingJpaEntity entity = LegacyOrderIdMappingJpaEntityFixtures.entity();

            // when
            LegacyOrderIdMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.legacyOrderId()).isEqualTo(entity.getLegacyOrderId());
            assertThat(domain.legacyPaymentId()).isEqualTo(entity.getLegacyPaymentId());
            assertThat(domain.internalOrderId()).isEqualTo(entity.getInternalOrderId());
            assertThat(domain.salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.channelName()).isEqualTo(entity.getChannelName());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithNullIdEntity_CreatesNewDomain() {
            // given
            LegacyOrderIdMappingJpaEntity entity =
                    LegacyOrderIdMappingJpaEntityFixtures.newEntity();

            // when
            LegacyOrderIdMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
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
            LegacyOrderIdMappingJpaEntity original = LegacyOrderIdMappingJpaEntityFixtures.entity();

            // when
            LegacyOrderIdMapping domain = mapper.toDomain(original);
            LegacyOrderIdMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getLegacyOrderId()).isEqualTo(original.getLegacyOrderId());
            assertThat(converted.getLegacyPaymentId()).isEqualTo(original.getLegacyPaymentId());
            assertThat(converted.getInternalOrderId()).isEqualTo(original.getInternalOrderId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getChannelName()).isEqualTo(original.getChannelName());
        }
    }
}
