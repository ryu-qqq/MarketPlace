package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AnalysisSource 단위 테스트")
class AnalysisSourceTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("AnalysisSource는 4가지 값을 가진다")
        void analysisSourceValues() {
            AnalysisSource[] values = AnalysisSource.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            AnalysisSource.DESCRIPTION_TEXT,
                            AnalysisSource.IMAGE_MULTIMODAL,
                            AnalysisSource.LLM_INFERENCE,
                            AnalysisSource.RULE_ENGINE);
        }

        @Test
        @DisplayName("각 출처의 description이 올바르다")
        void descriptionIsCorrect() {
            assertThat(AnalysisSource.DESCRIPTION_TEXT.description()).isEqualTo("상세설명텍스트");
            assertThat(AnalysisSource.IMAGE_MULTIMODAL.description()).isEqualTo("이미지분석");
            assertThat(AnalysisSource.LLM_INFERENCE.description()).isEqualTo("LLM추론");
            assertThat(AnalysisSource.RULE_ENGINE.description()).isEqualTo("룰엔진");
        }
    }
}
