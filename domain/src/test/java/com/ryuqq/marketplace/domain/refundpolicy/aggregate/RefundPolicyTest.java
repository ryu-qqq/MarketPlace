package com.ryuqq.marketplace.domain.refundpolicy.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyErrorCode;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyException;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicy Aggregate 테스트")
class RefundPolicyTest {

    @Nested
    @DisplayName("forNew() - 신규 환불 정책 생성")
    class ForNewTest {

        @Test
        @DisplayName("유효한 기간으로 신규 환불 정책을 생성한다")
        void createNewRefundPolicyWithValidPeriod() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicyName policyName = RefundPolicyFixtures.defaultPolicyName();
            int returnPeriodDays = 7;
            int exchangePeriodDays = 14;
            Instant now = CommonVoFixtures.now();

            // when
            RefundPolicy policy =
                    RefundPolicy.forNew(
                            sellerId,
                            policyName,
                            true,
                            returnPeriodDays,
                            exchangePeriodDays,
                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                            true,
                            true,
                            3,
                            "추가 정보",
                            now);

            // then
            assertThat(policy.isNew()).isTrue();
            assertThat(policy.sellerId()).isEqualTo(sellerId);
            assertThat(policy.policyName()).isEqualTo(policyName);
            assertThat(policy.isDefaultPolicy()).isTrue();
            assertThat(policy.isActive()).isTrue();
            assertThat(policy.returnPeriodDays()).isEqualTo(returnPeriodDays);
            assertThat(policy.exchangePeriodDays()).isEqualTo(exchangePeriodDays);
            assertThat(policy.isPartialRefundEnabled()).isTrue();
            assertThat(policy.isInspectionRequired()).isTrue();
            assertThat(policy.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("반품 기간이 0일이면 예외가 발생한다")
        void createWithZeroReturnPeriodThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundPolicy.forNew(
                                            sellerId,
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            true,
                                            0, // 유효하지 않은 반품 기간
                                            14,
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(RefundPolicyErrorCode.INVALID_RETURN_PERIOD);
                            });
        }

