package com.ryuqq.marketplace.adapter.out.client.sellic.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("unit")
@DisplayName("SellicCourierCodeResolver 단위 테스트")
class SellicCourierCodeResolverTest {

    @Nested
    @DisplayName("resolve()")
    class ResolveTest {

        @ParameterizedTest
        @CsvSource({
            "CJ, 1000",
            "CJLOGISTICS, 1000",
            "CJ대한통운, 1000",
            "HANJIN, 1001",
            "한진, 1001",
            "EPOST, 1002",
            "우체국, 1002",
            "LOTTE, 1003",
            "LOGEN, 1004",
            "KYUNGDONG, 1011",
            "DHL, 1028",
            "EMS, 1029",
            "FEDEX, 1030",
            "PANTOS, 1535",
            "LOTTEGLOBAL, 1600"
        })
        @DisplayName("알려진 택배사 코드를 올바르게 변환한다")
        void knownCourierCodes(String input, int expected) {
            assertThat(SellicCourierCodeResolver.resolve(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("소문자 입력도 대문자로 변환하여 매핑한다")
        void caseInsensitive() {
            assertThat(SellicCourierCodeResolver.resolve("hanjin")).isEqualTo(1001);
            assertThat(SellicCourierCodeResolver.resolve("fedex")).isEqualTo(1030);
        }

        @Test
        @DisplayName("null 입력은 기본값 CJ(1000)를 반환한다")
        void nullReturnsDefault() {
            assertThat(SellicCourierCodeResolver.resolve(null)).isEqualTo(1000);
        }

        @Test
        @DisplayName("빈 문자열은 기본값 CJ(1000)를 반환한다")
        void blankReturnsDefault() {
            assertThat(SellicCourierCodeResolver.resolve("")).isEqualTo(1000);
            assertThat(SellicCourierCodeResolver.resolve("   ")).isEqualTo(1000);
        }

        @Test
        @DisplayName("알 수 없는 택배사 코드는 기본값 CJ(1000)를 반환한다")
        void unknownReturnsDefault() {
            assertThat(SellicCourierCodeResolver.resolve("UNKNOWN_COURIER")).isEqualTo(1000);
        }
    }
}
