package com.ryuqq.marketplace.domain.exchange.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeItem 단위 테스트")
class ExchangeItemTest {

    @Nested
    @DisplayName("forNew() - 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("유효한 파라미터로 ExchangeItem을 생성한다")
        void createWithValidParams() {
            // when
            ExchangeItem item = ExchangeItem.forNew(10001L, 2);

            // then
            assertThat(item).isNotNull();
            assertThat(item.orderItemId()).isEqualTo(10001L);
            assertThat(item.exchangeQty()).isEqualTo(2);
        }

        @Test
        @DisplayName("신규 생성 시 ID는 null이다")
        void newItemHasNullId() {
            // when
            ExchangeItem item = ExchangeItem.forNew(10001L, 1);

            // then
            assertThat(item.idValue()).isNull();
        }

        @Test
        @DisplayName("교환 수량이 0이면 예외가 발생한다")
        void createWithZeroQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeItem.forNew(10001L, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 수량은 1 이상이어야 합니다");
        }

        @Test
        @DisplayName("교환 수량이 음수이면 예외가 발생한다")
        void createWithNegativeQty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExchangeItem.forNew(10001L, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 수량은 1 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ID와 함께 재구성한다")
        void reconstituteWithId() {
            // given
            ExchangeItemId id = ExchangeFixtures.defaultExchangeItemId();

            // when
            ExchangeItem item = ExchangeItem.reconstitute(id, 10001L, 2);

            // then
            assertThat(item.id()).isEqualTo(id);
            assertThat(item.idValue()).isEqualTo(1L);
            assertThat(item.orderItemId()).isEqualTo(10001L);
            assertThat(item.exchangeQty()).isEqualTo(2);
        }

        @Test
        @DisplayName("재구성 시 수량 유효성 검사를 하지 않는다")
        void reconstituteDoesNotValidateQty() {
            // given
            ExchangeItemId id = ExchangeFixtures.defaultExchangeItemId();

            // when & then (reconstitute는 검증 없이 생성됨)
            ExchangeItem item = ExchangeItem.reconstitute(id, 10001L, 0);
            assertThat(item.exchangeQty()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("모든 필드를 올바르게 반환한다")
        void allFieldsReturned() {
            // given
            ExchangeItem item = ExchangeFixtures.defaultExchangeItem();

            // then
            assertThat(item.orderItemId()).isEqualTo(10001L);
            assertThat(item.exchangeQty()).isEqualTo(1);
            assertThat(item.id()).isNotNull();
        }
    }
}