        @Test
        @DisplayName("반품 기간이 90일을 초과하면 예외가 발생한다")
        void createWithTooLongReturnPeriodThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundPolicy.forNew(
                                            sellerId,
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            true,
                                            91, // 유효하지 않은 반품 기간
                                            14,
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(RefundPolicyErrorCode.INVALID_RETURN_PERIOD);
                            });
        }

        @Test
        @DisplayName("교환 기간이 유효하지 않으면 예외가 발생한다")
        void createWithInvalidExchangePeriodThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundPolicy.forNew(
                                            sellerId,
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            true,
                                            7,
                                            0, // 유효하지 않은 교환 기간
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(RefundPolicyErrorCode.INVALID_EXCHANGE_PERIOD);
                            });
        }

        @Test
        @DisplayName("교환 기간이 90일을 초과하면 예외가 발생한다")
        void createWithTooLongExchangePeriodThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundPolicy.forNew(
                                            sellerId,
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            true,
                                            7,
                                            91, // 유효하지 않은 교환 기간
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(RefundPolicyErrorCode.INVALID_EXCHANGE_PERIOD);
                            });
        }

        @Test
        @DisplayName("null nonReturnableConditions로 신규 정책을 생성하면 빈 리스트가 된다")
        void createWithNullNonReturnableConditions() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when
            RefundPolicy policy =
                    RefundPolicy.forNew(
                            sellerId,
                            RefundPolicyFixtures.defaultPolicyName(),
                            true,
                            7,
                            14,
                            null, // null 조건
                            true,
                            true,
                            3,
                            "추가 정보",
                            now);

            // then
            assertThat(policy.nonReturnableConditions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("updatePolicy() - 환불 정책 수정")
    class UpdatePolicyTest {

        @Test
        @DisplayName("환불 정책 정보를 수정한다")
        void updatePolicyInfo() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            RefundPolicyName newName = RefundPolicyName.of("수정된 정책명");
            int newReturnPeriod = 10;
            int newExchangePeriod = 20;
            List<NonReturnableCondition> newConditions =
                    List.of(NonReturnableCondition.OPENED_PACKAGING);
            Instant now = CommonVoFixtures.now();

            // when
            policy.updatePolicy(
                    newName,
                    newReturnPeriod,
                    newExchangePeriod,
                    newConditions,
                    false,
                    false,
                    0,
                    "수정된 추가 정보",
                    now);

            // then
            assertThat(policy.policyName()).isEqualTo(newName);
            assertThat(policy.returnPeriodDays()).isEqualTo(newReturnPeriod);
            assertThat(policy.exchangePeriodDays()).isEqualTo(newExchangePeriod);
            assertThat(policy.nonReturnableConditions())
                    .containsExactly(NonReturnableCondition.OPENED_PACKAGING);
            assertThat(policy.isPartialRefundEnabled()).isFalse();
            assertThat(policy.isInspectionRequired()).isFalse();
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수정 시 유효하지 않은 반품 기간이면 예외가 발생한다")
        void updateWithInvalidReturnPeriodThrowsException() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    policy.updatePolicy(
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            0, // 유효하지 않은 기간
                                            14,
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class);
        }

        @Test
        @DisplayName("수정 시 교환 기간이 90일을 초과하면 예외가 발생한다")
        void updateWithTooLongExchangePeriodThrowsException() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    policy.updatePolicy(
                                            RefundPolicyFixtures.defaultPolicyName(),
                                            7,
                                            91, // 유효하지 않은 교환 기간
                                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                                            true,
                                            true,
                                            3,
                                            null,
                                            now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(RefundPolicyErrorCode.INVALID_EXCHANGE_PERIOD);
                            });
        }

        @Test
        @DisplayName("UpdateData를 사용하여 환불 정책을 수정한다")
        void updateWithUpdateData() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            RefundPolicyName newName = RefundPolicyName.of("업데이트된 정책명");
            List<NonReturnableCondition> newConditions =
                    List.of(NonReturnableCondition.DIGITAL_CONTENT);
            Instant now = CommonVoFixtures.now();

            RefundPolicyUpdateData updateData =
                    RefundPolicyUpdateData.of(
                            newName, 10, 20, newConditions, false, false, 0, "업데이트된 추가 정보");

            // when
            policy.update(updateData, now);

            // then
            assertThat(policy.policyName()).isEqualTo(newName);
            assertThat(policy.returnPeriodDays()).isEqualTo(10);
            assertThat(policy.exchangePeriodDays()).isEqualTo(20);
            assertThat(policy.nonReturnableConditions())
                    .containsExactly(NonReturnableCondition.DIGITAL_CONTENT);
            assertThat(policy.isPartialRefundEnabled()).isFalse();
            assertThat(policy.isInspectionRequired()).isFalse();
            assertThat(policy.inspectionPeriodDays()).isEqualTo(0);
            assertThat(policy.additionalInfo()).isEqualTo("업데이트된 추가 정보");
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("null nonReturnableConditions로 수정하면 빈 리스트가 된다")
        void updateWithNullNonReturnableConditions() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when
            policy.updatePolicy(
                    RefundPolicyFixtures.defaultPolicyName(),
                    7,
                    14,
                    null, // null 조건
                    true,
                    true,
                    3,
                    "추가 정보",
                    now);

            // then
            assertThat(policy.nonReturnableConditions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("markAsDefault() / unmarkDefault() - 기본 정책 설정")
    class DefaultPolicyTest {

        @Test
        @DisplayName("기본 정책으로 설정한다")
        void markAsDefaultPolicy() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.inactiveRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when
            policy.markAsDefault(now);

            // then
            assertThat(policy.isDefaultPolicy()).isTrue();
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 정책 설정을 해제한다")
        void unmarkDefaultPolicy() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when
            policy.unmarkDefault(now);

            // then
            assertThat(policy.isDefaultPolicy()).isFalse();
            assertThat(policy.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("정책을 활성화한다")
        void activatePolicy() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.inactiveRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when
            policy.activate(now);

            // then
            assertThat(policy.isActive()).isTrue();
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 정책이 아닌 정책을 비활성화한다")
        void deactivateNonDefaultPolicy() {
            // given: 기본 정책이 아닌 활성 정책
            RefundPolicy policy =
                    RefundPolicy.reconstitute(
                            RefundPolicyFixtures.activeRefundPolicy().id(),
                            CommonVoFixtures.defaultSellerId(),
                            RefundPolicyFixtures.defaultPolicyName(),
                            false, // 기본 정책 아님
                            true, // 활성 상태
                            RefundPolicyFixtures.DEFAULT_RETURN_PERIOD_DAYS,
                            RefundPolicyFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS,
                            RefundPolicyFixtures.defaultNonReturnableConditions(),
                            true,
                            true,
                            RefundPolicyFixtures.DEFAULT_INSPECTION_PERIOD_DAYS,
                            "추가 정보",
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday(),
                            null);
            Instant now = CommonVoFixtures.now();

            // when
            policy.deactivate(now);

            // then
            assertThat(policy.isActive()).isFalse();
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 정책을 비활성화하면 예외가 발생한다")
        void deactivateDefaultPolicyThrowsException() {
            // given: 기본 정책인 활성 정책
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> policy.deactivate(now))
                    .isInstanceOf(RefundPolicyException.class)
                    .satisfies(
                            e -> {
                                RefundPolicyException ex = (RefundPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(
                                                RefundPolicyErrorCode
                                                        .CANNOT_DEACTIVATE_DEFAULT_POLICY);
                            });
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 테스트")
    class BusinessLogicTest {

        @Test
        @DisplayName("반품 가능 기간 내인지 확인한다")
        void isReturnableWithinPeriod() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.newRefundPolicy(7, 14);

            // when & then
            assertThat(policy.isReturnableWithinPeriod(5)).isTrue();
            assertThat(policy.isReturnableWithinPeriod(7)).isTrue();
            assertThat(policy.isReturnableWithinPeriod(8)).isFalse();
        }

        @Test
        @DisplayName("교환 가능 기간 내인지 확인한다")
        void isExchangeableWithinPeriod() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.newRefundPolicy(7, 14);

            // when & then
            assertThat(policy.isExchangeableWithinPeriod(10)).isTrue();
            assertThat(policy.isExchangeableWithinPeriod(14)).isTrue();
            assertThat(policy.isExchangeableWithinPeriod(15)).isFalse();
        }

        @Test
        @DisplayName("반품 불가 조건을 포함하는지 확인한다")
        void hasNonReturnableCondition() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.newRefundPolicy();

            // when & then
            assertThat(policy.hasNonReturnableCondition(NonReturnableCondition.OPENED_PACKAGING))
                    .isTrue();
            assertThat(policy.hasNonReturnableCondition(NonReturnableCondition.USED_PRODUCT))
                    .isTrue();
            assertThat(policy.hasNonReturnableCondition(NonReturnableCondition.DIGITAL_CONTENT))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 정책 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("삭제된 정책을 복원한다")
        void reconstituteDeletedPolicy() {
            // given
            RefundPolicy deletedPolicy = RefundPolicyFixtures.deletedRefundPolicy();

            // then
            assertThat(deletedPolicy.isDeleted()).isTrue();
            assertThat(deletedPolicy.deletedAt()).isNotNull();
            assertThat(deletedPolicy.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제되지 않은 정책을 복원한다")
        void reconstituteNonDeletedPolicy() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // then
            assertThat(policy.isDeleted()).isFalse();
            assertThat(policy.deletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("nonReturnableConditions()는 불변 리스트를 반환한다")
        void nonReturnableConditionsReturnsUnmodifiableList() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // when
            List<NonReturnableCondition> conditions = policy.nonReturnableConditions();

            // then
            assertThatThrownBy(() -> conditions.add(NonReturnableCondition.DIGITAL_CONTENT))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("sellerIdValue()는 셀러 ID 값을 반환한다")
        void sellerIdValueReturnsLongValue() {
            // given
            SellerId sellerId = SellerId.of(100L);
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy(1L, sellerId);

            // when
            Long sellerIdValue = policy.sellerIdValue();

            // then
            assertThat(sellerIdValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("idValue()는 정책 ID 값을 반환한다")
        void idValueReturnsLongValue() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy(99L, SellerId.of(1L));

            // when
            Long idValue = policy.idValue();

            // then
            assertThat(idValue).isEqualTo(99L);
        }

        @Test
        @DisplayName("policyNameValue()는 정책명 문자열을 반환한다")
        void policyNameValueReturnsString() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // when
            String policyNameValue = policy.policyNameValue();

            // then
            assertThat(policyNameValue).isEqualTo("기본 환불 정책");
        }

        @Test
        @DisplayName("inspectionPeriodDays()는 검수 기간을 반환한다")
        void inspectionPeriodDaysReturnsValue() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // then
            assertThat(policy.inspectionPeriodDays())
                    .isEqualTo(RefundPolicyFixtures.DEFAULT_INSPECTION_PERIOD_DAYS);
        }

        @Test
        @DisplayName("additionalInfo()는 추가 정보를 반환한다")
        void additionalInfoReturnsValue() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // then
            assertThat(policy.additionalInfo()).isEqualTo("추가 정보");
        }

        @Test
        @DisplayName("createdAt()과 updatedAt()은 시간 정보를 반환한다")
        void timestampsReturnValues() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            // then
            assertThat(policy.createdAt()).isNotNull();
            assertThat(policy.updatedAt()).isNotNull();
        }
    }
}
