package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerSettlementJpaEntityMapperTest - 셀러 정산 정보 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerSettlementJpaEntityMapper 단위 테스트")
class SellerSettlementJpaEntityMapperTest {

    private SellerSettlementJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerSettlementJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveSettlement_ConvertsCorrectly() {
            // given
            SellerSettlement domain = SellerFixtures.activeSellerSettlement();

            // when
            SellerSettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getBankCode()).isEqualTo(domain.bankCode());
            assertThat(entity.getBankName()).isEqualTo(domain.bankName());
            assertThat(entity.getAccountNumber()).isEqualTo(domain.accountNumber());
            assertThat(entity.getAccountHolderName()).isEqualTo(domain.accountHolderName());
            assertThat(entity.getSettlementDay()).isEqualTo(domain.settlementDay());
            assertThat(entity.isVerified()).isEqualTo(domain.isVerified());
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewSettlement_ConvertsCorrectly() {
            // given
            SellerSettlement domain = SellerFixtures.newSellerSettlement();

            // when
            SellerSettlementJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getBankCode()).isEqualTo(domain.bankCode());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("검증된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithVerifiedEntity_ConvertsCorrectly() {
            // given
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.verifiedEntity();

            // when
            SellerSettlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.bankCode()).isEqualTo(entity.getBankCode());
            assertThat(domain.bankName()).isEqualTo(entity.getBankName());
            assertThat(domain.accountNumber()).isEqualTo(entity.getAccountNumber());
            assertThat(domain.accountHolderName()).isEqualTo(entity.getAccountHolderName());
            assertThat(domain.settlementDay()).isEqualTo(entity.getSettlementDay());
            assertThat(domain.isVerified()).isTrue();
            assertThat(domain.verifiedAt()).isNotNull();
        }

        @Test
        @DisplayName("미검증 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithUnverifiedEntity_ConvertsCorrectly() {
            // given
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.unverifiedEntity();

            // when
            SellerSettlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isVerified()).isFalse();
            assertThat(domain.verifiedAt()).isNull();
        }

        @Test
        @DisplayName("주간 정산 주기 Entity를 Domain으로 변환합니다")
        void toDomain_WithWeeklyEntity_ConvertsCorrectly() {
            // given
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.weeklyEntity();

            // when
            SellerSettlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.settlementCycle().name()).isEqualTo("WEEKLY");
        }

        @Test
        @DisplayName("격주 정산 주기 Entity를 Domain으로 변환합니다")
        void toDomain_WithBiweeklyEntity_ConvertsCorrectly() {
            // given
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.biweeklyEntity();

            // when
            SellerSettlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.settlementCycle().name()).isEqualTo("BIWEEKLY");
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerSettlementJpaEntity entity = SellerSettlementJpaEntityFixtures.newEntity();

            // when
            SellerSettlement domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.bankCode()).isEqualTo(entity.getBankCode());
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
            SellerSettlement original = SellerFixtures.activeSellerSettlement();

            // when
            SellerSettlementJpaEntity entity = mapper.toEntity(original);
            SellerSettlement converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.bankCode()).isEqualTo(original.bankCode());
            assertThat(converted.bankName()).isEqualTo(original.bankName());
            assertThat(converted.accountNumber()).isEqualTo(original.accountNumber());
            assertThat(converted.accountHolderName()).isEqualTo(original.accountHolderName());
            assertThat(converted.settlementCycle()).isEqualTo(original.settlementCycle());
            assertThat(converted.settlementDay()).isEqualTo(original.settlementDay());
            assertThat(converted.isVerified()).isEqualTo(original.isVerified());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerSettlementJpaEntity original = SellerSettlementJpaEntityFixtures.verifiedEntity();

            // when
            SellerSettlement domain = mapper.toDomain(original);
            SellerSettlementJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getBankCode()).isEqualTo(original.getBankCode());
            assertThat(converted.getBankName()).isEqualTo(original.getBankName());
            assertThat(converted.getAccountNumber()).isEqualTo(original.getAccountNumber());
            assertThat(converted.getAccountHolderName()).isEqualTo(original.getAccountHolderName());
            assertThat(converted.getSettlementCycle()).isEqualTo(original.getSettlementCycle());
            assertThat(converted.getSettlementDay()).isEqualTo(original.getSettlementDay());
            assertThat(converted.isVerified()).isEqualTo(original.isVerified());
        }
    }
}
