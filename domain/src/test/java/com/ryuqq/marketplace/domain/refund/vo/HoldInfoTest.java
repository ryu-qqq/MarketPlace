package com.ryuqq.marketplace.domain.refund.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("HoldInfo Value Object 단위 테스트")
class HoldInfoTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 보류 정보를 생성한다")
        void createWithValidValues() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            HoldInfo holdInfo = HoldInfo.of("추가 확인 필요", now);

            // then
            assertThat(holdInfo.holdReason()).isEqualTo("추가 확인 필요");
            assertThat(holdInfo.holdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("holdReason이 null이면 예외가 발생한다")
        void createWithNullReason_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> HoldInfo.of(null, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 사유");
        }

        @Test
        @DisplayName("holdReason이 빈 문자열이면 예외가 발생한다")
        void createWithBlankReason_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> HoldInfo.of("   ", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 사유");
        }

        @Test
        @DisplayName("holdAt이 null이면 예외가 발생한다")
        void createWithNullHoldAt_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> HoldInfo.of("보류 사유", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 시각");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            Instant now = CommonVoFixtures.now();
            HoldInfo holdInfo1 = HoldInfo.of("추가 확인 필요", now);
            HoldInfo holdInfo2 = HoldInfo.of("추가 확인 필요", now);

            // then
            assertThat(holdInfo1).isEqualTo(holdInfo2);
            assertThat(holdInfo1.hashCode()).isEqualTo(holdInfo2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            Instant now = CommonVoFixtures.now();
            HoldInfo holdInfo1 = HoldInfo.of("사유 A", now);
            HoldInfo holdInfo2 = HoldInfo.of("사유 B", now);

            // then
            assertThat(holdInfo1).isNotEqualTo(holdInfo2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("HoldInfo는 record로 불변 객체이다")
        void holdInfoIsImmutable() {
            // given
            Instant now = CommonVoFixtures.now();
            HoldInfo holdInfo = HoldInfo.of("보류 사유", now);

            // then
            assertThat(holdInfo.holdReason()).isEqualTo("보류 사유");
            assertThat(holdInfo.holdAt()).isEqualTo(now);
        }
    }
}
