package com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SettlementJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("SettlementJpaEntityMapper 단위 테스트")
class SettlementJpaEntityMapperTest {

    private SettlementJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SettlementJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("CALCULATING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCalculatingSettlement_ConvertsCorrectly() {
            // given
            Settlement domain = SettlementFixtures.calculatingSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getSettlementStatus()).isEqualTo(SettlementStatus.CALCULATING.name());
            assertThat(entity.getPeriodStartDate()).isEqualTo(domain.period().startDate());
            assertThat(entity.getPeriodEndDate()).isEqualTo(domain.period().endDate());
            assertThat(entity.getSettlementCycle()).isEqualTo(domain.period().cycle().name());
        }

        @Test
        @DisplayName("금액 필드들이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithAmounts_MapsAmountFieldsCorrectly() {
            // given
            Settlement domain = SettlementFixtures.calculatingSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getTotalSalesAmount())
                    .isEqualTo(domain.amounts().totalSalesAmount().value());
            assertThat(entity.getTotalCommissionAmount())
                    .isEqualTo(domain.amounts().totalCommissionAmount().value());
            assertThat(entity.getTotalReversalAmount())
                    .isEqualTo(domain.amounts().totalReversalAmount().value());
            assertThat(entity.getNetSettlementAmount())
                    .isEqualTo(domain.amounts().netSettlementAmount().value());
        }

        @Test
        @DisplayName("entryCount가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithEntryCount_MapsEntryCountCorrectly() {
            // given
            Settlement domain = SettlementFixtures.calculatingSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getEntryCount()).isEqualTo(domain.entryCount());
        }

        @Test
        @DisplayName("HoldInfo가 있으면 holdReason과 holdAt이 Entity에 매핑됩니다")
        void toEntity_WithHoldInfo_MapsHoldFieldsCorrectly() {
            // given
            Settlement domain = SettlementFixtures.heldSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSettlementStatus()).isEqualTo(SettlementStatus.HOLD.name());
            assertThat(entity.getHoldReason()).isEqualTo(domain.holdInfo().holdReason());
            assertThat(entity.getHoldAt()).isEqualTo(domain.holdInfo().holdAt());
        }

        @Test
        @DisplayName("HoldInfo가 null이면 holdReason과 holdAt이 null로 매핑됩니다")
        void toEntity_WithNullHoldInfo_MapsHoldFieldsAsNull() {
            // given
            Settlement domain = SettlementFixtures.confirmedSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getHoldReason()).isNull();
            assertThat(entity.getHoldAt()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환하면 settlementDay가 매핑됩니다")
        void toEntity_WithCompletedSettlement_MapsSettlementDayCorrectly() {
            // given
            Settlement domain = SettlementFixtures.completedSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSettlementStatus()).isEqualTo(SettlementStatus.COMPLETED.name());
            assertThat(entity.getSettlementDay()).isEqualTo(domain.settlementDay());
        }

        @Test
        @DisplayName("expectedSettlementDay가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithExpectedSettlementDay_MapsCorrectly() {
            // given
            Settlement domain = SettlementFixtures.calculatingSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExpectedSettlementDay()).isEqualTo(domain.expectedSettlementDay());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("CALCULATING Entity를 Domain으로 변환합니다")
        void toDomain_WithCalculatingEntity_ConvertsCorrectly() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.calculatingEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.status()).isEqualTo(SettlementStatus.CALCULATING);
            assertThat(domain.period().startDate()).isEqualTo(entity.getPeriodStartDate());
            assertThat(domain.period().endDate()).isEqualTo(entity.getPeriodEndDate());
        }

        @Test
        @DisplayName("금액 필드들이 Domain으로 올바르게 변환됩니다")
        void toDomain_WithAmountFields_ConvertsToDomainAmounts() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.calculatingEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.amounts().totalSalesAmount().value())
                    .isEqualTo(entity.getTotalSalesAmount());
            assertThat(domain.amounts().totalCommissionAmount().value())
                    .isEqualTo(entity.getTotalCommissionAmount());
            assertThat(domain.amounts().totalReversalAmount().value())
                    .isEqualTo(entity.getTotalReversalAmount());
            assertThat(domain.amounts().netSettlementAmount().value())
                    .isEqualTo(entity.getNetSettlementAmount());
        }

        @Test
        @DisplayName("HOLD Entity를 Domain으로 변환하면 HoldInfo가 복원됩니다")
        void toDomain_WithHoldEntity_RestoresHoldInfo() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.holdEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SettlementStatus.HOLD);
            assertThat(domain.holdInfo()).isNotNull();
            assertThat(domain.holdInfo().holdReason()).isEqualTo(entity.getHoldReason());
            assertThat(domain.holdInfo().holdAt()).isEqualTo(entity.getHoldAt());
        }

        @Test
        @DisplayName("holdReason이 null인 Entity는 HoldInfo가 null로 복원됩니다")
        void toDomain_WithNullHoldReason_RestoresNullHoldInfo() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.confirmedEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.holdInfo()).isNull();
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환하면 settlementDay가 복원됩니다")
        void toDomain_WithCompletedEntity_RestoresSettlementDay() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.completedEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SettlementStatus.COMPLETED);
            assertThat(domain.settlementDay()).isEqualTo(entity.getSettlementDay());
        }

        @Test
        @DisplayName("entryCount가 Domain으로 올바르게 복원됩니다")
        void toDomain_WithEntryCount_RestoresEntryCount() {
            // given
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.calculatingEntity();

            // when
            Settlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.entryCount()).isEqualTo(entity.getEntryCount());
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
            Settlement original = SettlementFixtures.calculatingSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(original);
            Settlement converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.entryCount()).isEqualTo(original.entryCount());
            assertThat(converted.amounts().totalSalesAmount().value())
                    .isEqualTo(original.amounts().totalSalesAmount().value());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            SettlementJpaEntity original = SettlementJpaEntityFixtures.calculatingEntity();

            // when
            Settlement domain = mapper.toDomain(original);
            SettlementJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getSettlementStatus()).isEqualTo(original.getSettlementStatus());
            assertThat(converted.getTotalSalesAmount()).isEqualTo(original.getTotalSalesAmount());
            assertThat(converted.getEntryCount()).isEqualTo(original.getEntryCount());
        }

        @Test
        @DisplayName("HOLD Domain 양방향 변환 시 HoldInfo가 보존됩니다")
        void roundTrip_HeldSettlement_PreservesHoldInfo() {
            // given
            Settlement original = SettlementFixtures.heldSettlement();

            // when
            SettlementJpaEntity entity = mapper.toEntity(original);
            Settlement converted = mapper.toDomain(entity);

            // then
            assertThat(converted.holdInfo()).isNotNull();
            assertThat(converted.holdInfo().holdReason())
                    .isEqualTo(original.holdInfo().holdReason());
        }
    }
}
