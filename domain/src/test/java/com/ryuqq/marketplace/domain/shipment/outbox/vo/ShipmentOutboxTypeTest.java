package com.ryuqq.marketplace.domain.shipment.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentOutboxType 단위 테스트")
class ShipmentOutboxTypeTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("CONFIRM의 설명은 발주확인이다")
        void confirmDescription() {
            assertThat(ShipmentOutboxType.CONFIRM.description()).isEqualTo("발주확인");
        }

        @Test
        @DisplayName("SHIP의 설명은 발송처리이다")
        void shipDescription() {
            assertThat(ShipmentOutboxType.SHIP.description()).isEqualTo("발송처리");
        }

        @Test
        @DisplayName("DELIVER의 설명은 배송완료이다")
        void deliverDescription() {
            assertThat(ShipmentOutboxType.DELIVER.description()).isEqualTo("배송완료");
        }

        @Test
        @DisplayName("CANCEL의 설명은 취소이다")
        void cancelDescription() {
            assertThat(ShipmentOutboxType.CANCEL.description()).isEqualTo("취소");
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("ShipmentOutboxType은 4가지 값이다")
        void outboxTypeValues() {
            assertThat(ShipmentOutboxType.values())
                    .containsExactlyInAnyOrder(
                            ShipmentOutboxType.CONFIRM,
                            ShipmentOutboxType.SHIP,
                            ShipmentOutboxType.DELIVER,
                            ShipmentOutboxType.CANCEL);
        }

        @Test
        @DisplayName("각 타입의 name()이 올바르다")
        void typeNames() {
            assertThat(ShipmentOutboxType.CONFIRM.name()).isEqualTo("CONFIRM");
            assertThat(ShipmentOutboxType.SHIP.name()).isEqualTo("SHIP");
            assertThat(ShipmentOutboxType.DELIVER.name()).isEqualTo("DELIVER");
            assertThat(ShipmentOutboxType.CANCEL.name()).isEqualTo("CANCEL");
        }
    }
}
