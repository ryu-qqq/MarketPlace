package com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ExchangePersistenceMapperTest - 교환 클레임 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ExchangePersistenceMapper 단위 테스트")
class ExchangePersistenceMapperTest {

    private ExchangePersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExchangePersistenceMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("REQUESTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithRequestedClaim_ConvertsCorrectly() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getClaimNumber()).isEqualTo(domain.claimNumberValue());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getExchangeQty()).isEqualTo(domain.exchangeQty());
            assertThat(entity.getExchangeStatus()).isEqualTo(ExchangeStatus.REQUESTED.name());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedClaim_ConvertsCorrectly() {
            // given
            ExchangeClaim domain = ExchangeFixtures.completedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExchangeStatus()).isEqualTo(ExchangeStatus.COMPLETED.name());
            assertThat(entity.getLinkedOrderId()).isNotNull();
        }

        @Test
        @DisplayName("교환 옵션이 있는 Domain을 Entity로 변환합니다")
        void toEntity_WithExchangeOption_ConvertsOptionFields() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOriginalProductId()).isNotNull();
            assertThat(entity.getOriginalSkuCode()).isNotNull();
            assertThat(entity.getTargetProductGroupId()).isNotNull();
            assertThat(entity.getTargetProductId()).isNotNull();
            assertThat(entity.getTargetSkuCode()).isNotNull();
            assertThat(entity.getTargetQuantity()).isNotNull();
        }

        @Test
        @DisplayName("AmountAdjustment가 있는 Domain을 Entity로 변환합니다")
        void toEntity_WithAmountAdjustment_ConvertsAdjustmentFields() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOriginalPrice()).isNotNull();
            assertThat(entity.getTargetPrice()).isNotNull();
            assertThat(entity.getShippingFeePayer()).isNotNull();
        }

        @Test
        @DisplayName("교환 사유가 Entity의 reasonType, reasonDetail로 변환됩니다")
        void toEntity_WithExchangeReason_ConvertsReasonFields() {
            // given
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReasonType()).isEqualTo(domain.reason().reasonType().name());
            assertThat(entity.getReasonDetail()).isEqualTo(domain.reason().reasonDetail());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("REQUESTED Entity를 Domain으로 변환합니다")
        void toDomain_WithRequestedEntity_ConvertsCorrectly() {
            // given
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.claimNumberValue()).isEqualTo(entity.getClaimNumber());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.status()).isEqualTo(ExchangeStatus.REQUESTED);
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.completedEntity();

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.status()).isEqualTo(ExchangeStatus.COMPLETED);
            assertThat(domain.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("교환 옵션이 있는 Entity를 Domain으로 변환합니다")
        void toDomain_WithExchangeOption_ReturnsExchangeOption() {
            // given
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.exchangeOption()).isNotNull();
            assertThat(domain.exchangeOption().originalSkuCode())
                    .isEqualTo(entity.getOriginalSkuCode());
            assertThat(domain.exchangeOption().targetSkuCode())
                    .isEqualTo(entity.getTargetSkuCode());
        }

        @Test
        @DisplayName("교환 옵션이 없는 Entity는 exchangeOption이 null입니다")
        void toDomain_WithoutExchangeOption_ReturnsNullOption() {
            // given
            ExchangeClaimJpaEntity entity =
                    ExchangeClaimJpaEntityFixtures.minimalEntity("minimal-001");

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.exchangeOption()).isNull();
        }

        @Test
        @DisplayName("AmountAdjustment가 있는 Entity를 Domain으로 변환합니다")
        void toDomain_WithAmountAdjustment_ReturnsAdjustment() {
            // given
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.amountAdjustment()).isNotNull();
            assertThat(domain.amountAdjustment().originalPrice().value())
                    .isEqualTo(entity.getOriginalPrice());
        }

        @Test
        @DisplayName("AmountAdjustment가 없는 Entity는 amountAdjustment가 null입니다")
        void toDomain_WithoutAmountAdjustment_ReturnsNullAdjustment() {
            // given
            ExchangeClaimJpaEntity entity =
                    ExchangeClaimJpaEntityFixtures.minimalEntity("minimal-002");

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.amountAdjustment()).isNull();
        }

        @Test
        @DisplayName("reasonDetail이 null인 Entity도 Domain으로 변환됩니다")
        void toDomain_WithNullReasonDetail_ConvertsToEmptyString() {
            // given - reasonDetail이 null인 entity
            ExchangeClaimJpaEntity entity =
                    ExchangeClaimJpaEntityFixtures.minimalEntity("minimal-003");

            // when
            ExchangeClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.reason().reasonDetail()).isNotNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesCoreData() {
            // given
            ExchangeClaim original = ExchangeFixtures.requestedExchangeClaim();

            // when
            ExchangeClaimJpaEntity entity = mapper.toEntity(original);
            ExchangeClaim converted = mapper.toDomain(entity, null);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.claimNumberValue()).isEqualTo(original.claimNumberValue());
            assertThat(converted.orderItemIdValue()).isEqualTo(original.orderItemIdValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.exchangeQty()).isEqualTo(original.exchangeQty());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            ExchangeClaimJpaEntity original = ExchangeClaimJpaEntityFixtures.requestedEntity();

            // when
            ExchangeClaim domain = mapper.toDomain(original, null);
            ExchangeClaimJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getClaimNumber()).isEqualTo(original.getClaimNumber());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getExchangeStatus()).isEqualTo(original.getExchangeStatus());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
        }
    }
}
