package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefundPolicyJpaEntityTest - 환불 정책 JPA Entity 단위 테스트.
 *
 * <p>create() 정적 팩토리 및 getter가 Fixtures와 일치하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RefundPolicyJpaEntity 단위 테스트")
class RefundPolicyJpaEntityTest {

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateTest {

        @Test
        @DisplayName("create로 생성한 Entity의 getter가 인자와 일치합니다")
        void create_WithAllArgs_ReturnsEntityWithMatchingGetters() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getSellerId())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID);
            assertThat(entity.getPolicyName())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME);
            assertThat(entity.isDefaultPolicy()).isTrue();
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.getReturnPeriodDays())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS);
            assertThat(entity.getExchangePeriodDays())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS);
            assertThat(entity.getNonReturnableConditions())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS);
            assertThat(entity.isPartialRefundEnabled()).isTrue();
            assertThat(entity.isInspectionRequired()).isTrue();
            assertThat(entity.getInspectionPeriodDays())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS);
            assertThat(entity.getAdditionalInfo())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO);
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("id가 null인 새 Entity를 생성합니다")
        void create_WithNullId_ReturnsEntityWithNullId() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            null,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getPolicyName())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME);
        }

        @Test
        @DisplayName("비활성 상태 Entity를 생성합니다")
        void create_WithInactive_ReturnsEntityWithActiveFalse() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            false,
                            false,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            false,
                            0,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.isActive()).isFalse();
            assertThat(entity.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("반품 불가 조건이 null인 Entity를 생성합니다")
        void create_WithoutConditions_ReturnsEntityWithNullConditions() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            null,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            null);

            assertThat(entity.getNonReturnableConditions()).isNull();
        }

        @Test
        @DisplayName("검수 기간이 null인 Entity를 생성합니다")
        void create_WithNullInspectionPeriod_ReturnsEntityWithNullInspectionPeriod() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            false,
                            null,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            null);

            assertThat(entity.getInspectionPeriodDays()).isNull();
            assertThat(entity.isInspectionRequired()).isFalse();
        }

        @Test
        @DisplayName("추가 정보가 null인 Entity를 생성합니다")
        void create_WithoutAdditionalInfo_ReturnsEntityWithNullAdditionalInfo() {
            Instant now = Instant.now();
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.getAdditionalInfo()).isNull();
        }

        @Test
        @DisplayName("커스텀 반품 기간을 가진 Entity를 생성합니다")
        void create_WithCustomReturnPeriod_ReturnsEntityWithReturnPeriod() {
            Instant now = Instant.now();
            int customReturnPeriod = 14;
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            customReturnPeriod,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            null);

            assertThat(entity.getReturnPeriodDays()).isEqualTo(customReturnPeriod);
        }

        @Test
        @DisplayName("삭제 시점이 있으면 deletedAt이 설정됩니다")
        void create_WithDeletedAt_ReturnsEntityWithDeletedAt() {
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(60);
            RefundPolicyJpaEntity entity =
                    RefundPolicyJpaEntity.create(
                            1L,
                            RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID,
                            RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_NON_RETURNABLE_CONDITIONS,
                            true,
                            true,
                            RefundPolicyJpaEntityFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            RefundPolicyJpaEntityFixtures.DEFAULT_ADDITIONAL_INFO,
                            now,
                            now,
                            deletedAt);

            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("Fixtures 일관성 테스트")
    class FixturesConsistencyTest {

        @Test
        @DisplayName("activeEntity Fixture가 create 인자와 일치합니다")
        void activeEntityFixture_MatchesCreateArgs() {
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.activeEntity();

            assertThat(entity.getId()).isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_ID);
            assertThat(entity.getSellerId())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_SELLER_ID);
            assertThat(entity.getPolicyName())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME);
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.isDefaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("newEntity Fixture는 id가 null입니다")
        void newEntityFixture_HasNullId() {
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.newEntity();

            assertThat(entity.getId()).isNull();
            assertThat(entity.getPolicyName())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_POLICY_NAME);
        }

        @Test
        @DisplayName("inactiveEntity Fixture는 active가 false입니다")
        void inactiveEntityFixture_HasActiveFalse() {
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.inactiveEntity();

            assertThat(entity.isActive()).isFalse();
            assertThat(entity.isDefaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("entityWithoutConditions Fixture는 nonReturnableConditions가 null입니다")
        void entityWithoutConditionsFixture_HasNullConditions() {
            RefundPolicyJpaEntity entity = RefundPolicyJpaEntityFixtures.entityWithoutConditions();

            assertThat(entity.getNonReturnableConditions()).isNull();
        }
    }
}
