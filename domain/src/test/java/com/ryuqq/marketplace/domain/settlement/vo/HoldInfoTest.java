package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.*;

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
    @DisplayName("of() - 보류 정보 생성")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 보류 정보를 생성한다")
        void createWithValidValues() {
            // given
            String reason = "이상 거래 의심";
            Instant holdAt = CommonVoFixtures.now();

            // when
            HoldInfo holdInfo = HoldInfo.of(reason, holdAt);

            // then
            assertThat(holdInfo.holdReason()).isEqualTo(reason);
            assertThat(holdInfo.holdAt()).isEqualTo(holdAt);
        }

        @Test
        @DisplayName("holdReason이 null이면 예외가 발생한다")
        void nullReason_ThrowsException() {
            assertThatThrownBy(() -> HoldInfo.of(null, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 사유");
        }

        @Test
        @DisplayName("holdReason이 빈 문자열이면 예외가 발생한다")
        void blankReason_ThrowsException() {
            assertThatThrownBy(() -> HoldInfo.of("   ", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 사유");
        }

        @Test
        @DisplayName("holdAt이 null이면 예외가 발생한다")
        void nullHoldAt_ThrowsException() {
            assertThatThrownBy(() -> HoldInfo.of("보류 사유", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("보류 시각");
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 HoldInfo는 같다")
        void sameValuesAreEqual() {
            // given
            Instant holdAt = CommonVoFixtures.now();
            HoldInfo holdInfo1 = HoldInfo.of("보류 사유", holdAt);
            HoldInfo holdInfo2 = HoldInfo.of("보류 사유", holdAt);

            // then
            assertThat(holdInfo1).isEqualTo(holdInfo2);
            assertThat(holdInfo1.hashCode()).isEqualTo(holdInfo2.hashCode());
        }

        @Test
        @DisplayName("다른 holdReason이면 동일하지 않다")
        void differentReasonIsNotEqual() {
            // given
            Instant holdAt = CommonVoFixtures.now();
            HoldInfo holdInfo1 = HoldInfo.of("사유 A", holdAt);
            HoldInfo holdInfo2 = HoldInfo.of("사유 B", holdAt);

            // then
            assertThat(holdInfo1).isNotEqualTo(holdInfo2);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("생성된 HoldInfo의 값은 변경되지 않는다")
        void holdInfoIsImmutable() {
            // given
            String reason = "원본 보류 사유";
            Instant holdAt = CommonVoFixtures.now();
            HoldInfo holdInfo = HoldInfo.of(reason, holdAt);

            // then
            assertThat(holdInfo.holdReason()).isEqualTo(reason);
            assertThat(holdInfo.holdAt()).isEqualTo(holdAt);
        }
    }
}
