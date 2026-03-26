package com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementEntryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementEntryJpaEntity;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SettlementEntryJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("SettlementEntryJpaEntityMapper 단위 테스트")
class SettlementEntryJpaEntityMapperTest {

    private SettlementEntryJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SettlementEntryJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("SALES 타입 Domain을 Entity로 변환합니다")
        void toEntity_WithSalesEntry_ConvertsCorrectly() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getEntryType()).isEqualTo(EntryType.SALES.name());
            assertThat(entity.getEntryStatus()).isEqualTo(EntryStatus.PENDING.name());
        }

        @Test
        @DisplayName("금액 필드들이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithAmounts_MapsAmountFieldsCorrectly() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSalesAmount()).isEqualTo(domain.amounts().salesAmount().value());
            assertThat(entity.getCommissionRate()).isEqualTo(domain.amounts().commissionRate());
            assertThat(entity.getCommissionAmount())
                    .isEqualTo(domain.amounts().commissionAmount().value());
            assertThat(entity.getSettlementAmount())
                    .isEqualTo(domain.amounts().settlementAmount().value());
        }

        @Test
        @DisplayName("orderItemId가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithOrderItemId_MapsOrderItemIdCorrectly() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOrderItemId()).isEqualTo(domain.source().orderItemId());
        }

        @Test
        @DisplayName("CANCEL 역분개 Domain의 claimId와 claimType이 Entity에 매핑됩니다")
        void toEntity_WithCancelReversal_MapsClaimFieldsCorrectly() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.cancelReversalEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getEntryType()).isEqualTo(EntryType.CANCEL.name());
            assertThat(entity.getClaimId()).isEqualTo(domain.source().claimId());
            assertThat(entity.getClaimType()).isEqualTo(domain.source().claimType());
        }

        @Test
        @DisplayName("reversalOfEntryId가 null이면 Entity의 reversalOfEntryId도 null입니다")
        void toEntity_WithNullReversalOfEntryId_MapsAsNull() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReversalOfEntryId()).isNull();
        }

        @Test
        @DisplayName("settlementId가 null이면 Entity의 settlementId도 null입니다")
        void toEntity_WithNullSettlementId_MapsAsNull() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSettlementId()).isNull();
        }

        @Test
        @DisplayName("eligibleAt이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithEligibleAt_MapsEligibleAtCorrectly() {
            // given
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getEligibleAt()).isEqualTo(domain.eligibleAt());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("SALES PENDING Entity를 Domain으로 변환합니다")
        void toDomain_WithSalesPendingEntity_ConvertsCorrectly() {
            // given
            SettlementEntryJpaEntity entity = SettlementEntryJpaEntityFixtures.salesPendingEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.entryType()).isEqualTo(EntryType.SALES);
            assertThat(domain.status()).isEqualTo(EntryStatus.PENDING);
        }

        @Test
        @DisplayName("금액 필드들이 Domain으로 올바르게 변환됩니다")
        void toDomain_WithAmountFields_ConvertsToDomainAmounts() {
            // given
            SettlementEntryJpaEntity entity = SettlementEntryJpaEntityFixtures.salesPendingEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.amounts().salesAmount().value()).isEqualTo(entity.getSalesAmount());
            assertThat(domain.amounts().commissionRate()).isEqualTo(entity.getCommissionRate());
            assertThat(domain.amounts().commissionAmount().value())
                    .isEqualTo(entity.getCommissionAmount());
            assertThat(domain.amounts().settlementAmount().value())
                    .isEqualTo(entity.getSettlementAmount());
        }

        @Test
        @DisplayName("CANCEL Entity를 Domain으로 변환하면 claimId가 source에 복원됩니다")
        void toDomain_WithCancelEntity_RestoresClaimInSource() {
            // given
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.cancelReversalEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.entryType()).isEqualTo(EntryType.CANCEL);
            assertThat(domain.source().claimId()).isEqualTo(entity.getClaimId());
            assertThat(domain.source().claimType()).isEqualTo(entity.getClaimType());
        }

        @Test
        @DisplayName("reversalOfEntryId가 null인 Entity는 Domain의 reversalOfEntryId도 null입니다")
        void toDomain_WithNullReversalOfEntryId_RestoresNullReversalId() {
            // given
            SettlementEntryJpaEntity entity = SettlementEntryJpaEntityFixtures.salesPendingEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.reversalOfEntryId()).isNull();
        }

        @Test
        @DisplayName("settlementId가 있는 Entity는 Domain의 settlementId가 복원됩니다")
        void toDomain_WithSettlementId_RestoresSettlementId() {
            // given
            SettlementEntryJpaEntity entity = SettlementEntryJpaEntityFixtures.salesSettledEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.settlementId()).isNotNull();
            assertThat(domain.settlementId().value()).isEqualTo(entity.getSettlementId());
        }

        @Test
        @DisplayName("settlementId가 null인 Entity는 Domain의 settlementId도 null입니다")
        void toDomain_WithNullSettlementId_RestoresNullSettlementId() {
            // given
            SettlementEntryJpaEntity entity = SettlementEntryJpaEntityFixtures.salesPendingEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.settlementId()).isNull();
        }

        @Test
        @DisplayName("CONFIRMED Entity를 Domain으로 변환하면 CONFIRMED 상태가 복원됩니다")
        void toDomain_WithConfirmedEntity_RestoresConfirmedStatus() {
            // given
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesConfirmedEntity();

            // when
            SettlementEntry domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(EntryStatus.CONFIRMED);
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
            SettlementEntry original = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryJpaEntity entity = mapper.toEntity(original);
            SettlementEntry converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.entryType()).isEqualTo(original.entryType());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.amounts().salesAmount().value())
                    .isEqualTo(original.amounts().salesAmount().value());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            SettlementEntryJpaEntity original =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity();

            // when
            SettlementEntry domain = mapper.toDomain(original);
            SettlementEntryJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getEntryType()).isEqualTo(original.getEntryType());
            assertThat(converted.getEntryStatus()).isEqualTo(original.getEntryStatus());
            assertThat(converted.getSalesAmount()).isEqualTo(original.getSalesAmount());
        }
    }
}
