package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OptionType Enum 단위 테스트")
class OptionTypeTest {

    @Nested
    @DisplayName("requiresOptionGroup() 테스트")
    class RequiresOptionGroupTest {

        @Test
        @DisplayName("SINGLE은 옵션 그룹이 필요하다")
        void singleRequiresOptionGroup() {
            assertThat(OptionType.SINGLE.requiresOptionGroup()).isTrue();
        }

        @Test
        @DisplayName("COMBINATION은 옵션 그룹이 필요하다")
        void combinationRequiresOptionGroup() {
            assertThat(OptionType.COMBINATION.requiresOptionGroup()).isTrue();
        }

        @Test
        @DisplayName("NONE은 옵션 그룹이 필요하지 않다")
        void noneDoesNotRequireOptionGroup() {
            assertThat(OptionType.NONE.requiresOptionGroup()).isFalse();
        }

        @Test
        @DisplayName("FREE_INPUT은 옵션 그룹이 필요하지 않다")
        void freeInputDoesNotRequireOptionGroup() {
            assertThat(OptionType.FREE_INPUT.requiresOptionGroup()).isFalse();
        }
    }

    @Nested
    @DisplayName("expectedOptionGroupCount() 테스트")
    class ExpectedOptionGroupCountTest {

        @Test
        @DisplayName("NONE은 0개의 옵션 그룹을 요구한다")
        void noneExpects0OptionGroups() {
            assertThat(OptionType.NONE.expectedOptionGroupCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("FREE_INPUT은 0개의 옵션 그룹을 요구한다")
        void freeInputExpects0OptionGroups() {
            assertThat(OptionType.FREE_INPUT.expectedOptionGroupCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("SINGLE은 1개의 옵션 그룹을 요구한다")
        void singleExpects1OptionGroup() {
            assertThat(OptionType.SINGLE.expectedOptionGroupCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("COMBINATION은 2개의 옵션 그룹을 요구한다")
        void combinationExpects2OptionGroups() {
            assertThat(OptionType.COMBINATION.expectedOptionGroupCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("displayName() 테스트")
    class DisplayNameTest {

        @Test
        @DisplayName("모든 옵션 타입은 표시 이름을 가진다")
        void allTypesHaveDisplayName() {
            assertThat(OptionType.NONE.displayName()).isEqualTo("옵션 없음");
            assertThat(OptionType.SINGLE.displayName()).isEqualTo("단일 옵션");
            assertThat(OptionType.COMBINATION.displayName()).isEqualTo("조합 옵션");
            assertThat(OptionType.FREE_INPUT.displayName()).isEqualTo("자유 입력");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 옵션 타입이 존재한다")
        void allValuesExist() {
            assertThat(OptionType.values())
                    .containsExactly(
                            OptionType.NONE,
                            OptionType.SINGLE,
                            OptionType.COMBINATION,
                            OptionType.FREE_INPUT);
        }
    }
}
