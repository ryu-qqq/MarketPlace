package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItem 단위 테스트")
class OrderItemTest {

    @Nested
    @DisplayName("forNew() - 신규 주문 상품 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 주문 상품 생성 시 UUIDv7 ID가 설정된다")
        void forNewOrderItemHasUuidId() {
            // when
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.id().value()).isNotBlank();
            assertThat(item.id().value())
                    .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("신규 주문 상품에 내부 상품 참조가 설정된다")
        void forNewOrderItemHasInternalProductReference() {
            // when
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.internalProduct())
                    .isEqualTo(OrderFixtures.defaultInternalProductReference());
        }

        @Test
        @DisplayName("신규 주문 상품에 외부 상품 스냅샷이 설정된다")
        void forNewOrderItemHasExternalProductSnapshot() {
            // when
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.externalProduct())
                    .isEqualTo(OrderFixtures.defaultExternalProductSnapshot());
        }

        @Test
        @DisplayName("신규 주문 상품에 가격 정보가 설정된다")
        void forNewOrderItemHasPrice() {
            // when
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.price()).isEqualTo(OrderFixtures.defaultExternalOrderItemPrice());
        }

        @Test
        @DisplayName("신규 주문 상품에 수령인 정보가 설정된다")
        void forNewOrderItemHasReceiverInfo() {
            // when
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.receiverInfo()).isEqualTo(OrderFixtures.defaultReceiverInfo());
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ID를 포함하여 주문 상품을 재구성한다")
        void reconstituteOrderItemWithId() {
            // when
            OrderItem item = OrderFixtures.reconstitutedOrderItem();

            // then
            assertThat(item.id()).isEqualTo(OrderFixtures.defaultOrderItemId());
            assertThat(item.id().value()).isNotBlank();
        }

        @Test
        @DisplayName("재구성 시 모든 필드가 올바르게 설정된다")
        void reconstituteOrderItemWithAllFields() {
            // when
            OrderItem item = OrderFixtures.reconstitutedOrderItem();

            // then
            assertThat(item.internalProduct())
                    .isEqualTo(OrderFixtures.defaultInternalProductReference());
            assertThat(item.externalProduct())
                    .isEqualTo(OrderFixtures.defaultExternalProductSnapshot());
            assertThat(item.price()).isEqualTo(OrderFixtures.defaultExternalOrderItemPrice());
            assertThat(item.receiverInfo()).isEqualTo(OrderFixtures.defaultReceiverInfo());
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 String(UUIDv7) 값을 반환한다")
        void idValueReturnsString() {
            // given
            OrderItem item = OrderFixtures.reconstitutedOrderItem();

            // then
            assertThat(item.idValue()).isEqualTo(OrderFixtures.defaultOrderItemId().value());
        }

        @Test
        @DisplayName("quantity()는 가격 정보의 수량을 위임한다")
        void quantityDelegatesToPrice() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.quantity()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("OrderItemId 테스트")
    class OrderItemIdTest {

        @Test
        @DisplayName("of()로 유효한 UUIDv7 ID를 생성한다")
        void ofCreateValidOrderItemId() {
            // when
            String uuid = "01940001-0000-7000-8000-000000000001";
            OrderItemId id = OrderItemId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외가 발생한다")
        void ofWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("of()에 빈 문자열을 전달하면 예외가 발생한다")
        void ofWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderItemId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }
}
