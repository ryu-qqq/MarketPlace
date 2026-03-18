package com.ryuqq.marketplace.adapter.out.persistence.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrderJpaEntityMapper 단위 테스트.
 *
 * <p>Payment 리팩토링 이후 toPaymentEntity(Order, String paymentId) 시그니처 변경 검증 포인트: UUIDv7 id 전달,
 * paymentNumber 매핑, PENDING/COMPLETED 상태 분기.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("OrderJpaEntityMapper 단위 테스트")
class OrderJpaEntityMapperTest {

    private OrderJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderJpaEntityMapper();
    }

    // ========================================================================
    // toPaymentEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toPaymentEntity 메서드 테스트")
    class ToPaymentEntityTest {

        @Test
        @DisplayName("외부에서 전달한 UUIDv7 paymentId가 Entity id 필드에 그대로 매핑됩니다")
        void toPaymentEntity_WithProvidedPaymentId_UsesItAsEntityId() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000099";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getId()).isEqualTo(paymentId);
        }

        @Test
        @DisplayName("PaymentInfo의 paymentNumber가 Entity paymentNumber 필드에 매핑됩니다")
        void toPaymentEntity_WithPaymentNumber_MapsPaymentNumberCorrectly() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000099";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getPaymentNumber())
                    .isEqualTo(order.paymentInfo().paymentNumber().value());
        }

        @Test
        @DisplayName("paidAt이 있으면 paymentStatus가 COMPLETED로 매핑됩니다")
        void toPaymentEntity_WithPaidAt_MapsStatusAsCompleted() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000099";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getPaymentStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("orderId는 Order의 idValue로 매핑됩니다")
        void toPaymentEntity_WithOrder_MapsOrderIdCorrectly() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000002";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getOrderId()).isEqualTo(order.idValue());
        }

        @Test
        @DisplayName("paymentMethod가 Entity paymentMethod 필드에 매핑됩니다")
        void toPaymentEntity_WithPaymentMethod_MapsPaymentMethodCorrectly() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000003";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getPaymentMethod()).isEqualTo(order.paymentInfo().paymentMethod());
        }

        @Test
        @DisplayName("totalPaymentAmount가 Entity paymentAmount 필드에 매핑됩니다")
        void toPaymentEntity_WithPaymentAmount_MapsAmountCorrectly() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000004";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getPaymentAmount())
                    .isEqualTo(order.paymentInfo().totalPaymentAmount().value());
        }

        @Test
        @DisplayName("PaymentInfo가 null일 때 paymentStatus가 PENDING으로 매핑됩니다")
        void toPaymentEntity_WithNullPaymentInfo_MapsStatusAsPending() {
            // given
            Order order =
                    Order.reconstitute(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            OrderFixtures.defaultBuyerInfo(),
                            null,
                            OrderFixtures.defaultExternalOrderReference(),
                            Instant.now().minusSeconds(3600),
                            Instant.now(),
                            java.util.List.of());
            String paymentId = "01944b2a-bbbb-7fff-8888-000000000005";

            // when
            PaymentJpaEntity entity = mapper.toPaymentEntity(order, paymentId);

            // then
            assertThat(entity.getPaymentStatus()).isEqualTo("PENDING");
            assertThat(entity.getPaymentNumber()).isNull();
            assertThat(entity.getPaymentAmount()).isZero();
        }

        @Test
        @DisplayName("서로 다른 paymentId를 전달하면 각각 다른 Entity id가 생성됩니다")
        void toPaymentEntity_WithDifferentPaymentIds_CreatesDifferentEntities() {
            // given
            Order order = OrderFixtures.newOrder();
            String paymentId1 = "01944b2a-bbbb-7fff-8888-000000000010";
            String paymentId2 = "01944b2a-bbbb-7fff-8888-000000000011";

            // when
            PaymentJpaEntity entity1 = mapper.toPaymentEntity(order, paymentId1);
            PaymentJpaEntity entity2 = mapper.toPaymentEntity(order, paymentId2);

            // then
            assertThat(entity1.getId()).isEqualTo(paymentId1);
            assertThat(entity2.getId()).isEqualTo(paymentId2);
            assertThat(entity1.getId()).isNotEqualTo(entity2.getId());
        }
    }
}
