package com.ryuqq.marketplace.domain.cancel.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.cancel.id.CancelItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelItem 단위 테스트")
class CancelItemTest {

    @Nested
    @DisplayName("forNew() - 신규 취소 항목 생성")
    class ForNewTest {

        @Test
        @DisplayName("유효한 값으로 신규 취소 항목을 생성한다")
        void createNewCancelItemWithValidValues() {
            // when
            CancelItem item = CancelItem.forNew(1001L, 2);

            // then
            assertThat(item.orderItemId()).isEqualTo(1001L);
            assertThat(item.cancelQty()).isEqualTo(2);
            assertThat(item.id()).isNotNull();
            assertThat(item.id().isNew()).isTrue();
            assertThat(item.idValue()).isNull();
        }

        @Test
        @DisplayName("취소 수량이 1이면 생성된다")
        void createNewCancelItemWithMinimumQty() {
            // when
            CancelItem item = CancelItem.forNew(1001L, 1);

            // then
            assertThat(item.cancelQty()).isEqualTo(1);
        }

        @Test
        @DisplayName("취소 수량이 0이면 예외가 발생한다")
        void createNewCancelItemWithZeroQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelItem.forNew(1001L, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1 이상");
        }

        @Test
        @DisplayName("취소 수량이 음수이면 예외가 발생한다")
        void createNewCancelItemWithNegativeQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelItem.forNew(1001L, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1 이상");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 기존 취소 항목 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ID가 있는 취소 항목을 재구성한다")
        void reconstituteCancelItem() {
            // given
            CancelItemId id = CancelItemId.of(10L);

            // when
            CancelItem item = CancelItem.reconstitute(id, 2001L, 3);

            // then
            assertThat(item.id()).isEqualTo(id);
            assertThat(item.idValue()).isEqualTo(10L);
            assertThat(item.orderItemId()).isEqualTo(2001L);
            assertThat(item.cancelQty()).isEqualTo(3);
            assertThat(item.id().isNew()).isFalse();
        }
    }
}
