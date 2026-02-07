package com.ryuqq.marketplace.domain.shippingpolicy.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyErrorCode;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicy Aggregate 테스트")
class ShippingPolicyTest {

    @Nested
    @DisplayName("forNew() - 신규 배송 정책 생성")
    class ForNewTest {

        @Test
        @DisplayName("무료배송 정책을 생성한다")
        void createFreeShippingPolicy() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicyName policyName = ShippingPolicyName.of("무료배송 정책");
            Instant now = CommonVoFixtures.now();

            // when
            ShippingPolicy policy =
                    ShippingPolicy.forNew(
                            sellerId,
                            policyName,
                            true,
                            ShippingFeeType.FREE,
                            Money.zero(),
                            null,
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultReturnFee(),
                            CommonVoFixtures.defaultExchangeFee(),
                            ShippingPolicyFixtures.defaultLeadTime(),
                            now);

            // then
            assertThat(policy.isNew()).isTrue();
            assertThat(policy.sellerId()).isEqualTo(sellerId);
            assertThat(policy.policyName()).isEqualTo(policyName);
            assertThat(policy.shippingFeeType()).isEqualTo(ShippingFeeType.FREE);
            assertThat(policy.isDefaultPolicy()).isTrue();
            assertThat(policy.isActive()).isTrue();
            assertThat(policy.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("유료배송 정책을 생성한다")
        void createPaidShippingPolicy() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newPaidShippingPolicy();

            // then
            assertThat(policy.shippingFeeType()).isEqualTo(ShippingFeeType.PAID);
            assertThat(policy.baseFee()).isEqualTo(CommonVoFixtures.defaultBaseFee());
        }

        @Test
        @DisplayName("조건부 무료배송 정책을 생성한다")
        void createConditionalFreeShippingPolicy() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();

            // then
            assertThat(policy.shippingFeeType()).isEqualTo(ShippingFeeType.CONDITIONAL_FREE);
            assertThat(policy.freeThreshold()).isEqualTo(CommonVoFixtures.defaultFreeThreshold());
        }

