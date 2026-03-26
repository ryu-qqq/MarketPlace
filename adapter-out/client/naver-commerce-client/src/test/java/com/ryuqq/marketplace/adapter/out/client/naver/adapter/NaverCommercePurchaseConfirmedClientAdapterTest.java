package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NaverCommercePurchaseConfirmedClientAdapter 단위 테스트")
class NaverCommercePurchaseConfirmedClientAdapterTest {

    @InjectMocks private NaverCommercePurchaseConfirmedClientAdapter sut;

    @Mock private NaverCommerceOrderClientAdapter orderClientAdapter;

    private static final long SALES_CHANNEL_ID = 1L;
    private static final long SHOP_ID = 100L;
    private static final Instant FROM_TIME = Instant.parse("2026-03-24T10:00:00Z");
    private static final Instant TO_TIME = Instant.parse("2026-03-25T10:00:00Z");
    private static final ShopCredentials CREDENTIALS =
            ShopCredentials.of("NAVER", "api-key", "api-secret", "access-token", "vendor-001");

    @Nested
    @DisplayName("supports() - 채널 코드 지원 여부")
    class SupportsTest {

        @Test
        @DisplayName("NAVER 채널이면 true를 반환한다")
        void supports_NaverChannel_ReturnsTrue() {
            assertThat(sut.supports("NAVER")).isTrue();
        }

        @Test
        @DisplayName("SELLIC 채널이면 false를 반환한다")
        void supports_SellicChannel_ReturnsFalse() {
            assertThat(sut.supports("SELLIC")).isFalse();
        }
    }

    @Nested
    @DisplayName("fetchPurchaseConfirmedProductOrderIds() - 구매확정 상품주문번호 조회")
    class FetchPurchaseConfirmedProductOrderIdsTest {

        @Test
        @DisplayName("PURCHASE_DECIDED + claimType=null인 이벤트만 필터링하여 반환한다")
        void fetch_PurchaseDecidedOnly_FiltersCorrectly() {
            // given
            NaverLastChangedStatus purchaseDecided1 =
                    new NaverLastChangedStatus(
                            "ORD-001",
                            "PO-001",
                            "PURCHASE_DECIDED",
                            null,
                            null,
                            "PURCHASE_DECIDED",
                            null,
                            null,
                            null,
                            null);
            NaverLastChangedStatus purchaseDecided2 =
                    new NaverLastChangedStatus(
                            "ORD-002",
                            "PO-002",
                            "PURCHASE_DECIDED",
                            null,
                            null,
                            "PURCHASE_DECIDED",
                            null,
                            null,
                            null,
                            null);
            NaverLastChangedStatus claimEvent =
                    new NaverLastChangedStatus(
                            "ORD-003",
                            "PO-003",
                            "CLAIM_REQUESTED",
                            null,
                            null,
                            "CLAIM_REQUESTED",
                            "CANCEL",
                            "CANCEL_REQUEST",
                            null,
                            null);
            NaverLastChangedStatus purchaseWithClaim =
                    new NaverLastChangedStatus(
                            "ORD-004",
                            "PO-004",
                            "PURCHASE_DECIDED",
                            null,
                            null,
                            "PURCHASE_DECIDED",
                            "RETURN",
                            null,
                            null,
                            null);

            NaverLastChangedStatusesResponse response =
                    new NaverLastChangedStatusesResponse(
                            new NaverLastChangedStatusesResponse.Data(
                                    List.of(
                                            purchaseDecided1,
                                            purchaseDecided2,
                                            claimEvent,
                                            purchaseWithClaim),
                                    4,
                                    null));

            given(orderClientAdapter.getLastChangedStatusesAll(FROM_TIME, TO_TIME, null))
                    .willReturn(response);

            // when
            List<String> result =
                    sut.fetchPurchaseConfirmedProductOrderIds(
                            SALES_CHANNEL_ID, SHOP_ID, CREDENTIALS, FROM_TIME, TO_TIME);

            // then
            assertThat(result).containsExactly("PO-001", "PO-002");
        }

        @Test
        @DisplayName("클레임 이벤트(CLAIM_REQUESTED)는 필터링되어 제외된다")
        void fetch_ClaimEvents_FilteredOut() {
            // given
            NaverLastChangedStatus claimEvent =
                    new NaverLastChangedStatus(
                            "ORD-001",
                            "PO-001",
                            "CLAIM_REQUESTED",
                            null,
                            null,
                            "CLAIM_REQUESTED",
                            "CANCEL",
                            "CANCEL_REQUEST",
                            null,
                            null);

            NaverLastChangedStatusesResponse response =
                    new NaverLastChangedStatusesResponse(
                            new NaverLastChangedStatusesResponse.Data(
                                    List.of(claimEvent), 1, null));

            given(orderClientAdapter.getLastChangedStatusesAll(FROM_TIME, TO_TIME, null))
                    .willReturn(response);

            // when
            List<String> result =
                    sut.fetchPurchaseConfirmedProductOrderIds(
                            SALES_CHANNEL_ID, SHOP_ID, CREDENTIALS, FROM_TIME, TO_TIME);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 응답이면 빈 목록을 반환한다")
        void fetch_EmptyResponse_ReturnsEmptyList() {
            // given
            NaverLastChangedStatusesResponse response =
                    new NaverLastChangedStatusesResponse(
                            new NaverLastChangedStatusesResponse.Data(List.of(), 0, null));

            given(orderClientAdapter.getLastChangedStatusesAll(FROM_TIME, TO_TIME, null))
                    .willReturn(response);

            // when
            List<String> result =
                    sut.fetchPurchaseConfirmedProductOrderIds(
                            SALES_CHANNEL_ID, SHOP_ID, CREDENTIALS, FROM_TIME, TO_TIME);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 응답이면 빈 목록을 반환한다")
        void fetch_NullResponse_ReturnsEmptyList() {
            // given
            given(orderClientAdapter.getLastChangedStatusesAll(FROM_TIME, TO_TIME, null))
                    .willReturn(null);

            // when
            List<String> result =
                    sut.fetchPurchaseConfirmedProductOrderIds(
                            SALES_CHANNEL_ID, SHOP_ID, CREDENTIALS, FROM_TIME, TO_TIME);

            // then
            assertThat(result).isEmpty();
        }
    }
}
