package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OptionMappingSuggestion 단위 테스트")
class OptionMappingSuggestionTest {

    @Nested
    @DisplayName("생성 검증")
    class CreationTest {

        @Test
        @DisplayName("필수 필드로 OptionMappingSuggestion을 생성한다")
        void createWithRequiredFields() {
            OptionMappingSuggestion suggestion =
                    new OptionMappingSuggestion(
                            10L,
                            100L,
                            "색상",
                            1L,
                            10L,
                            "블랙",
                            ConfidenceScore.of(0.85),
                            AnalysisSource.RULE_ENGINE,
                            false);

            assertThat(suggestion.sellerOptionGroupId()).isEqualTo(10L);
            assertThat(suggestion.sellerOptionValueId()).isEqualTo(100L);
            assertThat(suggestion.sellerOptionName()).isEqualTo("색상");
            assertThat(suggestion.suggestedCanonicalGroupId()).isEqualTo(1L);
            assertThat(suggestion.suggestedCanonicalValueId()).isEqualTo(10L);
            assertThat(suggestion.suggestedCanonicalValueName()).isEqualTo("블랙");
            assertThat(suggestion.confidence().value()).isEqualTo(0.85);
            assertThat(suggestion.source()).isEqualTo(AnalysisSource.RULE_ENGINE);
            assertThat(suggestion.appliedAutomatically()).isFalse();
        }

        @Test
        @DisplayName("confidence가 null이면 예외가 발생한다")
        void createWithNullConfidence_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new OptionMappingSuggestion(
                                            10L,
                                            100L,
                                            "색상",
                                            1L,
                                            10L,
                                            "블랙",
                                            null,
                                            AnalysisSource.RULE_ENGINE,
                                            false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("confidence");
        }

        @Test
        @DisplayName("source가 null이면 예외가 발생한다")
        void createWithNullSource_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new OptionMappingSuggestion(
                                            10L,
                                            100L,
                                            "색상",
                                            1L,
                                            10L,
                                            "블랙",
                                            ConfidenceScore.of(0.85),
                                            null,
                                            false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("source");
        }
    }

    @Nested
    @DisplayName("of() - 팩토리 메서드")
    class FactoryMethodTest {

        @Test
        @DisplayName("of()로 생성하면 appliedAutomatically는 false이다")
        void ofCreatesWithAppliedAutomaticallyFalse() {
            OptionMappingSuggestion suggestion =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.85, AnalysisSource.RULE_ENGINE);

            assertThat(suggestion.appliedAutomatically()).isFalse();
        }
    }

    @Nested
    @DisplayName("markAsApplied() - 자동 적용 완료 처리")
    class MarkAsAppliedTest {

        @Test
        @DisplayName("markAsApplied는 appliedAutomatically가 true인 새 인스턴스를 반환한다")
        void markAsAppliedReturnsNewInstanceWithAppliedTrue() {
            OptionMappingSuggestion original =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.95, AnalysisSource.RULE_ENGINE);

            OptionMappingSuggestion applied = original.markAsApplied();

            assertThat(applied.appliedAutomatically()).isTrue();
            assertThat(original.appliedAutomatically()).isFalse();
        }

        @Test
        @DisplayName("markAsApplied는 다른 필드는 유지한다")
        void markAsAppliedPreservesOtherFields() {
            OptionMappingSuggestion original =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.95, AnalysisSource.RULE_ENGINE);

            OptionMappingSuggestion applied = original.markAsApplied();

            assertThat(applied.sellerOptionGroupId()).isEqualTo(10L);
            assertThat(applied.suggestedCanonicalValueName()).isEqualTo("블랙");
            assertThat(applied.confidence()).isEqualTo(original.confidence());
        }
    }

    @Nested
    @DisplayName("isAutoApplicable() - 자동 적용 가능 여부")
    class IsAutoApplicableTest {

        @Test
        @DisplayName("신뢰도 0.95 이상이면 자동 적용 가능하다")
        void highConfidenceIsAutoApplicable() {
            OptionMappingSuggestion suggestion =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.95, AnalysisSource.RULE_ENGINE);

            assertThat(suggestion.isAutoApplicable()).isTrue();
        }

        @Test
        @DisplayName("신뢰도 0.95 미만이면 자동 적용 불가하다")
        void lowConfidenceIsNotAutoApplicable() {
            OptionMappingSuggestion suggestion =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.85, AnalysisSource.RULE_ENGINE);

            assertThat(suggestion.isAutoApplicable()).isFalse();
        }
    }

    @Nested
    @DisplayName("confidenceValue() - 신뢰도 값 반환")
    class ConfidenceValueTest {

        @Test
        @DisplayName("신뢰도 값을 double로 반환한다")
        void confidenceValueReturnsDouble() {
            OptionMappingSuggestion suggestion =
                    OptionMappingSuggestion.of(
                            10L, 100L, "색상", 1L, 10L, "블랙", 0.85, AnalysisSource.RULE_ENGINE);

            assertThat(suggestion.confidenceValue()).isEqualTo(0.85);
        }
    }
}
