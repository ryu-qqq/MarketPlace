package com.ryuqq.marketplace.domain.claimhistory.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistoryType enum 단위 테스트")
class ClaimHistoryTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ClaimHistoryType 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ClaimHistoryType.values())
                    .containsExactly(ClaimHistoryType.STATUS_CHANGE, ClaimHistoryType.MANUAL);
        }

        @Test
        @DisplayName("STATUS_CHANGE의 description은 '상태 변경'이다")
        void statusChangeDescription() {
            // then
            assertThat(ClaimHistoryType.STATUS_CHANGE.description()).isEqualTo("상태 변경");
        }

        @Test
        @DisplayName("MANUAL의 description은 '수기 메모'이다")
        void manualDescription() {
            // then
            assertThat(ClaimHistoryType.MANUAL.description()).isEqualTo("수기 메모");
        }
    }

    @Nested
    @DisplayName("enum 기본 동작 테스트")
    class EnumBehaviorTest {

        @Test
        @DisplayName("name()으로 enum 이름을 반환한다")
        void nameReturnsEnumName() {
            // then
            assertThat(ClaimHistoryType.STATUS_CHANGE.name()).isEqualTo("STATUS_CHANGE");
            assertThat(ClaimHistoryType.MANUAL.name()).isEqualTo("MANUAL");
        }

        @Test
        @DisplayName("valueOf()로 enum 값을 조회한다")
        void valueOfReturnsEnum() {
            // then
            assertThat(ClaimHistoryType.valueOf("STATUS_CHANGE"))
                    .isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(ClaimHistoryType.valueOf("MANUAL")).isEqualTo(ClaimHistoryType.MANUAL);
        }
    }
}
