package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AnalysisType 단위 테스트")
class AnalysisTypeTest {

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("AnalysisType은 3가지 값을 가진다")
        void analysisTypeValues() {
            AnalysisType[] values = AnalysisType.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            AnalysisType.DESCRIPTION, AnalysisType.OPTION, AnalysisType.NOTICE);
        }

        @Test
        @DisplayName("각 분석 유형의 description이 올바르다")
        void descriptionIsCorrect() {
            assertThat(AnalysisType.DESCRIPTION.description()).isEqualTo("상세설명분석");
            assertThat(AnalysisType.OPTION.description()).isEqualTo("옵션매핑분석");
            assertThat(AnalysisType.NOTICE.description()).isEqualTo("고시정보분석");
        }
    }
}
