package com.ryuqq.marketplace.domain.inboundorder.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.inboundorder.InboundOrderFixtures;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrders 도메인 VO 단위 테스트")
class InboundOrdersTest {

    @Nested
    @DisplayName("of() / empty() 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("주문 목록으로 InboundOrders를 생성한다")
        void createWithOrders() {
            List<InboundOrder> orders =
                    List.of(
                            InboundOrderFixtures.receivedOrder(1L),
                            InboundOrderFixtures.receivedOrder(2L));

            InboundOrders inboundOrders = InboundOrders.of(orders);

            assertThat(inboundOrders.size()).isEqualTo(2);
            assertThat(inboundOrders.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("빈 InboundOrders를 생성한다")
        void createEmpty() {
            InboundOrders inboundOrders = InboundOrders.empty();

            assertThat(inboundOrders.size()).isEqualTo(0);
            assertThat(inboundOrders.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("externalOrderNos() 테스트")
    class ExternalOrderNosTest {

        @Test
        @DisplayName("모든 주문의 외부 주문번호 집합을 반환한다")
        void returnsExternalOrderNos() {
            InboundOrder order1 = InboundOrderFixtures.receivedOrder(1L);
            InboundOrder order2 = InboundOrderFixtures.receivedOrder(2L);
            InboundOrders inboundOrders = InboundOrders.of(List.of(order1, order2));

            Set<String> nos = inboundOrders.externalOrderNos();

            assertThat(nos).contains(InboundOrderFixtures.DEFAULT_EXTERNAL_ORDER_NO);
        }
    }

    @Nested
    @DisplayName("excludeDuplicates() 테스트")
    class ExcludeDuplicatesTest {

        @Test
        @DisplayName("이미 존재하는 주문번호를 제외한 주문을 반환한다")
        void excludesExistingOrderNos() {
            InboundOrder order1 = InboundOrderFixtures.receivedOrder(1L);
            InboundOrder order2 = InboundOrderFixtures.receivedOrder(2L);
            InboundOrders inboundOrders = InboundOrders.of(List.of(order1, order2));
            Set<String> existing = Set.of(InboundOrderFixtures.DEFAULT_EXTERNAL_ORDER_NO);

            InboundOrders filtered = inboundOrders.excludeDuplicates(existing);

            assertThat(filtered.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("기존 주문번호가 없으면 전체를 반환한다")
        void returnsAllWhenNoExistingOrderNos() {
            InboundOrder order1 = InboundOrderFixtures.receivedOrder(1L);
            InboundOrders inboundOrders = InboundOrders.of(List.of(order1));

            InboundOrders filtered = inboundOrders.excludeDuplicates(Set.of());

            assertThat(filtered.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("fullyMapped() / pendingMapping() 테스트")
    class MappingFilterTest {

        @Test
        @DisplayName("모든 아이템이 매핑된 주문만 반환한다")
        void returnsFullyMappedOrders() {
            InboundOrder mappedOrder = InboundOrderFixtures.mappedOrder();
            InboundOrder unmappedOrder = InboundOrderFixtures.receivedOrder();
            InboundOrders inboundOrders = InboundOrders.of(List.of(mappedOrder, unmappedOrder));

            List<InboundOrder> fullyMapped = inboundOrders.fullyMapped();

            assertThat(fullyMapped).hasSize(1);
            assertThat(fullyMapped.get(0).idValue()).isEqualTo(mappedOrder.idValue());
        }

        @Test
        @DisplayName("매핑되지 않은 아이템이 있는 주문만 반환한다")
        void returnsPendingMappingOrders() {
            InboundOrder mappedOrder = InboundOrderFixtures.mappedOrder();
            InboundOrder unmappedOrder = InboundOrderFixtures.receivedOrder();
            InboundOrders inboundOrders = InboundOrders.of(List.of(mappedOrder, unmappedOrder));

            List<InboundOrder> pending = inboundOrders.pendingMapping();

            assertThat(pending).hasSize(1);
            assertThat(pending.get(0).idValue()).isEqualTo(unmappedOrder.idValue());
        }
    }

    @Nested
    @DisplayName("notConverted() 테스트")
    class NotConvertedTest {

        @Test
        @DisplayName("CONVERTED가 아닌 주문만 반환한다")
        void returnsNotConvertedOrders() {
            InboundOrder convertedOrder = InboundOrderFixtures.convertedOrder();
            InboundOrder receivedOrder = InboundOrderFixtures.receivedOrder();
            InboundOrders inboundOrders = InboundOrders.of(List.of(convertedOrder, receivedOrder));

            List<InboundOrder> notConverted = inboundOrders.notConverted();

            assertThat(notConverted).hasSize(1);
            assertThat(notConverted.get(0).idValue()).isEqualTo(receivedOrder.idValue());
        }
    }

    @Nested
    @DisplayName("all() - 불변성 테스트")
    class AllImmutabilityTest {

        @Test
        @DisplayName("all() 반환 리스트는 수정할 수 없다")
        void allListIsUnmodifiable() {
            InboundOrders inboundOrders =
                    InboundOrders.of(List.of(InboundOrderFixtures.receivedOrder()));

            assertThatThrownBy(() -> inboundOrders.all().add(InboundOrderFixtures.receivedOrder()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("unmappedExternalProductIds() 테스트")
    class UnmappedExternalProductIdsTest {

        @Test
        @DisplayName("매핑되지 않은 외부 상품 ID 집합을 반환한다")
        void returnsUnmappedExternalProductIds() {
            InboundOrder receivedOrder = InboundOrderFixtures.receivedOrder();
            InboundOrders inboundOrders = InboundOrders.of(List.of(receivedOrder));

            Set<String> unmapped = inboundOrders.unmappedExternalProductIds();

            assertThat(unmapped).contains(InboundOrderFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
        }

        @Test
        @DisplayName("모두 매핑된 주문에서는 빈 집합을 반환한다")
        void returnsEmptyWhenAllMapped() {
            InboundOrder mappedOrder = InboundOrderFixtures.mappedOrder();
            InboundOrders inboundOrders = InboundOrders.of(List.of(mappedOrder));

            Set<String> unmapped = inboundOrders.unmappedExternalProductIds();

            assertThat(unmapped).isEmpty();
        }
    }
}
