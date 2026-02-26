package com.ryuqq.marketplace.domain.imagevariant.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ResultAssetId VO 테스트")
class ResultAssetIdTest {

    @Nested
    @DisplayName("생성 및 검증")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            ResultAssetId id = ResultAssetId.of("asset-abc-123");
            assertThat(id.value()).isEqualTo("asset-abc-123");
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한다")
        void trimWhitespace() {
            ResultAssetId id = ResultAssetId.of("  asset-123  ");
            assertThat(id.value()).isEqualTo("asset-123");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> ResultAssetId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> ResultAssetId.of("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("100자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longValue = "a".repeat(101);
            assertThatThrownBy(() -> ResultAssetId.of(longValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("100자는 허용된다")
        void createWith100Chars() {
            String value = "a".repeat(100);
            ResultAssetId id = ResultAssetId.of(value);
            assertThat(id.value()).hasSize(100);
        }
    }
}
