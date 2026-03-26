package com.ryuqq.marketplace.domain.inboundorder.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundorder.InboundOrderFixtures;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrder Aggregate 단위 테스트")
class InboundOrderTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundOrder 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 InboundOrder는 RECEIVED 상태로 생성된다")
        void createNewInboundOrderWithReceivedStatus() {
            InboundOrder order = InboundOrderFixtures.newReceivedOrder();

            assertThat(order.status()).isEqualTo(InboundOrderStatus.RECEIVED);
        }

        @Test
        @DisplayName("신규 생성 시 internalOrderId와 failureReason은 null이다")
        void createNewInboundOrderHasNullInternalOrderId() {
            InboundOrder order = InboundOrderFixtures.newReceivedOrder();

            assertThat(order.internalOrderId()).isNull();
            assertThat(order.failureReason()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 아이템 목록이 포함된다")
        void createNewInboundOrderHasItems() {
            InboundOrder order = InboundOrderFixtures.newReceivedOrder();

            assertThat(order.items()).hasSize(1);
        }

        @Test
        @DisplayName("신규 생성 시 주문 기본 정보가 올바르게 설정된다")
        void createNewInboundOrderHasCorrectBasicInfo() {
            InboundOrder order = InboundOrderFixtures.newReceivedOrder();

            assertThat(order.salesChannelId())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(order.sellerId()).isEqualTo(InboundOrderFixtures.DEFAULT_SELLER_ID);
            assertThat(order.externalOrderNo())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_EXTERNAL_ORDER_NO);
            assertThat(order.buyerName()).isEqualTo(InboundOrderFixtures.DEFAULT_BUYER_NAME);
            assertThat(order.totalPaymentAmount())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_TOTAL_PAYMENT_AMOUNT);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("RECEIVED 상태로 복원된 주문은 id.isNew()가 false이다")
        void reconstitutedOrderIsNotNew() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            assertThat(order.id().isNew()).isFalse();
            assertThat(order.idValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("CONVERTED 상태로 복원하면 internalOrderId가 설정된다")
        void reconstitutedAsConverted() {
            InboundOrder order = InboundOrderFixtures.convertedOrder();

            assertThat(order.status()).isEqualTo(InboundOrderStatus.CONVERTED);
            assertThat(order.internalOrderId())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_INTERNAL_ORDER_ID);
        }
    }

    @Nested
    @DisplayName("markPendingMapping() - PENDING_MAPPING 전이")
    class MarkPendingMappingTest {

        @Test
        @DisplayName("RECEIVED 상태에서 PENDING_MAPPING으로 전이한다")
        void markPendingMappingFromReceived() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();
            Instant now = CommonVoFixtures.now();

            order.markPendingMapping(now);

            assertThat(order.status()).isEqualTo(InboundOrderStatus.PENDING_MAPPING);
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태에서 PENDING_MAPPING으로 재전이 가능하다")
        void markPendingMappingFromPendingMapping() {
            InboundOrder order = InboundOrderFixtures.pendingMappingOrder();

            order.markPendingMapping(CommonVoFixtures.now());

            assertThat(order.status()).isEqualTo(InboundOrderStatus.PENDING_MAPPING);
        }

        @Test
        @DisplayName("MAPPED 상태에서 PENDING_MAPPING으로 전이하면 예외가 발생한다")
        void markPendingMappingFromMapped_ThrowsException() {
            InboundOrder order = InboundOrderFixtures.mappedOrder();

            assertThatThrownBy(() -> order.markPendingMapping(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("applyMapping() - MAPPED 전이")
    class ApplyMappingTest {

        @Test
        @DisplayName("RECEIVED 상태에서 매핑 적용 시 MAPPED로 전이한다")
        void applyMappingFromReceived() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            order.applyMapping(CommonVoFixtures.now());

            assertThat(order.status()).isEqualTo(InboundOrderStatus.MAPPED);
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태에서 매핑 적용 시 MAPPED로 전이한다")
        void applyMappingFromPendingMapping() {
            InboundOrder order = InboundOrderFixtures.pendingMappingOrder();

            order.applyMapping(CommonVoFixtures.now());

            assertThat(order.status()).isEqualTo(InboundOrderStatus.MAPPED);
        }

        @Test
        @DisplayName("MAPPED 상태에서 applyMapping 호출 시 예외가 발생한다")
        void applyMappingFromMapped_ThrowsException() {
            InboundOrder order = InboundOrderFixtures.mappedOrder();

            assertThatThrownBy(() -> order.applyMapping(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("markConverted() - CONVERTED 전이")
    class MarkConvertedTest {

        @Test
        @DisplayName("MAPPED 상태에서 변환 완료 처리하면 CONVERTED로 전이한다")
        void markConvertedFromMapped() {
            InboundOrder order = InboundOrderFixtures.mappedOrder();

            order.markConverted("ORDER-UUID-001", CommonVoFixtures.now());

            assertThat(order.status()).isEqualTo(InboundOrderStatus.CONVERTED);
            assertThat(order.internalOrderId()).isEqualTo("ORDER-UUID-001");
        }

        @Test
        @DisplayName("RECEIVED 상태에서 변환 완료 처리하면 예외가 발생한다")
        void markConvertedFromReceived_ThrowsException() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            assertThatThrownBy(() -> order.markConverted("ORDER-UUID-001", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("markFailed() - 실패 처리")
    class MarkFailedTest {

        @Test
        @DisplayName("어떤 상태에서도 실패 처리할 수 있다")
        void markFailedFromAnyStatus() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            order.markFailed("매핑 실패: 외부 상품을 찾을 수 없습니다", CommonVoFixtures.now());

            assertThat(order.status()).isEqualTo(InboundOrderStatus.FAILED);
            assertThat(order.failureReason()).isEqualTo("매핑 실패: 외부 상품을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("assignSellerId() - 셀러 ID 할당")
    class AssignSellerIdTest {

        @Test
        @DisplayName("셀러 ID를 할당한다")
        void assignSellerId() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            order.assignSellerId(999L);

            assertThat(order.sellerId()).isEqualTo(999L);
        }
    }

    @Nested
    @DisplayName("items() - 아이템 목록 불변성")
    class ItemsImmutabilityTest {

        @Test
        @DisplayName("items() 반환 리스트는 수정할 수 없다")
        void itemsListIsUnmodifiable() {
            InboundOrder order = InboundOrderFixtures.receivedOrder();

            assertThatThrownBy(() -> order.items().add(InboundOrderFixtures.newItem()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
