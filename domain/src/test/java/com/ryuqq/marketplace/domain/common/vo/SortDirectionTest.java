package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SortDirection Value Object 단위 테스트")
class SortDirectionTest {

    @Nested
    @DisplayName("기본값 테스트")
    class DefaultValueTest {

        @Test
        @DisplayName("기본 정렬 방향은 DESC이다")
        void defaultDirectionIsDesc() {
            assertThat(SortDirection.defaultDirection()).isEqualTo(SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("isAscending / isDescending 테스트")
    class DirectionCheckTest {

        @Test
        @DisplayName("ASC는 오름차순이다")
        void ascIsAscending() {
            assertThat(SortDirection.ASC.isAscending()).isTrue();
            assertThat(SortDirection.ASC.isDescending()).isFalse();
        }

        @Test
        @DisplayName("DESC는 내림차순이다")
        void descIsDescending() {
            assertThat(SortDirection.DESC.isDescending()).isTrue();
            assertThat(SortDirection.DESC.isAscending()).isFalse();
        }
    }

    @Nested
    @DisplayName("reverse() 테스트")
    class ReverseTest {

        @Test
        @DisplayName("ASC를 reverse하면 DESC이다")
        void reverseAscToDesc() {
            assertThat(SortDirection.ASC.reverse()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("DESC를 reverse하면 ASC이다")
        void reverseDescToAsc() {
            assertThat(SortDirection.DESC.reverse()).isEqualTo(SortDirection.ASC);
        }
    }

    @Nested
    @DisplayName("displayName() 테스트")
    class DisplayNameTest {

        @Test
        @DisplayName("ASC의 표시 이름은 오름차순이다")
        void ascDisplayName() {
            assertThat(SortDirection.ASC.displayName()).isEqualTo("오름차순");
        }

        @Test
        @DisplayName("DESC의 표시 이름은 내림차순이다")
        void descDisplayName() {
            assertThat(SortDirection.DESC.displayName()).isEqualTo("내림차순");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("asc 문자열을 ASC로 변환한다")
        void fromAscString() {
            assertThat(SortDirection.fromString("asc")).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("DESC 문자열을 DESC로 변환한다")
        void fromDescString() {
            assertThat(SortDirection.fromString("DESC")).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("null이면 기본값 DESC를 반환한다")
        void nullReturnsDefaultDirection() {
            assertThat(SortDirection.fromString(null)).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("빈 문자열이면 기본값 DESC를 반환한다")
        void blankReturnsDefaultDirection() {
            assertThat(SortDirection.fromString("  ")).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("알 수 없는 값이면 기본값 DESC를 반환한다")
        void unknownValueReturnsDefaultDirection() {
            assertThat(SortDirection.fromString("UNKNOWN")).isEqualTo(SortDirection.DESC);
        }
    }
}
