package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.PaymentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.condition.OrderConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * OrderQueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 및 연관 엔티티 조회를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("OrderQueryDslRepository 통합 테스트")
class OrderQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private OrderQueryDslRepository repository() {
        return new OrderQueryDslRepository(
                new JPAQueryFactory(entityManager), new OrderConditionBuilder());
    }

    private OrderJpaEntity persistOrder(OrderJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private PaymentJpaEntity persistPayment(PaymentJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private OrderItemJpaEntity persistItem(OrderItemJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private OrderItemHistoryJpaEntity persistHistory(OrderItemHistoryJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Order는 findById로 조회됩니다")
        void findById_WithNotDeletedOrder_ReturnsEntity() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000001";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));

            // when
            Optional<OrderJpaEntity> result = repository().findById(orderId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("삭제된 Order는 findById로 조회되지 않습니다")
        void findById_WithDeletedOrder_ReturnsEmpty() {
            // given: deletedEntity의 DEFAULT_ID 사용 시 충돌 방지를 위해 별도 Entity 생성
            persistOrder(OrderJpaEntityFixtures.deletedEntity());

            // when
            Optional<OrderJpaEntity> result =
                    repository().findById(OrderJpaEntityFixtures.DEFAULT_ID);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 orderId로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<OrderJpaEntity> result =
                    repository().findById("00000000-0000-7000-8000-000000000000");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // findByOrderNumber 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderNumber 테스트")
    class FindByOrderNumberTest {

        @Test
        @DisplayName("미삭제 Order를 orderNumber로 조회합니다")
        void findByOrderNumber_WithNotDeletedOrder_ReturnsEntity() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000010";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));

            // when
            Optional<OrderJpaEntity> result =
                    repository().findByOrderNumber(OrderJpaEntityFixtures.DEFAULT_ORDER_NUMBER);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getOrderNumber())
                    .isEqualTo(OrderJpaEntityFixtures.DEFAULT_ORDER_NUMBER);
        }

        @Test
        @DisplayName("존재하지 않는 orderNumber로 조회 시 빈 Optional을 반환합니다")
        void findByOrderNumber_WithNonExistingOrderNumber_ReturnsEmpty() {
            // when
            Optional<OrderJpaEntity> result = repository().findByOrderNumber("ORD-99999999-9999");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // existsByExternalOrderNo 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByExternalOrderNo 테스트")
    class ExistsByExternalOrderNoTest {

        @Test
        @DisplayName("미삭제 Order의 externalOrderNo가 존재하면 true를 반환합니다")
        void existsByExternalOrderNo_WithActiveOrder_ReturnsTrue() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000020";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));

            // when
            boolean result =
                    repository()
                            .existsByExternalOrderNo(
                                    OrderJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    OrderJpaEntityFixtures.DEFAULT_EXTERNAL_ORDER_NO);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Order의 externalOrderNo는 존재하지 않는 것으로 처리됩니다")
        void existsByExternalOrderNo_WithDeletedOrder_ReturnsFalse() {
            // given
            persistOrder(OrderJpaEntityFixtures.deletedEntity());

            // when
            boolean result =
                    repository()
                            .existsByExternalOrderNo(
                                    OrderJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    OrderJpaEntityFixtures.DEFAULT_EXTERNAL_ORDER_NO);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 externalOrderNo는 false를 반환합니다")
        void existsByExternalOrderNo_WithNonExistingOrderNo_ReturnsFalse() {
            // when
            boolean result = repository().existsByExternalOrderNo(1L, "EXT-NOT-EXIST-000");

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // findPaymentByOrderId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPaymentByOrderId 테스트")
    class FindPaymentByOrderIdTest {

        @Test
        @DisplayName("orderId로 Payment를 조회합니다")
        void findPaymentByOrderId_WithExistingPayment_ReturnsEntity() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000030";
            String paymentId = "01944b2a-aaaa-7fff-8888-000000000030";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));
            persistPayment(PaymentJpaEntityFixtures.completedEntity(paymentId, orderId));

            // when
            Optional<PaymentJpaEntity> result = repository().findPaymentByOrderId(orderId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getOrderId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("Payment가 없는 orderId로 조회 시 빈 Optional을 반환합니다")
        void findPaymentByOrderId_WithNoPayment_ReturnsEmpty() {
            // when
            Optional<PaymentJpaEntity> result =
                    repository().findPaymentByOrderId("00000000-0000-7000-8000-000000000000");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // findItemsByOrderId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findItemsByOrderId 테스트")
    class FindItemsByOrderIdTest {

        @Test
        @DisplayName("orderId로 OrderItem 목록을 조회합니다")
        void findItemsByOrderId_WithExistingItems_ReturnsEntities() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000040";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));
            persistItem(OrderItemJpaEntityFixtures.defaultItem(orderId));
            persistItem(OrderItemJpaEntityFixtures.defaultItem(orderId));

            // when
            List<OrderItemJpaEntity> result = repository().findItemsByOrderId(orderId);

            // then
            assertThat(result).hasSize(2);
            result.forEach(item -> assertThat(item.getOrderId()).isEqualTo(orderId));
        }

        @Test
        @DisplayName("OrderItem이 없는 orderId로 조회 시 빈 리스트를 반환합니다")
        void findItemsByOrderId_WithNoItems_ReturnsEmptyList() {
            // when
            List<OrderItemJpaEntity> result =
                    repository().findItemsByOrderId("00000000-0000-7000-8000-000000000000");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // findItemHistoriesByOrderItemIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findItemHistoriesByOrderItemIds 테스트")
    class FindItemHistoriesByOrderItemIdsTest {

        @Test
        @DisplayName("orderItemId 목록으로 이력을 조회합니다")
        void findItemHistoriesByOrderItemIds_WithExistingHistories_ReturnsEntities() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000050";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = persistItem(OrderItemJpaEntityFixtures.defaultItem(orderId));
            persistHistory(OrderItemHistoryJpaEntityFixtures.creationHistory(item.getId()));
            persistHistory(
                    OrderItemHistoryJpaEntityFixtures.transitionHistory(
                            item.getId(), "READY", "CONFIRMED"));

            // when
            List<OrderItemHistoryJpaEntity> result =
                    repository().findItemHistoriesByOrderItemIds(List.of(item.getId()));

            // then
            assertThat(result).hasSize(2);
            result.forEach(h -> assertThat(h.getOrderItemId()).isEqualTo(item.getId()));
        }

        @Test
        @DisplayName("빈 orderItemId 목록을 전달하면 빈 리스트를 반환합니다")
        void findItemHistoriesByOrderItemIds_WithEmptyIds_ReturnsEmptyList() {
            // when
            List<OrderItemHistoryJpaEntity> result =
                    repository().findItemHistoriesByOrderItemIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null orderItemId 목록을 전달하면 빈 리스트를 반환합니다")
        void findItemHistoriesByOrderItemIds_WithNullIds_ReturnsEmptyList() {
            // when
            List<OrderItemHistoryJpaEntity> result =
                    repository().findItemHistoriesByOrderItemIds(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("이력이 changedAt 역순으로 정렬되어 반환됩니다")
        void findItemHistoriesByOrderItemIds_ReturnsHistoriesOrderedByChangedAtDesc() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abc000000060";
            persistOrder(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = persistItem(OrderItemJpaEntityFixtures.defaultItem(orderId));
            persistHistory(OrderItemHistoryJpaEntityFixtures.creationHistory(item.getId()));
            persistHistory(
                    OrderItemHistoryJpaEntityFixtures.transitionHistory(
                            item.getId(), "READY", "CONFIRMED"));

            // when
            List<OrderItemHistoryJpaEntity> result =
                    repository().findItemHistoriesByOrderItemIds(List.of(item.getId()));

            // then
            assertThat(result).hasSize(2);
            // changedAt DESC 정렬: 나중에 저장된 이력이 먼저 반환
            assertThat(result.get(0).getToStatus()).isEqualTo("CONFIRMED");
        }
    }
}
