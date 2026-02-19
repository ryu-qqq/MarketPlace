package com.ryuqq.marketplace.domain.cancel.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.cancel.exception.CancelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelReason Value Object 테스트")
class CancelReasonTest {

    @Nested
    @DisplayName("생성 테스트 - 일반 사유")
    class NormalReasonCreationTest {

        @Test
        @DisplayName("유효한 사유 유형으로 생성한다")
        void createWithValidReasonType() {
            // when
            CancelReason reason = new CancelReason(CancelReasonType.CHANGE_OF_MIND, null);

            // then
            assertThat(reason.reasonType()).isEqualTo(CancelReasonType.CHANGE_OF_MIND);
            assertThat(reason.reasonDetail()).isNull();
        }

        @Test
        @DisplayName("WRONG_ORDER 유형으로 생성한다")
        void createWithWrongOrder() {
            // when
            CancelReason reason = new CancelReason(CancelReasonType.WRONG_ORDER, null);

            // then
            assertThat(reason.reasonType()).isEqualTo(CancelReasonType.WRONG_ORDER);
        }

        @Test
        @DisplayName("OUT_OF_STOCK 유형으로 상세 사유 없이 생성한다")
        void createWithOutOfStockWithoutDetail() {
            // when
            CancelReason reason = new CancelReason(CancelReasonType.OUT_OF_STOCK, null);

            // then
            assertThat(reason.reasonType()).isEqualTo(CancelReasonType.OUT_OF_STOCK);
            assertThat(reason.reasonDetail()).isNull();
        }
    }

    @Nested
    @DisplayName("생성 테스트 - OTHER 사유")
    class OtherReasonCreationTest {

        @Test
        @DisplayName("OTHER 유형은 상세 사유를 포함하면 생성된다")
        void createOtherReasonWithDetail() {
            // when
            CancelReason reason = new CancelReason(CancelReasonType.OTHER, "개인 사정으로 취소합니다");

            // then
            assertThat(reason.reasonType()).isEqualTo(CancelReasonType.OTHER);
            assertThat(reason.reasonDetail()).isEqualTo("개인 사정으로 취소합니다");
        }

        @Test
        @DisplayName("OTHER 유형에서 상세 사유가 null이면 예외가 발생한다")
        void createOtherReasonWithNullDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new CancelReason(CancelReasonType.OTHER, null))
                    .isInstanceOf(CancelException.class)
                    .hasMessageContaining("상세 사유는 필수");
        }

        @Test
        @DisplayName("OTHER 유형에서 상세 사유가 빈 문자열이면 예외가 발생한다")
        void createOtherReasonWithBlankDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new CancelReason(CancelReasonType.OTHER, "  "))
                    .isInstanceOf(CancelException.class)
                    .hasMessageContaining("상세 사유는 필수");
        }

        @Test
        @DisplayName("OTHER 유형에서 상세 사유가 빈 문자열('')이면 예외가 발생한다")
        void createOtherReasonWithEmptyDetail_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new CancelReason(CancelReasonType.OTHER, ""))
                    .isInstanceOf(CancelException.class);
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("reasonType이 null이면 예외가 발생한다")
        void createWithNullReasonType_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new CancelReason(null, null))
                    .isInstanceOf(CancelException.class)
                    .hasMessageContaining("취소 사유 유형은 필수");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 유형과 상세 사유이면 동일하다")
        void sameReasonAreEqual() {
            // given
            CancelReason reason1 = new CancelReason(CancelReasonType.CHANGE_OF_MIND, null);
            CancelReason reason2 = new CancelReason(CancelReasonType.CHANGE_OF_MIND, null);

            // then
            assertThat(reason1).isEqualTo(reason2);
            assertThat(reason1.hashCode()).isEqualTo(reason2.hashCode());
        }

        @Test
        @DisplayName("OTHER 유형에서 같은 상세 사유이면 동일하다")
        void sameOtherReasonAreEqual() {
            // given
            CancelReason reason1 = new CancelReason(CancelReasonType.OTHER, "개인 사정");
            CancelReason reason2 = new CancelReason(CancelReasonType.OTHER, "개인 사정");

            // then
            assertThat(reason1).isEqualTo(reason2);
        }

        @Test
        @DisplayName("다른 유형이면 동일하지 않다")
        void differentTypesAreNotEqual() {
            // given
            CancelReason reason1 = new CancelReason(CancelReasonType.CHANGE_OF_MIND, null);
            CancelReason reason2 = new CancelReason(CancelReasonType.WRONG_ORDER, null);

            // then
            assertThat(reason1).isNotEqualTo(reason2);
        }
    }
}
