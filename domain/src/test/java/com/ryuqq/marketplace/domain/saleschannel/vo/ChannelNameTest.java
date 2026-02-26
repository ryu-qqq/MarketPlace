package com.ryuqq.marketplace.domain.saleschannel.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ChannelName Value Object 단위 테스트")
class ChannelNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            ChannelName channelName = ChannelName.of("쿠팡");

            // then
            assertThat(channelName.value()).isEqualTo("쿠팡");
        }

        @Test
        @DisplayName("유효하지 않은 값(null)으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> ChannelName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("판매채널명은 필수입니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithEmptyString_ThrowsException() {
            assertThatThrownBy(() -> ChannelName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("판매채널명은 필수입니다");
        }

        @Test
        @DisplayName("공백 문자열로 생성하면 예외가 발생한다")
        void createWithBlankString_ThrowsException() {
            assertThatThrownBy(() -> ChannelName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("판매채널명은 필수입니다");
        }

        @Test
        @DisplayName("100자를 초과하면 예외가 발생한다")
        void createWithExceedingMaxLength_ThrowsException() {
            // given
            String longName = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> ChannelName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1~100자 이내여야 합니다");
        }

        @Test
        @DisplayName("앞뒤 공백은 자동으로 제거된다")
        void trimWhitespaceAutomatically() {
            // given & when
            ChannelName channelName = ChannelName.of("  쿠팡  ");

            // then
            assertThat(channelName.value()).isEqualTo("쿠팡");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {
        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            ChannelName name1 = ChannelName.of("쿠팡");
            ChannelName name2 = ChannelName.of("쿠팡");
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            ChannelName name1 = ChannelName.of("쿠팡");
            ChannelName name2 = ChannelName.of("네이버");
            assertThat(name1).isNotEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {
        @Test
        @DisplayName("record는 불변이므로 값을 변경할 수 없다")
        void recordIsImmutable() {
            // given
            ChannelName channelName = ChannelName.of("쿠팡");

            // when
            String originalValue = channelName.value();

            // then
            assertThat(channelName.value()).isEqualTo(originalValue);
        }
    }
}
