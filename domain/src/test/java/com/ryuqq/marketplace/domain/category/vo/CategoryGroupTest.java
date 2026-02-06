package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** CategoryGroup VO 단위 테스트. */
@DisplayName("CategoryGroup")
class CategoryGroupTest {

    @Nested
    @DisplayName("fromString")
    class FromString {

        @ParameterizedTest
        @CsvSource({
            "CLOTHING, CLOTHING",
            "clothing, CLOTHING",
            "Clothing, CLOTHING",
            "SHOES, SHOES",
            "shoes, SHOES",
            "BAGS, BAGS",
            "ACCESSORIES, ACCESSORIES",
            "COSMETICS, COSMETICS",
            "JEWELRY, JEWELRY",
            "WATCHES, WATCHES",
            "FURNITURE, FURNITURE",
            "DIGITAL, DIGITAL",
            "SPORTS, SPORTS",
            "BABY_KIDS, BABY_KIDS",
            "baby_kids, BABY_KIDS",
            "ETC, ETC"
        })
        @DisplayName("유효한 문자열을 CategoryGroup으로 변환한다")
        void convertsValidString(String input, CategoryGroup expected) {
            assertThat(CategoryGroup.fromString(input)).isEqualTo(expected);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "INVALID", "unknown", "OTHER"})
        @DisplayName("유효하지 않은 문자열은 ETC로 변환한다")
        void returnsEtcForInvalidString(String input) {
            assertThat(CategoryGroup.fromString(input)).isEqualTo(CategoryGroup.ETC);
        }
    }

    @Nested
    @DisplayName("requiresNoticeInfo")
    class RequiresNoticeInfo {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "CLOTHING",
                    "SHOES",
                    "BAGS",
                    "ACCESSORIES",
                    "COSMETICS",
                    "JEWELRY",
                    "WATCHES",
                    "FURNITURE",
                    "DIGITAL",
                    "SPORTS",
                    "BABY_KIDS"
                })
        @DisplayName("ETC가 아닌 카테고리 그룹은 고시정보가 필요하다")
        void requiresNoticeInfoForNonEtc(String groupName) {
            CategoryGroup group = CategoryGroup.valueOf(groupName);
            assertThat(group.requiresNoticeInfo()).isTrue();
        }

        @Test
        @DisplayName("ETC는 고시정보가 필요하지 않다")
        void etcDoesNotRequireNoticeInfo() {
            assertThat(CategoryGroup.ETC.requiresNoticeInfo()).isFalse();
        }
    }

    @Test
    @DisplayName("모든 CategoryGroup 값이 12개 존재한다")
    void hasExpectedValues() {
        assertThat(CategoryGroup.values()).hasSize(12);
    }
}
