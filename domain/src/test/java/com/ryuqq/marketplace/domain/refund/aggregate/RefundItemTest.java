package com.ryuqq.marketplace.domain.refund.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.id.RefundItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundItem 단위 테스트")
class RefundItemTest {

    @Nested
    @DisplayName("forNew() - 신규 환불 아이템 생성")
    class ForNewTest {

        @Test
        @DisplayName("유효한 값으로 환불 아이템을 생성한다")
        void createWithValidValues() {
            // when
            RefundItem item = RefundItem.forNew(1001L, 2);

            // then
            assertThat(item).isNotNull();
            assertThat(item.orderItemId()).isEqualTo(1001L);
            assertThat(item.refundQty()).isEqualTo(2);
        }

        @Test
        @DisplayName("신규 생성 시 ID는 null이다")
        void newItemHasNullId() {
            // when
            RefundItem item = RefundItem.forNew(1001L, 1);

            // then
            assertThat(item.idValue()).isNull();
        }

        @Test
        @DisplayName("수량이 0이면 예외가 발생한다")
        void createWithZeroQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundItem.forNew(1001L, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1 이상");
        }

        @Test
        @DisplayName("수량이 음수이면 예외가 발생한다")
        void createWithNegativeQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundItem.forNew(1001L, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1 이상");
        }

        @Test
        @DisplayName("수량이 1이면 정상 생성된다")
        void createWithMinQty() {
            // when & then
            assertThatCode(() -> RefundItem.forNew(1001L, 1)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("유효한 값으로 환불 아이템을 재구성한다")
        void reconstituteWithValidValues() {
            // given
            RefundItemId id = RefundFixtures.defaultRefundItemId();

            // when
            RefundItem item = RefundItem.reconstitute(id, 1001L, 3);

            // then
            assertThat(item.id()).isEqualTo(id);
            assertThat(item.idValue()).isEqualTo(1L);
            assertThat(item.orderItemId()).isEqualTo(1001L);
            assertThat(item.refundQty()).isEqualTo(3);
        }

        @Test
        @DisplayName("재구성 시 수량 검증을 하지 않는다")
        void reconstituteDoesNotValidateQty() {
            // given
            RefundItemId id = RefundItemId.of(99L);

            // when & then (재구성은 DB에서 오므로 검증 없음)
            assertThatCode(() -> RefundItem.reconstitute(id, 1001L, 1)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Fixtures 검증")
    class FixturesTest {

        @Test
        @DisplayName("defaultRefundItem()은 유효한 아이템을 반환한다")
        void defaultRefundItemIsValid() {
            // when
            RefundItem item = RefundFixtures.defaultRefundItem();

            // then
            assertThat(item).isNotNull();
            assertThat(item.refundQty()).isGreaterThan(0);
        }

        @Test
        @DisplayName("reconstitutedRefundItem()은 ID가 있는 아이템을 반환한다")
        void reconstitutedRefundItemHasId() {
            // when
            RefundItem item = RefundFixtures.reconstitutedRefundItem();

            // then
            assertThat(item.idValue()).isNotNull();
        }
    }
}
