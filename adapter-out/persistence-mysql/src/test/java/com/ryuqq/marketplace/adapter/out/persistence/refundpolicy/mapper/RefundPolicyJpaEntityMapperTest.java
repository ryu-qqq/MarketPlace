package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefundPolicyJpaEntityMapperTest - 환불 정책 Mapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RefundPolicyJpaEntityMapper 단위 테스트")
class RefundPolicyJpaEntityMapperTest {

    private RefundPolicyJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundPolicyJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 변환 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveDomain_ReturnsValidEntity() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.activeRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getPolicyName()).isEqualTo(domain.policyNameValue());
            assertThat(entity.isDefaultPolicy()).isEqualTo(domain.isDefaultPolicy());
            assertThat(entity.isActive()).isEqualTo(domain.isActive());
            assertThat(entity.getReturnPeriodDays()).isEqualTo(domain.returnPeriodDays());
            assertThat(entity.getExchangePeriodDays()).isEqualTo(domain.exchangePeriodDays());
            assertThat(entity.isPartialRefundEnabled()).isEqualTo(domain.isPartialRefundEnabled());
            assertThat(entity.isInspectionRequired()).isEqualTo(domain.isInspectionRequired());
            assertThat(entity.getInspectionPeriodDays()).isEqualTo(domain.inspectionPeriodDays());
            assertThat(entity.getAdditionalInfo()).isEqualTo(domain.additionalInfo());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("신규 Domain을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ReturnsValidEntity() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.newRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getPolicyName()).isEqualTo(domain.policyNameValue());
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveDomain_ReturnsValidEntity() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.inactiveRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedDomain_ReturnsEntityWithDeletedAt() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.deletedRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("반품 불가 조건을 쉼표로 구분된 문자열로 변환합니다")
        void toEntity_WithNonReturnableConditions_ReturnsCommaSeparatedString() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.activeRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(domain);

            // then
            String conditions = entity.getNonReturnableConditions();
            assertThat(conditions).isNotNull();
            assertThat(conditions).contains("OPENED_PACKAGING");
            assertThat(conditions).contains("USED_PRODUCT");
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 변환 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ReturnsValidDomain() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.activeEntity();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.policyNameValue()).isEqualTo(entity.getPolicyName());
            assertThat(domain.isDefaultPolicy()).isEqualTo(entity.isDefaultPolicy());
            assertThat(domain.isActive()).isEqualTo(entity.isActive());
            assertThat(domain.returnPeriodDays()).isEqualTo(entity.getReturnPeriodDays());
            assertThat(domain.exchangePeriodDays()).isEqualTo(entity.getExchangePeriodDays());
            assertThat(domain.isPartialRefundEnabled()).isEqualTo(entity.isPartialRefundEnabled());
            assertThat(domain.isInspectionRequired()).isEqualTo(entity.isInspectionRequired());
            assertThat(domain.inspectionPeriodDays()).isEqualTo(entity.getInspectionPeriodDays());
            assertThat(domain.additionalInfo()).isEqualTo(entity.getAdditionalInfo());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ReturnsValidDomain() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.inactiveEntity();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
            assertThat(domain.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ReturnsDomainWithDeletedAt() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.deletedEntity();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("쉼표로 구분된 문자열을 반품 불가 조건 List로 변환합니다")
        void toDomain_WithConditionsString_ReturnsConditionsList() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.activeEntity();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            List<NonReturnableCondition> conditions = domain.nonReturnableConditions();
            assertThat(conditions).isNotEmpty();
            assertThat(conditions).contains(NonReturnableCondition.OPENED_PACKAGING);
            assertThat(conditions).contains(NonReturnableCondition.USED_PRODUCT);
        }

        @Test
        @DisplayName("반품 불가 조건이 null인 경우 빈 List를 반환합니다")
        void toDomain_WithNullConditions_ReturnsEmptyList() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.entityWithoutConditions();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nonReturnableConditions()).isEmpty();
        }

        @Test
        @DisplayName("반품 불가 조건이 빈 문자열인 경우 빈 List를 반환합니다")
        void toDomain_WithEmptyConditionsString_ReturnsEmptyList() {
            // given
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntityFixtures.entityWithEmptyConditions();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nonReturnableConditions()).isEmpty();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.newEntity();

            // when
            RefundPolicy domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.policyNameValue()).isEqualTo(entity.getPolicyName());
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
            RefundPolicy originalDomain = RefundPolicyFixtures.activeRefundPolicy();

            // when
            RefundPolicyJpaEntity entity = mapper.toEntity(originalDomain);
            RefundPolicy convertedDomain = mapper.toDomain(entity);

            // then
            assertThat(convertedDomain.idValue()).isEqualTo(originalDomain.idValue());
            assertThat(convertedDomain.sellerIdValue()).isEqualTo(originalDomain.sellerIdValue());
            assertThat(convertedDomain.policyNameValue())
                    .isEqualTo(originalDomain.policyNameValue());
            assertThat(convertedDomain.isDefaultPolicy())
                    .isEqualTo(originalDomain.isDefaultPolicy());
            assertThat(convertedDomain.isActive()).isEqualTo(originalDomain.isActive());
            assertThat(convertedDomain.returnPeriodDays())
                    .isEqualTo(originalDomain.returnPeriodDays());
            assertThat(convertedDomain.exchangePeriodDays())
                    .isEqualTo(originalDomain.exchangePeriodDays());
            assertThat(convertedDomain.nonReturnableConditions())
                    .containsExactlyInAnyOrderElementsOf(originalDomain.nonReturnableConditions());
        }
    }
}
