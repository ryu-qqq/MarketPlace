package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItemHistory 단위 테스트")
class OrderItemHistoryTest {

    @Nested
    @DisplayName("of() - 이력 생성 (신규)")
    class OfTest {

        @Test
        @DisplayName("of()로 새 이력을 생성하면 id가 null이다")
        void ofCreatesHistoryWithNullId() {
            // given
            OrderItemId orderItemId = OrderFixtures.defaultOrderItemId();
            Instant changedAt = CommonVoFixtures.now();

            // when
            OrderItemHistory history =
                    OrderItemHistory.of(
                            orderItemId,
                            OrderItemStatus.READY,
                            OrderItemStatus.CONFIRMED,
                            "system",
                            null,
                            changedAt);

            // then
            assertThat(history.id()).isNull();
            assertThat(history.orderItemId()).isEqualTo(orderItemId);
            assertThat(history.fromStatus()).isEqualTo(OrderItemStatus.READY);
            assertThat(history.toStatus()).isEqualTo(OrderItemStatus.CONFIRMED);
            assertThat(history.changedBy()).isEqualTo("system");
            assertThat(history.reason()).isNull();
            assertThat(history.changedAt()).isEqualTo(changedAt);
        }

        @Test
        @DisplayName("of()로 취소 이력을 생성하면 사유가 기록된다")
        void ofCreatesCancelHistoryWithReason() {
            // given
            OrderItemId orderItemId = OrderFixtures.defaultOrderItemId();
            String reason = "단순 변심";
            Instant changedAt = CommonVoFixtures.now();

            // when
            OrderItemHistory history =
                    OrderItemHistory.of(
                            orderItemId,
                            OrderItemStatus.READY,
                            OrderItemStatus.CANCELLED,
                            "customer",
                            reason,
                            changedAt);

            // then
            assertThat(history.reason()).isEqualTo(reason);
            assertThat(history.changedBy()).isEqualTo("customer");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("reconstitute()로 이력을 복원하면 id가 설정된다")
        void reconstituteCreatesHistoryWithId() {
            // given
            Long id = 100L;
            OrderItemId orderItemId = OrderFixtures.defaultOrderItemId();
            Instant changedAt = CommonVoFixtures.yesterday();

            // when
            OrderItemHistory history =
                    OrderItemHistory.reconstitute(
                            id,
                            orderItemId,
                            OrderItemStatus.READY,
                            OrderItemStatus.CONFIRMED,
                            "admin",
                            null,
                            changedAt);

            // then
            assertThat(history.id()).isEqualTo(id);
            assertThat(history.orderItemId()).isEqualTo(orderItemId);
            assertThat(history.fromStatus()).isEqualTo(OrderItemStatus.READY);
            assertThat(history.toStatus()).isEqualTo(OrderItemStatus.CONFIRMED);
            assertThat(history.changedAt()).isEqualTo(changedAt);
        }
    }

    @Nested
    @DisplayName("getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("orderItemIdValue()는 OrderItemId의 String 값을 반환한다")
        void orderItemIdValueReturnsStringValue() {
            // given
            OrderItemId orderItemId = OrderFixtures.defaultOrderItemId();

            // when
            OrderItemHistory history =
                    OrderItemHistory.of(
                            orderItemId,
                            OrderItemStatus.READY,
                            OrderItemStatus.CONFIRMED,
                            "system",
                            null,
                            CommonVoFixtures.now());

            // then
            assertThat(history.orderItemIdValue()).isEqualTo(orderItemId.value());
        }
    }
}
