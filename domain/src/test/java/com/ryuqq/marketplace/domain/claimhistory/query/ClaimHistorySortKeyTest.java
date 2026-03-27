package com.ryuqq.marketplace.domain.claimhistory.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistorySortKey 단위 테스트")
class ClaimHistorySortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(ClaimHistorySortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtFieldName() {
            // then
            assertThat(ClaimHistorySortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            // then
            assertThat(ClaimHistorySortKey.defaultKey()).isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("'CREATED_AT' 문자열로 CREATED_AT을 반환한다")
        void fromStringCreatedAt() {
            // then
            assertThat(ClaimHistorySortKey.fromString("CREATED_AT"))
                    .isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }

        @Test
        @DisplayName("null을 전달하면 기본 키 CREATED_AT을 반환한다")
        void fromStringNullReturnsDefault() {
            // then
            assertThat(ClaimHistorySortKey.fromString(null))
                    .isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }

        @Test
        @DisplayName("빈 문자열을 전달하면 기본 키 CREATED_AT을 반환한다")
        void fromStringBlankReturnsDefault() {
            // then
            assertThat(ClaimHistorySortKey.fromString(""))
                    .isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }

        @Test
        @DisplayName("공백만 있는 문자열을 전달하면 기본 키 CREATED_AT을 반환한다")
        void fromStringWhitespaceReturnsDefault() {
            // then
            assertThat(ClaimHistorySortKey.fromString("   "))
                    .isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }

        @Test
        @DisplayName("유효하지 않은 값을 전달하면 예외가 발생한다")
        void fromStringInvalidValueThrowsException() {
            // when & then
            assertThatThrownBy(() -> ClaimHistorySortKey.fromString("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ClaimHistorySortKey.values())
                    .containsExactly(ClaimHistorySortKey.CREATED_AT);
        }
    }
}
