package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ShippingPolicyJpaEntityMapperTest - 배송 정책 Mapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ShippingPolicyJpaEntityMapper 단위 테스트")
class ShippingPolicyJpaEntityMapperTest {

    private ShippingPolicyJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShippingPolicyJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 변환 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("조건부 무료배송 Domain을 Entity로 변환합니다")
        void toEntity_WithConditionalFreeDomain_ReturnsValidEntity() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getPolicyName()).isEqualTo(domain.policyNameValue());
            assertThat(entity.isDefaultPolicy()).isEqualTo(domain.isDefaultPolicy());
            assertThat(entity.isActive()).isEqualTo(domain.isActive());
            assertThat(entity.getShippingFeeType()).isEqualTo(domain.shippingFeeType().name());
            assertThat(entity.getBaseFee()).isEqualTo(domain.baseFeeValue());
            assertThat(entity.getFreeThreshold()).isEqualTo(domain.freeThresholdValue());
            assertThat(entity.getJejuExtraFee()).isEqualTo(domain.jejuExtraFeeValue());
            assertThat(entity.getIslandExtraFee()).isEqualTo(domain.islandExtraFeeValue());
            assertThat(entity.getReturnFee()).isEqualTo(domain.returnFeeValue());
            assertThat(entity.getExchangeFee()).isEqualTo(domain.exchangeFeeValue());
            assertThat(entity.getLeadTimeMinDays()).isEqualTo(domain.leadTimeMinDays());
            assertThat(entity.getLeadTimeMaxDays()).isEqualTo(domain.leadTimeMaxDays());
            assertThat(entity.getLeadTimeCutoffTime()).isEqualTo(domain.leadTimeCutoffTime());
        }

        @Test
        @DisplayName("무료배송 Domain을 Entity로 변환합니다")
        void toEntity_WithFreeShippingDomain_ReturnsValidEntity() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.newFreeShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getShippingFeeType()).isEqualTo(ShippingFeeType.FREE.name());
            assertThat(entity.getBaseFee()).isEqualTo(0);
            assertThat(entity.getFreeThreshold()).isNull();
        }

        @Test
        @DisplayName("유료배송 Domain을 Entity로 변환합니다")
        void toEntity_WithPaidShippingDomain_ReturnsValidEntity() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.newPaidShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getShippingFeeType()).isEqualTo(ShippingFeeType.PAID.name());
            assertThat(entity.getBaseFee()).isNotNull();
            assertThat(entity.getFreeThreshold()).isNull();
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveDomain_ReturnsValidEntity() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.inactiveShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedDomain_ReturnsEntityWithDeletedAt() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.deletedShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 변환 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("조건부 무료배송 Entity를 Domain으로 변환합니다")
        void toDomain_WithConditionalFreeEntity_ReturnsValidDomain() {
            // given
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.policyNameValue()).isEqualTo(entity.getPolicyName());
            assertThat(domain.isDefaultPolicy()).isEqualTo(entity.isDefaultPolicy());
            assertThat(domain.isActive()).isEqualTo(entity.isActive());
            assertThat(domain.shippingFeeType())
                    .isEqualTo(ShippingFeeType.valueOf(entity.getShippingFeeType()));
            assertThat(domain.baseFeeValue()).isEqualTo(entity.getBaseFee());
            assertThat(domain.freeThresholdValue()).isEqualTo(entity.getFreeThreshold());
            assertThat(domain.jejuExtraFeeValue()).isEqualTo(entity.getJejuExtraFee());
            assertThat(domain.islandExtraFeeValue()).isEqualTo(entity.getIslandExtraFee());
            assertThat(domain.returnFeeValue()).isEqualTo(entity.getReturnFee());
            assertThat(domain.exchangeFeeValue()).isEqualTo(entity.getExchangeFee());
            assertThat(domain.leadTimeMinDays()).isEqualTo(entity.getLeadTimeMinDays());
            assertThat(domain.leadTimeMaxDays()).isEqualTo(entity.getLeadTimeMaxDays());
            assertThat(domain.leadTimeCutoffTime()).isEqualTo(entity.getLeadTimeCutoffTime());
        }

        @Test
        @DisplayName("무료배송 Entity를 Domain으로 변환합니다")
        void toDomain_WithFreeShippingEntity_ReturnsValidDomain() {
            // given
            ShippingPolicyJpaEntity entity = ShippingPolicyJpaEntityFixtures.freeShippingEntity();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shippingFeeType()).isEqualTo(ShippingFeeType.FREE);
        }

        @Test
        @DisplayName("유료배송 Entity를 Domain으로 변환합니다")
        void toDomain_WithPaidShippingEntity_ReturnsValidDomain() {
            // given
            ShippingPolicyJpaEntity entity = ShippingPolicyJpaEntityFixtures.paidShippingEntity();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shippingFeeType()).isEqualTo(ShippingFeeType.PAID);
            assertThat(domain.freeThresholdValue()).isNull();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ReturnsValidDomain() {
            // given
            ShippingPolicyJpaEntity entity = ShippingPolicyJpaEntityFixtures.inactiveEntity();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
            assertThat(domain.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ReturnsDomainWithDeletedAt() {
            // given
            ShippingPolicyJpaEntity entity = ShippingPolicyJpaEntityFixtures.deletedEntity();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("LeadTime이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutLeadTime_ReturnsZeroLeadTime() {
            // given
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.entityWithoutLeadTime();

            // when
            ShippingPolicy domain = mapper.toDomain(entity);

            // then
            // LeadTime이 null일 때, Domain의 leadTimeMinDays/MaxDays는 기본값 0을 반환
            assertThat(domain.leadTimeMinDays()).isZero();
            assertThat(domain.leadTimeMaxDays()).isZero();
            assertThat(domain.leadTimeCutoffTime()).isNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터가 보존됩니다")
        void bidirectionalConversion_PreservesData() {
            // given
            ShippingPolicy originalDomain = ShippingPolicyFixtures.activeShippingPolicy();

            // when
            ShippingPolicyJpaEntity entity = mapper.toEntity(originalDomain);
            ShippingPolicy convertedDomain = mapper.toDomain(entity);

            // then
            assertThat(convertedDomain.idValue()).isEqualTo(originalDomain.idValue());
            assertThat(convertedDomain.sellerIdValue()).isEqualTo(originalDomain.sellerIdValue());
            assertThat(convertedDomain.policyNameValue())
                    .isEqualTo(originalDomain.policyNameValue());
            assertThat(convertedDomain.isDefaultPolicy())
                    .isEqualTo(originalDomain.isDefaultPolicy());
            assertThat(convertedDomain.isActive()).isEqualTo(originalDomain.isActive());
            assertThat(convertedDomain.shippingFeeType())
                    .isEqualTo(originalDomain.shippingFeeType());
            assertThat(convertedDomain.baseFeeValue()).isEqualTo(originalDomain.baseFeeValue());
            assertThat(convertedDomain.freeThresholdValue())
                    .isEqualTo(originalDomain.freeThresholdValue());
            assertThat(convertedDomain.leadTimeMinDays())
                    .isEqualTo(originalDomain.leadTimeMinDays());
            assertThat(convertedDomain.leadTimeMaxDays())
                    .isEqualTo(originalDomain.leadTimeMaxDays());
        }
    }
}
