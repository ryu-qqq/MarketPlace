package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerContractJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerContractJpaEntityMapperTest - 셀러 계약 정보 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerContractJpaEntityMapper 단위 테스트")
class SellerContractJpaEntityMapperTest {

    private SellerContractJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerContractJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveContract_ConvertsCorrectly() {
            // given
            SellerContract domain = SellerFixtures.activeSellerContract();

            // when
            SellerContractJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getCommissionRate().doubleValue())
                    .isEqualTo(domain.commissionRateValue());
            assertThat(entity.getContractStartDate()).isEqualTo(domain.contractStartDate());
            assertThat(entity.getContractEndDate()).isEqualTo(domain.contractEndDate());
            assertThat(entity.getStatus())
                    .isEqualTo(SellerContractJpaEntity.ContractStatusJpaValue.ACTIVE);
            assertThat(entity.getSpecialTerms()).isEqualTo(domain.specialTerms());
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewContract_ConvertsCorrectly() {
            // given
            SellerContract domain = SellerFixtures.newSellerContract();

            // when
            SellerContractJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCommissionRate().doubleValue())
                    .isEqualTo(domain.commissionRateValue());
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
            SellerContractJpaEntity entity = SellerContractJpaEntityFixtures.activeEntity();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.commissionRateValue())
                    .isEqualTo(entity.getCommissionRate().doubleValue());
            assertThat(domain.contractStartDate()).isEqualTo(entity.getContractStartDate());
            assertThat(domain.contractEndDate()).isEqualTo(entity.getContractEndDate());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus().name());
            assertThat(domain.specialTerms()).isEqualTo(entity.getSpecialTerms());
        }

        @Test
        @DisplayName("만료된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithExpiredEntity_ConvertsCorrectly() {
            // given
            SellerContractJpaEntity entity = SellerContractJpaEntityFixtures.expiredEntity();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("EXPIRED");
        }

        @Test
        @DisplayName("종료된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithTerminatedEntity_ConvertsCorrectly() {
            // given
            SellerContractJpaEntity entity = SellerContractJpaEntityFixtures.terminatedEntity();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status().name()).isEqualTo("TERMINATED");
        }

        @Test
        @DisplayName("특별 약관이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutSpecialTerms_ConvertsCorrectly() {
            // given
            SellerContractJpaEntity entity =
                    SellerContractJpaEntityFixtures.entityWithoutSpecialTerms();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.specialTerms()).isNull();
        }

        @Test
        @DisplayName("계약 종료일이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutEndDate_ConvertsCorrectly() {
            // given
            SellerContractJpaEntity entity = SellerContractJpaEntityFixtures.entityWithoutEndDate();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.contractEndDate()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerContractJpaEntity entity = SellerContractJpaEntityFixtures.newEntity();

            // when
            SellerContract domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.commissionRateValue())
                    .isEqualTo(entity.getCommissionRate().doubleValue());
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
            SellerContract original = SellerFixtures.activeSellerContract();

            // when
            SellerContractJpaEntity entity = mapper.toEntity(original);
            SellerContract converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.commissionRateValue()).isEqualTo(original.commissionRateValue());
            assertThat(converted.contractStartDate()).isEqualTo(original.contractStartDate());
            assertThat(converted.contractEndDate()).isEqualTo(original.contractEndDate());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.specialTerms()).isEqualTo(original.specialTerms());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerContractJpaEntity original = SellerContractJpaEntityFixtures.activeEntity();

            // when
            SellerContract domain = mapper.toDomain(original);
            SellerContractJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getCommissionRate())
                    .isEqualByComparingTo(original.getCommissionRate());
            assertThat(converted.getContractStartDate()).isEqualTo(original.getContractStartDate());
            assertThat(converted.getContractEndDate()).isEqualTo(original.getContractEndDate());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getSpecialTerms()).isEqualTo(original.getSpecialTerms());
        }
    }
}
