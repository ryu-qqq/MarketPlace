package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InspectionDecision 단위 테스트")
class InspectionDecisionTest {

    @Nested
    @DisplayName("생성 검증")
    class CreationTest {

        @Test
        @DisplayName("필수 필드로 InspectionDecision을 생성한다")
        void createWithRequiredFields() {
            Instant now = Instant.now();

            InspectionDecision decision =
                    new InspectionDecision(
                            DecisionType.AUTO_APPROVED,
                            ConfidenceScore.of(0.95),
                            List.of("분석 완료"),
                            now);

            assertThat(decision.decisionType()).isEqualTo(DecisionType.AUTO_APPROVED);
            assertThat(decision.overallConfidence().value()).isEqualTo(0.95);
            assertThat(decision.reasons()).containsExactly("분석 완료");
            assertThat(decision.decidedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("decisionType이 null이면 예외가 발생한다")
        void createWithNullDecisionType_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new InspectionDecision(
                                            null,
                                            ConfidenceScore.of(0.95),
                                            List.of(),
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("decisionType");
        }

        @Test
        @DisplayName("overallConfidence가 null이면 예외가 발생한다")
        void createWithNullConfidence_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new InspectionDecision(
                                            DecisionType.AUTO_APPROVED,
                                            null,
                                            List.of(),
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("overallConfidence");
        }

        @Test
        @DisplayName("reasons가 null이면 빈 리스트로 처리된다")
        void createWithNullReasons_UsesEmptyList() {
            InspectionDecision decision =
                    new InspectionDecision(
                            DecisionType.AUTO_APPROVED,
                            ConfidenceScore.of(0.95),
                            null,
                            Instant.now());

            assertThat(decision.reasons()).isEmpty();
        }
    }

    @Nested
    @DisplayName("팩토리 메서드")
    class FactoryMethodTest {

        @Test
        @DisplayName("autoApprove()는 AUTO_APPROVED 판정을 생성한다")
        void autoApproveCreatesAutoApprovedDecision() {
            Instant now = Instant.now();

            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.96, List.of("조건 충족"), now);

            assertThat(decision.decisionType()).isEqualTo(DecisionType.AUTO_APPROVED);
            assertThat(decision.overallConfidence().value()).isEqualTo(0.96);
        }

        @Test
        @DisplayName("humanReview()는 HUMAN_REVIEW 판정을 생성한다")
        void humanReviewCreatesHumanReviewDecision() {
            Instant now = Instant.now();

            InspectionDecision decision =
                    InspectionDecision.humanReview(0.82, List.of("검수 필요"), now);

            assertThat(decision.decisionType()).isEqualTo(DecisionType.HUMAN_REVIEW);
            assertThat(decision.overallConfidence().value()).isEqualTo(0.82);
        }

        @Test
        @DisplayName("autoReject()는 AUTO_REJECTED 판정을 생성한다")
        void autoRejectCreatesAutoRejectedDecision() {
            Instant now = Instant.now();

            InspectionDecision decision =
                    InspectionDecision.autoReject(0.3, List.of("조건 미충족"), now);

            assertThat(decision.decisionType()).isEqualTo(DecisionType.AUTO_REJECTED);
            assertThat(decision.overallConfidence().value()).isEqualTo(0.3);
        }
    }

    @Nested
    @DisplayName("상태 확인 메서드")
    class StatusCheckTest {

        @Test
        @DisplayName("AUTO_APPROVED 판정은 isApproved가 true이다")
        void autoApprovedIsApproved() {
            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.96, List.of(), Instant.now());

            assertThat(decision.isApproved()).isTrue();
            assertThat(decision.needsReview()).isFalse();
            assertThat(decision.isRejected()).isFalse();
        }

        @Test
        @DisplayName("HUMAN_REVIEW 판정은 needsReview가 true이다")
        void humanReviewNeedsReview() {
            InspectionDecision decision =
                    InspectionDecision.humanReview(0.82, List.of(), Instant.now());

            assertThat(decision.needsReview()).isTrue();
            assertThat(decision.isApproved()).isFalse();
            assertThat(decision.isRejected()).isFalse();
        }

        @Test
        @DisplayName("AUTO_REJECTED 판정은 isRejected가 true이다")
        void autoRejectedIsRejected() {
            InspectionDecision decision =
                    InspectionDecision.autoReject(0.3, List.of(), Instant.now());

            assertThat(decision.isRejected()).isTrue();
            assertThat(decision.isApproved()).isFalse();
            assertThat(decision.needsReview()).isFalse();
        }
    }

    @Nested
    @DisplayName("overallConfidencePercentage() - 백분율 변환")
    class ConfidencePercentageTest {

        @Test
        @DisplayName("신뢰도 0.95는 95%로 변환된다")
        void confidenceToPercentage() {
            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.95, List.of(), Instant.now());

            assertThat(decision.overallConfidencePercentage()).isEqualTo(95);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("reasons는 불변 리스트이다")
        void reasonsIsImmutable() {
            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.95, List.of("이유1"), Instant.now());

            assertThatThrownBy(() -> decision.reasons().add("이유2"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
