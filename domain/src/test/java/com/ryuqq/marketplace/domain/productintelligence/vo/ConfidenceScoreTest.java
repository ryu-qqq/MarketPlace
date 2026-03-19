package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ConfidenceScore 단위 테스트")
class ConfidenceScoreTest {

    @Nested
    @DisplayName("of() - 생성")
    class OfTest {

        @Test
        @DisplayName("0.0 이상 1.0 이하의 값으로 생성한다")
        void createWithValidValue() {
            ConfidenceScore score = ConfidenceScore.of(0.85);

            assertThat(score.value()).isEqualTo(0.85);
        }

        @Test
        @DisplayName("0.0으로 생성한다")
        void createWithZero() {
            ConfidenceScore score = ConfidenceScore.of(0.0);

            assertThat(score.value()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("1.0으로 생성한다")
        void createWithOne() {
            ConfidenceScore score = ConfidenceScore.of(1.0);

            assertThat(score.value()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("0.0 미만 값이면 예외가 발생한다")
        void createWithNegativeValue_ThrowsException() {
            assertThatThrownBy(() -> ConfidenceScore.of(-0.1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0.0~1.0");
        }

        @Test
        @DisplayName("1.0 초과 값이면 예외가 발생한다")
        void createWithValueOverOne_ThrowsException() {
            assertThatThrownBy(() -> ConfidenceScore.of(1.1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0.0~1.0");
        }
    }

    @Nested
    @DisplayName("팩토리 메서드")
    class FactoryMethodTest {

        @Test
        @DisplayName("perfect()는 1.0 점수를 반환한다")
        void perfectReturnsOne() {
            ConfidenceScore score = ConfidenceScore.perfect();

            assertThat(score.value()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("zero()는 0.0 점수를 반환한다")
        void zeroReturnsZero() {
            ConfidenceScore score = ConfidenceScore.zero();

            assertThat(score.value()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("isAutoApplicable() - 자동 적용 가능 여부")
    class IsAutoApplicableTest {

        @Test
        @DisplayName("0.95 이상이면 자동 적용 가능하다")
        void aboveThresholdIsAutoApplicable() {
            assertThat(ConfidenceScore.of(0.95).isAutoApplicable()).isTrue();
            assertThat(ConfidenceScore.of(1.0).isAutoApplicable()).isTrue();
        }

        @Test
        @DisplayName("0.95 미만이면 자동 적용 불가하다")
        void belowThresholdIsNotAutoApplicable() {
            assertThat(ConfidenceScore.of(0.94).isAutoApplicable()).isFalse();
            assertThat(ConfidenceScore.of(0.0).isAutoApplicable()).isFalse();
        }
    }

    @Nested
    @DisplayName("needsReview() - 검수 필요 여부")
    class NeedsReviewTest {

        @Test
        @DisplayName("0.80 이상 0.95 미만이면 검수가 필요하다")
        void betweenThresholdsNeedsReview() {
            assertThat(ConfidenceScore.of(0.80).needsReview()).isTrue();
            assertThat(ConfidenceScore.of(0.90).needsReview()).isTrue();
            assertThat(ConfidenceScore.of(0.94).needsReview()).isTrue();
        }

        @Test
        @DisplayName("0.95 이상이면 검수 필요 없다")
        void aboveAutoThresholdDoesNotNeedReview() {
            assertThat(ConfidenceScore.of(0.95).needsReview()).isFalse();
        }

        @Test
        @DisplayName("0.80 미만이면 검수 필요 없다 (수동 확인 대상)")
        void belowReviewThresholdDoesNotNeedReview() {
            assertThat(ConfidenceScore.of(0.79).needsReview()).isFalse();
        }
    }

    @Nested
    @DisplayName("needsManualConfirmation() - 수동 확인 필요 여부")
    class NeedsManualConfirmationTest {

        @Test
        @DisplayName("0.80 미만이면 수동 확인이 필요하다")
        void belowReviewThresholdNeedsManualConfirmation() {
            assertThat(ConfidenceScore.of(0.79).needsManualConfirmation()).isTrue();
            assertThat(ConfidenceScore.of(0.0).needsManualConfirmation()).isTrue();
        }

        @Test
        @DisplayName("0.80 이상이면 수동 확인 불필요하다")
        void aboveReviewThresholdDoesNotNeedManualConfirmation() {
            assertThat(ConfidenceScore.of(0.80).needsManualConfirmation()).isFalse();
        }
    }

    @Nested
    @DisplayName("toPercentage() - 정수 백분율 변환")
    class ToPercentageTest {

        @Test
        @DisplayName("0.95는 95로 변환된다")
        void ninetyfivePercent() {
            assertThat(ConfidenceScore.of(0.95).toPercentage()).isEqualTo(95);
        }

        @Test
        @DisplayName("1.0은 100으로 변환된다")
        void hundredPercent() {
            assertThat(ConfidenceScore.perfect().toPercentage()).isEqualTo(100);
        }

        @Test
        @DisplayName("0.0은 0으로 변환된다")
        void zeroPercent() {
            assertThat(ConfidenceScore.zero().toPercentage()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 ConfidenceScore는 같다")
        void sameValuesAreEqual() {
            ConfidenceScore score1 = ConfidenceScore.of(0.85);
            ConfidenceScore score2 = ConfidenceScore.of(0.85);

            assertThat(score1).isEqualTo(score2);
            assertThat(score1.hashCode()).isEqualTo(score2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 ConfidenceScore는 같지 않다")
        void differentValuesAreNotEqual() {
            ConfidenceScore score1 = ConfidenceScore.of(0.85);
            ConfidenceScore score2 = ConfidenceScore.of(0.90);

            assertThat(score1).isNotEqualTo(score2);
        }
    }
}
