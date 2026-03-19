package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DecisionType 단위 테스트")
class DecisionTypeTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("DecisionType은 3가지 값을 가진다")
        void decisionTypeValues() {
            DecisionType[] values = DecisionType.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            DecisionType.AUTO_APPROVED,
                            DecisionType.HUMAN_REVIEW,
                            DecisionType.AUTO_REJECTED);
        }

        @Test
        @DisplayName("각 타입의 description이 올바르다")
        void descriptionIsCorrect() {
            assertThat(DecisionType.AUTO_APPROVED.description()).isEqualTo("자동승인");
            assertThat(DecisionType.HUMAN_REVIEW.description()).isEqualTo("검수자확인");
            assertThat(DecisionType.AUTO_REJECTED.description()).isEqualTo("자동반려");
        }
    }

    @Nested
    @DisplayName("isApproved() - 승인 여부 확인")
    class IsApprovedTest {

        @Test
        @DisplayName("AUTO_APPROVED만 isApproved가 true이다")
        void onlyAutoApprovedIsApproved() {
            assertThat(DecisionType.AUTO_APPROVED.isApproved()).isTrue();
            assertThat(DecisionType.HUMAN_REVIEW.isApproved()).isFalse();
            assertThat(DecisionType.AUTO_REJECTED.isApproved()).isFalse();
        }
    }

    @Nested
    @DisplayName("needsReview() - 검수 필요 여부 확인")
    class NeedsReviewTest {

        @Test
        @DisplayName("HUMAN_REVIEW만 needsReview가 true이다")
        void onlyHumanReviewNeedsReview() {
            assertThat(DecisionType.HUMAN_REVIEW.needsReview()).isTrue();
            assertThat(DecisionType.AUTO_APPROVED.needsReview()).isFalse();
            assertThat(DecisionType.AUTO_REJECTED.needsReview()).isFalse();
        }
    }

    @Nested
    @DisplayName("isRejected() - 반려 여부 확인")
    class IsRejectedTest {

        @Test
        @DisplayName("AUTO_REJECTED만 isRejected가 true이다")
        void onlyAutoRejectedIsRejected() {
            assertThat(DecisionType.AUTO_REJECTED.isRejected()).isTrue();
            assertThat(DecisionType.AUTO_APPROVED.isRejected()).isFalse();
            assertThat(DecisionType.HUMAN_REVIEW.isRejected()).isFalse();
        }
    }
}
