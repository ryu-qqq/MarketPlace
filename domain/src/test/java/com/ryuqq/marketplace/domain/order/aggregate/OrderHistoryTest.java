package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderHistoryId;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderHistory 단위 테스트")
class OrderHistoryTest {

    @Nested
    @DisplayName("of() - 이력 생성")
    class OfTest {

        @Test
        @DisplayName("fromStatus가 null인 최초 상태 변경 이력을 생성한다")
        void createInitialHistoryWithNullFromStatus() {
            // given
            OrderId orderId = OrderFixtures.defaultOrderId();
            Instant now = CommonVoFixtures.now();

            // when
            OrderHistory history =
                    OrderHistory.of(orderId, null, OrderStatus.ORDERED, "system", null, now);

            // then
            assertThat(history.orderId()).isEqualTo(orderId);
            assertThat(history.fromStatus()).isNull();
            assertThat(history.toStatus()).isEqualTo(OrderStatus.ORDERED);
            assertThat(history.changedBy()).isEqualTo("system");
            assertThat(history.reason()).isNull();
            assertThat(history.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("상태 전이 이력을 생성한다")
        void createStatusTransitionHistory() {
            // given
            OrderId orderId = OrderFixtures.defaultOrderId();
            Instant now = CommonVoFixtures.now();

            // when
            OrderHistory history =
                    OrderHistory.of(
                            orderId,
                            OrderStatus.ORDERED,
                            OrderStatus.PREPARING,
                            "system",
                            null,
                            now);

            // then
            assertThat(history.fromStatus()).isEqualTo(OrderStatus.ORDERED);
            assertThat(history.toStatus()).isEqualTo(OrderStatus.PREPARING);
        }

        @Test
        @DisplayName("취소 사유를 포함한 이력을 생성한다")
        void createHistoryWithReason() {
            // given
            OrderId orderId = OrderFixtures.defaultOrderId();
            String reason = "단순 변심";

            // when
            OrderHistory history =
                    OrderHistory.of(
                            orderId,
                            OrderStatus.ORDERED,
                            OrderStatus.CANCELLED,
                            "buyer",
                            reason,
                            CommonVoFixtures.now());

            // then
            assertThat(history.reason()).isEqualTo(reason);
            assertThat(history.changedBy()).isEqualTo("buyer");
        }

        @Test
        @DisplayName("of()로 생성한 이력은 ID가 null이다")
        void historyCreatedByOfHasNullId() {
            // when
            OrderHistory history =
                    OrderFixtures.defaultOrderHistory(OrderFixtures.defaultOrderId());

            // then
            assertThat(history.id().isNew()).isTrue();
            assertThat(history.id().value()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ID를 포함하여 이력을 재구성한다")
        void reconstituteHistoryWithId() {
            // given
            OrderHistoryId historyId = OrderHistoryId.of(10L);
            OrderId orderId = OrderFixtures.defaultOrderId();
            Instant now = CommonVoFixtures.now();

            // when
            OrderHistory history =
                    OrderHistory.reconstitute(
                            historyId,
                            orderId,
                            OrderStatus.ORDERED,
                            OrderStatus.PREPARING,
                            "system",
                            null,
                            now);

            // then
            assertThat(history.id()).isEqualTo(historyId);
            assertThat(history.id().isNew()).isFalse();
            assertThat(history.orderId()).isEqualTo(orderId);
        }
    }

    @Nested
    @DisplayName("idValue() getter 테스트")
    class IdValueTest {

        @Test
        @DisplayName("idValue()는 ID의 Long 값을 반환한다")
        void idValueReturnsLong() {
            // given
            OrderHistoryId historyId = OrderHistoryId.of(99L);
            OrderHistory history =
                    OrderHistory.reconstitute(
                            historyId,
                            OrderFixtures.defaultOrderId(),
                            null,
                            OrderStatus.ORDERED,
                            "system",
                            null,
                            CommonVoFixtures.now());

            // then
            assertThat(history.idValue()).isEqualTo(99L);
        }
    }

    @Nested
    @DisplayName("OrderHistoryId 테스트")
    class OrderHistoryIdTest {

        @Test
        @DisplayName("forNew()는 value가 null인 ID를 생성한다")
        void forNewOrderHistoryIdHasNullValue() {
            // when
            OrderHistoryId id = OrderHistoryId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("of()로 유효한 ID를 생성한다")
        void ofCreateValidOrderHistoryId() {
            // when
            OrderHistoryId id = OrderHistoryId.of(50L);

            // then
            assertThat(id.value()).isEqualTo(50L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외가 발생한다")
        void ofWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OrderHistoryId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }
}
