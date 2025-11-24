package com.ryuqq.marketplace.domain.externalmall;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * MallCode Enum 테스트
 *
 * <p>Red Phase: 실패하는 테스트 작성
 * - MallCode Enum이 아직 존재하지 않음
 * - 컴파일 에러 발생 예상
 *
 * <p>검증 항목:
 * - 4개 외부몰 코드 (OCO, SELLIC, LF, BUYMA) 존재
 * - fromCode() 정적 메서드로 String → Enum 변환
 * - 유효하지 않은 코드는 IllegalArgumentException 발생
 */
@DisplayName("MallCode Enum 테스트")
class MallCodeTest {

    @Nested
    @DisplayName("fromCode 메서드")
    class FromCodeMethod {

        @ParameterizedTest
        @ValueSource(strings = {"OCO", "SELLIC", "LF", "BUYMA"})
        @DisplayName("유효한 코드로 MallCode Enum 반환")
        void shouldReturnMallCodeWhenValidCode(String code) {
            // When
            MallCode result = MallCode.fromCode(code);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("대소문자 구분 없이 변환 (oco → OCO)")
        void shouldBeCaseInsensitive() {
            // When
            MallCode result = MallCode.fromCode("oco");

            // Then
            assertThat(result).isEqualTo(MallCode.OCO);
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "UNKNOWN", "GMARKET"})
        @DisplayName("유효하지 않은 코드는 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenInvalidCode(String invalidCode) {
            // When & Then
            assertThatThrownBy(() -> MallCode.fromCode(invalidCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 외부몰 코드");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열은 IllegalArgumentException 발생")
        void shouldThrowExceptionWhenNullOrEmpty(String nullOrEmpty) {
            // When & Then
            assertThatThrownBy(() -> MallCode.fromCode(nullOrEmpty))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Enum 값 검증")
    class EnumValueValidation {

        @Test
        @DisplayName("OCO 코드 검증")
        void shouldHaveOcoCode() {
            // When
            MallCode oco = MallCode.OCO;

            // Then
            assertThat(oco.getCode()).isEqualTo("OCO");
        }

        @Test
        @DisplayName("SELLIC 코드 검증")
        void shouldHaveSellicCode() {
            // When
            MallCode sellic = MallCode.SELLIC;

            // Then
            assertThat(sellic.getCode()).isEqualTo("SELLIC");
        }

        @Test
        @DisplayName("LF 코드 검증")
        void shouldHaveLfCode() {
            // When
            MallCode lf = MallCode.LF;

            // Then
            assertThat(lf.getCode()).isEqualTo("LF");
        }

        @Test
        @DisplayName("BUYMA 코드 검증")
        void shouldHaveBuymaCode() {
            // When
            MallCode buyma = MallCode.BUYMA;

            // Then
            assertThat(buyma.getCode()).isEqualTo("BUYMA");
        }

        @Test
        @DisplayName("전체 4개 외부몰 코드 존재")
        void shouldHaveFourMallCodes() {
            // When
            MallCode[] values = MallCode.values();

            // Then
            assertThat(values).hasSize(4);
            assertThat(values).containsExactlyInAnyOrder(
                    MallCode.OCO,
                    MallCode.SELLIC,
                    MallCode.LF,
                    MallCode.BUYMA
            );
        }
    }
}