        @Test
        @DisplayName("조건부 무료배송에 무료배송 기준금액이 없으면 예외가 발생한다")
        void createConditionalFreeWithoutThresholdThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ShippingPolicy.forNew(
                                            sellerId,
                                            ShippingPolicyFixtures.defaultPolicyName(),
                                            false,
                                            ShippingFeeType.CONDITIONAL_FREE,
                                            CommonVoFixtures.defaultBaseFee(),
                                            null, // 기준금액 없음
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultReturnFee(),
                                            CommonVoFixtures.defaultExchangeFee(),
                                            ShippingPolicyFixtures.defaultLeadTime(),
                                            now))
                    .isInstanceOf(ShippingPolicyException.class)
                    .satisfies(
                            e -> {
                                ShippingPolicyException ex = (ShippingPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(ShippingPolicyErrorCode.INVALID_FREE_THRESHOLD);
                            });
        }

        @Test
        @DisplayName("조건부 무료배송에 무료배송 기준금액이 0원이면 예외가 발생한다")
        void createConditionalFreeWithZeroThresholdThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    ShippingPolicy.forNew(
                                            sellerId,
                                            ShippingPolicyFixtures.defaultPolicyName(),
                                            false,
                                            ShippingFeeType.CONDITIONAL_FREE,
                                            CommonVoFixtures.defaultBaseFee(),
                                            Money.zero(), // 기준금액 0원
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultReturnFee(),
                                            CommonVoFixtures.defaultExchangeFee(),
                                            ShippingPolicyFixtures.defaultLeadTime(),
                                            now))
                    .isInstanceOf(ShippingPolicyException.class);
        }
    }

    @Nested
    @DisplayName("updatePolicy() - 배송 정책 수정")
    class UpdatePolicyTest {

        @Test
        @DisplayName("배송 정책 정보를 수정한다")
        void updatePolicyInfo() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
            ShippingPolicyName newName = ShippingPolicyName.of("수정된 배송 정책");
            Money newBaseFee = Money.of(5000);
            Money newFreeThreshold = Money.of(70000);
            LeadTime newLeadTime = LeadTime.of(2, 5, null);
            Instant now = CommonVoFixtures.now();

            // when
            policy.updatePolicy(
                    newName,
                    ShippingFeeType.CONDITIONAL_FREE,
                    newBaseFee,
                    newFreeThreshold,
                    Money.of(5000),
                    Money.of(5000),
                    Money.of(5000),
                    Money.of(10000),
                    newLeadTime,
                    now);

            // then
            assertThat(policy.policyName()).isEqualTo(newName);
            assertThat(policy.baseFee()).isEqualTo(newBaseFee);
            assertThat(policy.freeThreshold()).isEqualTo(newFreeThreshold);
            assertThat(policy.leadTime()).isEqualTo(newLeadTime);
            assertThat(policy.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수정 시 조건부 무료배송에 기준금액이 없으면 예외가 발생한다")
        void updateWithInvalidConditionalFreeThrowsException() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(
                            () ->
                                    policy.updatePolicy(
                                            ShippingPolicyFixtures.defaultPolicyName(),
                                            ShippingFeeType.CONDITIONAL_FREE,
                                            CommonVoFixtures.defaultBaseFee(),
                                            null, // 기준금액 없음
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultExtraFee(),
                                            CommonVoFixtures.defaultReturnFee(),
                                            CommonVoFixtures.defaultExchangeFee(),
                                            ShippingPolicyFixtures.defaultLeadTime(),
                                            now))
                    .isInstanceOf(ShippingPolicyException.class);
        }
    }

    @Nested
    @DisplayName("markAsDefault() / unmarkDefault() - 기본 정책 설정")
    class DefaultPolicyTest {

        @Test
        @DisplayName("기본 정책으로 설정한다")
        void markAsDefaultPolicy() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.inactiveShippingPolicy();
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
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
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
            ShippingPolicy policy = ShippingPolicyFixtures.inactiveShippingPolicy();
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
            ShippingPolicy policy =
                    ShippingPolicy.reconstitute(
                            ShippingPolicyFixtures.activeShippingPolicy().id(),
                            CommonVoFixtures.defaultSellerId(),
                            ShippingPolicyFixtures.defaultPolicyName(),
                            false, // 기본 정책 아님
                            true, // 활성 상태
                            ShippingFeeType.CONDITIONAL_FREE,
                            CommonVoFixtures.defaultBaseFee(),
                            CommonVoFixtures.defaultFreeThreshold(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultReturnFee(),
                            CommonVoFixtures.defaultExchangeFee(),
                            ShippingPolicyFixtures.defaultLeadTime(),
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
        @DisplayName("기본 정책 비활성화 시 예외가 발생한다")
        void deactivateDefaultPolicyThrowsException() {
            // given: 기본 정책
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> policy.deactivate(now))
                    .isInstanceOf(ShippingPolicyException.class)
                    .satisfies(
                            e -> {
                                ShippingPolicyException ex = (ShippingPolicyException) e;
                                assertThat(ex.getErrorCode())
                                        .isEqualTo(
                                                ShippingPolicyErrorCode
                                                        .CANNOT_DEACTIVATE_DEFAULT_POLICY);
                            });
        }
    }

    @Nested
    @DisplayName("calculateShippingFee() - 배송비 계산")
    class CalculateShippingFeeTest {

        @Test
        @DisplayName("무료배송은 0원을 반환한다")
        void freeShippingReturnsZero() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newFreeShippingPolicy();
            Money orderAmount = Money.of(10000);

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee.isZero()).isTrue();
        }

        @Test
        @DisplayName("유료배송은 기본 배송비를 반환한다")
        void paidShippingReturnsBaseFee() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newPaidShippingPolicy();
            Money orderAmount = Money.of(100000);

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee).isEqualTo(CommonVoFixtures.defaultBaseFee());
        }

        @Test
        @DisplayName("조건부 무료배송에서 기준금액 미만이면 배송비를 반환한다")
        void conditionalFreeBelowThresholdReturnsBaseFee() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            Money orderAmount = Money.of(30000); // 기준금액 50000원 미만

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee).isEqualTo(CommonVoFixtures.defaultBaseFee());
        }

        @Test
        @DisplayName("조건부 무료배송에서 기준금액 이상이면 0원을 반환한다")
        void conditionalFreeAboveThresholdReturnsZero() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            Money orderAmount = Money.of(50000); // 기준금액 이상

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee.isZero()).isTrue();
        }

        @Test
        @DisplayName("조건부 무료배송에서 기준금액을 초과하면 0원을 반환한다")
        void conditionalFreeExceedingThresholdReturnsZero() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            Money orderAmount = Money.of(100000); // 기준금액 초과

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee.isZero()).isTrue();
        }

        @Test
        @DisplayName("수량기반 배송은 기본 배송비를 반환한다")
        void quantityBasedReturnsBaseFee() {
            // given
            ShippingPolicy policy =
                    ShippingPolicy.forNew(
                            CommonVoFixtures.defaultSellerId(),
                            ShippingPolicyFixtures.defaultPolicyName(),
                            false,
                            ShippingFeeType.QUANTITY_BASED,
                            CommonVoFixtures.defaultBaseFee(),
                            null,
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultReturnFee(),
                            CommonVoFixtures.defaultExchangeFee(),
                            ShippingPolicyFixtures.defaultLeadTime(),
                            CommonVoFixtures.now());
            Money orderAmount = Money.of(50000);

            // when
            Money shippingFee = policy.calculateShippingFee(orderAmount);

            // then
            assertThat(shippingFee).isEqualTo(CommonVoFixtures.defaultBaseFee());
        }
    }

    @Nested
    @DisplayName("도서산간 배송비 계산")
    class RegionalShippingFeeTest {

        @Test
        @DisplayName("제주 추가 배송비를 계산한다")
        void calculateJejuShippingFee() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            Money orderAmount = Money.of(100000); // 무료배송 기준 이상

            // when
            Money shippingFee = policy.calculateShippingFeeWithJeju(orderAmount);

            // then
            assertThat(shippingFee).isEqualTo(CommonVoFixtures.defaultExtraFee());
        }

        @Test
        @DisplayName("도서산간 추가 배송비를 계산한다")
        void calculateIslandShippingFee() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newConditionalFreeShippingPolicy();
            Money orderAmount = Money.of(30000); // 무료배송 기준 미만

            // when
            Money shippingFee = policy.calculateShippingFeeWithIsland(orderAmount);

            // then
            // 기본 배송비 3000 + 도서산간 추가비 3000 = 6000
            Money expectedFee =
                    CommonVoFixtures.defaultBaseFee().add(CommonVoFixtures.defaultExtraFee());
            assertThat(shippingFee).isEqualTo(expectedFee);
        }
    }

    @Nested
    @DisplayName("update() - UpdateData를 사용한 정책 수정")
    class UpdateWithDataTest {

        @Test
        @DisplayName("UpdateData로 배송 정책을 수정한다")
        void updateWithShippingPolicyUpdateData() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
            ShippingPolicyUpdateData updateData =
                    new ShippingPolicyUpdateData(
                            ShippingPolicyName.of("수정된 정책"),
                            ShippingFeeType.PAID,
                            Money.of(4000),
                            null,
                            Money.of(4000),
                            Money.of(4000),
                            Money.of(4000),
                            Money.of(8000),
                            LeadTime.of(2, 4, null));
            Instant now = CommonVoFixtures.now();

            // when
            policy.update(updateData, now);

            // then
            assertThat(policy.policyNameValue()).isEqualTo("수정된 정책");
            assertThat(policy.shippingFeeType()).isEqualTo(ShippingFeeType.PAID);
            assertThat(policy.baseFeeValue()).isEqualTo(4000);
            assertThat(policy.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("삭제된 정책을 복원한다")
        void reconstituteDeletedPolicy() {
            // given
            Instant deletedAt = CommonVoFixtures.yesterday();

            // when
            ShippingPolicy policy =
                    ShippingPolicy.reconstitute(
                            ShippingPolicyFixtures.activeShippingPolicy().id(),
                            CommonVoFixtures.defaultSellerId(),
                            ShippingPolicyFixtures.defaultPolicyName(),
                            false,
                            false,
                            ShippingFeeType.PAID,
                            CommonVoFixtures.defaultBaseFee(),
                            null,
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultExtraFee(),
                            CommonVoFixtures.defaultReturnFee(),
                            CommonVoFixtures.defaultExchangeFee(),
                            ShippingPolicyFixtures.defaultLeadTime(),
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday(),
                            deletedAt);

            // then
            assertThat(policy.isDeleted()).isTrue();
            assertThat(policy.deletedAt()).isEqualTo(deletedAt);
            assertThat(policy.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("leadTimeMinDays()는 최소 발송일을 반환한다")
        void leadTimeMinDaysReturnsMinDays() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();

            // when
            int minDays = policy.leadTimeMinDays();

            // then
            assertThat(minDays).isEqualTo(1);
        }

        @Test
        @DisplayName("leadTimeMaxDays()는 최대 발송일을 반환한다")
        void leadTimeMaxDaysReturnsMaxDays() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();

            // when
            int maxDays = policy.leadTimeMaxDays();

            // then
            assertThat(maxDays).isEqualTo(3);
        }

        @Test
        @DisplayName("leadTime이 null이면 기본값을 반환한다")
        void leadTimeNullReturnsDefaultValues() {
            // given
            ShippingPolicy policy =
                    ShippingPolicy.reconstitute(
                            ShippingPolicyFixtures.activeShippingPolicy().id(),
                            CommonVoFixtures.defaultSellerId(),
                            ShippingPolicyFixtures.defaultPolicyName(),
                            false,
                            true,
                            ShippingFeeType.FREE,
                            Money.zero(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday(),
                            null);

            // then
            assertThat(policy.leadTimeMinDays()).isEqualTo(0);
            assertThat(policy.leadTimeMaxDays()).isEqualTo(0);
            assertThat(policy.leadTimeCutoffTime()).isNull();
        }

        @Test
        @DisplayName("baseFee가 null이면 baseFeeValue는 null을 반환한다")
        void baseFeeNullReturnsNull() {
            // given
            ShippingPolicy policy =
                    ShippingPolicy.reconstitute(
                            ShippingPolicyFixtures.activeShippingPolicy().id(),
                            CommonVoFixtures.defaultSellerId(),
                            ShippingPolicyFixtures.defaultPolicyName(),
                            false,
                            true,
                            ShippingFeeType.FREE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday(),
                            null);

            // then
            assertThat(policy.baseFeeValue()).isNull();
            assertThat(policy.freeThresholdValue()).isNull();
            assertThat(policy.jejuExtraFeeValue()).isNull();
            assertThat(policy.islandExtraFeeValue()).isNull();
            assertThat(policy.returnFeeValue()).isNull();
            assertThat(policy.exchangeFeeValue()).isNull();
        }

        @Test
        @DisplayName("idValue는 ID 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();

            // then
            assertThat(policy.idValue()).isNotNull();
            assertThat(policy.sellerIdValue()).isNotNull();
        }
    }
}
