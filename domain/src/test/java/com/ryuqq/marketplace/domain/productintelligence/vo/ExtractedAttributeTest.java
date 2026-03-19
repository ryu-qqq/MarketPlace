package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExtractedAttribute 단위 테스트")
class ExtractedAttributeTest {

    @Nested
    @DisplayName("생성 검증")
    class CreationTest {

        @Test
        @DisplayName("필수 필드로 ExtractedAttribute를 생성한다")
        void createWithRequiredFields() {
            Instant now = Instant.now();

            ExtractedAttribute attr =
                    new ExtractedAttribute(
                            "소재",
                            "면100%",
                            ConfidenceScore.of(0.9),
                            AnalysisSource.DESCRIPTION_TEXT,
                            "상세설명",
                            now);

            assertThat(attr.key()).isEqualTo("소재");
            assertThat(attr.value()).isEqualTo("면100%");
            assertThat(attr.confidence().value()).isEqualTo(0.9);
            assertThat(attr.source()).isEqualTo(AnalysisSource.DESCRIPTION_TEXT);
            assertThat(attr.sourceDetail()).isEqualTo("상세설명");
            assertThat(attr.extractedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("key가 null이면 예외가 발생한다")
        void createWithNullKey_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new ExtractedAttribute(
                                            null,
                                            "값",
                                            ConfidenceScore.of(0.9),
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("key");
        }

        @Test
        @DisplayName("key가 빈 문자열이면 예외가 발생한다")
        void createWithBlankKey_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new ExtractedAttribute(
                                            "  ",
                                            "값",
                                            ConfidenceScore.of(0.9),
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("key");
        }

        @Test
        @DisplayName("value가 null이면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new ExtractedAttribute(
                                            "소재",
                                            null,
                                            ConfidenceScore.of(0.9),
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("value");
        }

        @Test
        @DisplayName("confidence가 null이면 예외가 발생한다")
        void createWithNullConfidence_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new ExtractedAttribute(
                                            "소재",
                                            "면100%",
                                            null,
                                            AnalysisSource.DESCRIPTION_TEXT,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("confidence");
        }

        @Test
        @DisplayName("source가 null이면 예외가 발생한다")
        void createWithNullSource_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new ExtractedAttribute(
                                            "소재",
                                            "면100%",
                                            ConfidenceScore.of(0.9),
                                            null,
                                            null,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("source");
        }
    }

    @Nested
    @DisplayName("팩토리 메서드 of()")
    class FactoryMethodTest {

        @Test
        @DisplayName("sourceDetail 없이 생성한다")
        void createWithoutSourceDetail() {
            Instant now = Instant.now();

            ExtractedAttribute attr =
                    ExtractedAttribute.of("소재", "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT, now);

            assertThat(attr.key()).isEqualTo("소재");
            assertThat(attr.value()).isEqualTo("면100%");
            assertThat(attr.sourceDetail()).isNull();
        }

        @Test
        @DisplayName("sourceDetail과 함께 생성한다")
        void createWithSourceDetail() {
            Instant now = Instant.now();

            ExtractedAttribute attr =
                    ExtractedAttribute.of(
                            "소재",
                            "면100%",
                            0.9,
                            AnalysisSource.DESCRIPTION_TEXT,
                            "상세설명 1번째 단락",
                            now);

            assertThat(attr.sourceDetail()).isEqualTo("상세설명 1번째 단락");
        }
    }

    @Nested
    @DisplayName("isAutoApplicable() - 자동 적용 가능 여부")
    class IsAutoApplicableTest {

        @Test
        @DisplayName("신뢰도 0.95 이상이면 자동 적용 가능하다")
        void highConfidenceIsAutoApplicable() {
            ExtractedAttribute attr =
                    ExtractedAttribute.of(
                            "소재", "면100%", 0.95, AnalysisSource.DESCRIPTION_TEXT, Instant.now());

            assertThat(attr.isAutoApplicable()).isTrue();
        }

        @Test
        @DisplayName("신뢰도 0.95 미만이면 자동 적용 불가하다")
        void lowConfidenceIsNotAutoApplicable() {
            ExtractedAttribute attr =
                    ExtractedAttribute.of(
                            "소재", "면100%", 0.85, AnalysisSource.DESCRIPTION_TEXT, Instant.now());

            assertThat(attr.isAutoApplicable()).isFalse();
        }
    }

    @Nested
    @DisplayName("confidenceValue() - 신뢰도 값 반환")
    class ConfidenceValueTest {

        @Test
        @DisplayName("신뢰도 값을 double로 반환한다")
        void confidenceValueReturnsDouble() {
            ExtractedAttribute attr =
                    ExtractedAttribute.of(
                            "소재", "면100%", 0.85, AnalysisSource.DESCRIPTION_TEXT, Instant.now());

            assertThat(attr.confidenceValue()).isEqualTo(0.85);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 필드로 생성된 ExtractedAttribute는 같다")
        void sameFieldsAreEqual() {
            Instant now = Instant.ofEpochMilli(1000L);

            ExtractedAttribute attr1 =
                    ExtractedAttribute.of("소재", "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT, now);
            ExtractedAttribute attr2 =
                    ExtractedAttribute.of("소재", "면100%", 0.9, AnalysisSource.DESCRIPTION_TEXT, now);

            assertThat(attr1).isEqualTo(attr2);
            assertThat(attr1.hashCode()).isEqualTo(attr2.hashCode());
        }
    }
}
