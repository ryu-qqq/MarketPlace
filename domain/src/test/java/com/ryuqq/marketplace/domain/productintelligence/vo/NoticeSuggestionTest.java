package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeSuggestion 단위 테스트")
class NoticeSuggestionTest {

    @Nested
    @DisplayName("생성 검증")
    class CreationTest {

        @Test
        @DisplayName("필수 필드로 NoticeSuggestion을 생성한다")
        void createWithRequiredFields() {
            NoticeSuggestion suggestion =
                    new NoticeSuggestion(
                            1L,
                            "소재",
                            null,
                            "면100%",
                            ConfidenceScore.of(0.9),
                            AnalysisSource.DESCRIPTION_TEXT,
                            false);

            assertThat(suggestion.noticeFieldId()).isEqualTo(1L);
            assertThat(suggestion.fieldName()).isEqualTo("소재");
            assertThat(suggestion.currentValue()).isNull();
            assertThat(suggestion.suggestedValue()).isEqualTo("면100%");
            assertThat(suggestion.confidence().value()).isEqualTo(0.9);
            assertThat(suggestion.source()).isEqualTo(AnalysisSource.DESCRIPTION_TEXT);
            assertThat(suggestion.appliedAutomatically()).isFalse();
        }

        @Test
        @DisplayName("noticeFieldId가 null이면 예외가 발생한다")
        void createWithNullNoticeFieldId_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new NoticeSuggestion(
                                            null,
                                            "소재",
                                            null,
                                            "면100%",
                                            ConfidenceScore.of(0.9),
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("noticeFieldId");
        }

        @Test
        @DisplayName("confidence가 null이면 예외가 발생한다")
        void createWithNullConfidence_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new NoticeSuggestion(
                                            1L,
                                            "소재",
                                            null,
                                            "면100%",
                                            null,
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("confidence");
        }

        @Test
        @DisplayName("source가 null이면 예외가 발생한다")
        void createWithNullSource_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new NoticeSuggestion(
                                            1L,
                                            "소재",
                                            null,
                                            "면100%",
                                            ConfidenceScore.of(0.9),
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
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.appliedAutomatically()).isFalse();
        }
    }

    @Nested
    @DisplayName("markAsApplied() - 자동 적용 완료 처리")
    class MarkAsAppliedTest {

        @Test
        @DisplayName("markAsApplied는 appliedAutomatically가 true인 새 인스턴스를 반환한다")
        void markAsAppliedReturnsNewInstanceWithAppliedTrue() {
            NoticeSuggestion original =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.95, AnalysisSource.DESCRIPTION_TEXT);

            NoticeSuggestion applied = original.markAsApplied();

            assertThat(applied.appliedAutomatically()).isTrue();
            assertThat(original.appliedAutomatically()).isFalse();
        }

        @Test
        @DisplayName("markAsApplied는 다른 필드는 유지한다")
        void markAsAppliedPreservesOtherFields() {
            NoticeSuggestion original =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.95, AnalysisSource.DESCRIPTION_TEXT);

            NoticeSuggestion applied = original.markAsApplied();

            assertThat(applied.noticeFieldId()).isEqualTo(1L);
            assertThat(applied.suggestedValue()).isEqualTo("면100%");
            assertThat(applied.confidence()).isEqualTo(original.confidence());
        }
    }

    @Nested
    @DisplayName("isCurrentValueEmpty() - 현재 값 비어있음 확인")
    class IsCurrentValueEmptyTest {

        @Test
        @DisplayName("currentValue가 null이면 비어있다")
        void nullCurrentValueIsEmpty() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.isCurrentValueEmpty()).isTrue();
        }

        @Test
        @DisplayName("currentValue가 빈 문자열이면 비어있다")
        void blankCurrentValueIsEmpty() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", "  ", "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.isCurrentValueEmpty()).isTrue();
        }

        @Test
        @DisplayName("currentValue가 있으면 비어있지 않다")
        void existingCurrentValueIsNotEmpty() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", "폴리에스테르", "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.isCurrentValueEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("isAutoApplicable() - 자동 적용 가능 여부")
    class IsAutoApplicableTest {

        @Test
        @DisplayName("신뢰도 0.95 이상이면 자동 적용 가능하다")
        void highConfidenceIsAutoApplicable() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.95, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.isAutoApplicable()).isTrue();
        }

        @Test
        @DisplayName("신뢰도 0.95 미만이면 자동 적용 불가하다")
        void lowConfidenceIsNotAutoApplicable() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.85, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.isAutoApplicable()).isFalse();
        }
    }

    @Nested
    @DisplayName("confidenceValue() - 신뢰도 값 반환")
    class ConfidenceValueTest {

        @Test
        @DisplayName("신뢰도 값을 double로 반환한다")
        void confidenceValueReturnsDouble() {
            NoticeSuggestion suggestion =
                    NoticeSuggestion.of(
                            1L, "소재", null, "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT);

            assertThat(suggestion.confidenceValue()).isEqualTo(0.9);
        }
    }
}
