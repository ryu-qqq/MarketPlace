package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderOrder;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceClaimMapper 단위 테스트")
class NaverCommerceClaimMapperTest {

    private final NaverCommerceClaimMapper sut = new NaverCommerceClaimMapper();

    // ── 헬퍼 메서드 ──

    private NaverLastChangedStatus createChange(
            String orderId, String productOrderId, String claimType, String claimStatus) {
        return new NaverLastChangedStatus(
                orderId,
                productOrderId,
                "CLAIM_REQUESTED",
                null,
                "2026-03-20T10:00:00+09:00",
                "PAYED",
                claimType,
                claimStatus,
                null,
                null);
    }

    private NaverProductOrderDetail createDetail(
            String productOrderId, NaverClaimInfo currentClaim) {
        var order =
                new NaverProductOrderOrder(
                        "ORD001", null, null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null);
        var po =
                new NaverProductOrderDetail.ProductOrderInfo(
                        productOrderId, // 1
                        "PAYED", // 2
                        null,
                        null,
                        "CLM001", // 3-5
                        null,
                        null,
                        null, // 6-8
                        null,
                        null, // 9-10
                        "PROD001",
                        null, // 11-12
                        "상품",
                        null, // 13-14
                        null,
                        null, // 15-16
                        null,
                        null, // 17-18
                        null,
                        null, // 19-20
                        1,
                        null,
                        null, // 21-23
                        50000,
                        50000, // 24-25
                        null,
                        null, // 26-27
                        0,
                        50000, // 28-29
                        null,
                        null, // 30-31
                        null,
                        null,
                        null, // 32-34
                        null,
                        null,
                        null, // 35-37
                        null,
                        null,
                        null, // 38-40
                        null,
                        null, // 41-42
                        null,
                        null,
                        null,
                        null, // 43-46
                        null,
                        null,
                        null, // 47-49
                        null, // 50 shippingAddress
                        null, // 51 shippingMemo
                        null, // 52 freeGift
                        currentClaim, // 53 currentClaim
                        null); // 54 completedClaims
        return new NaverProductOrderDetail(order, po, null);
    }

    private NaverClaimInfo createClaimInfo() {
        return new NaverClaimInfo(
                "RETURN",
                "CLM001",
                "RETURN_REQUEST",
                "2026-03-20T09:00:00+09:00",
                "BUYER",
                "단순변심",
                "사이즈가 안 맞아요",
                1,
                null,
                null,
                null,
                null,
                null,
                null,
                "CJGLS",
                "1234567890",
                "COLLECTING",
                null,
                null,
                null);
    }

    @Nested
    @DisplayName("toExternalClaimPayloads()")
    class ToExternalClaimPayloads {

        @Test
        @DisplayName("클레임 변경 상태와 상세 정보를 결합하여 변환한다")
        void combinesChangeAndDetail() {
            var change = createChange("ORD001", "PO001", "RETURN", "RETURN_REQUEST");
            var detail = createDetail("PO001", createClaimInfo());

            List<ExternalClaimPayload> result =
                    sut.toExternalClaimPayloads(List.of(change), List.of(detail));

            assertThat(result).hasSize(1);
            var payload = result.get(0);
            assertThat(payload.externalOrderId()).isEqualTo("ORD001");
            assertThat(payload.externalProductOrderId()).isEqualTo("PO001");
            assertThat(payload.claimType()).isEqualTo("RETURN");
            assertThat(payload.claimStatus()).isEqualTo("RETURN_REQUEST");
            assertThat(payload.claimId()).isEqualTo("CLM001");
            assertThat(payload.claimReason()).isEqualTo("단순변심");
            assertThat(payload.claimDetailedReason()).isEqualTo("사이즈가 안 맞아요");
            assertThat(payload.externalReasonCode()).isEqualTo("단순변심");
            assertThat(payload.requestQuantity()).isEqualTo(1);
            assertThat(payload.requestChannel()).isEqualTo("BUYER");
            assertThat(payload.collectDeliveryCompany()).isEqualTo("CJGLS");
            assertThat(payload.collectTrackingNumber()).isEqualTo("1234567890");
            assertThat(payload.collectStatus()).isEqualTo("COLLECTING");
            assertThat(payload.claimRequestDate()).isNotNull();
            assertThat(payload.lastChangedDate()).isNotNull();
        }

        @Test
        @DisplayName("claimType이 null인 변경 상태는 필터링된다")
        void filtersNonClaimChanges() {
            var change = createChange("ORD001", "PO001", null, null);
            var detail = createDetail("PO001", null);

            List<ExternalClaimPayload> result =
                    sut.toExternalClaimPayloads(List.of(change), List.of(detail));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("매칭되는 상세 정보가 없으면 해당 클레임은 제외된다")
        void noMatchingDetailExcluded() {
            var change = createChange("ORD001", "PO999", "CANCEL", "CANCEL_REQUEST");

            List<ExternalClaimPayload> result =
                    sut.toExternalClaimPayloads(List.of(change), List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("currentClaim이 없으면 productOrder의 claimId를 사용한다")
        void fallsBackToProductOrderClaimId() {
            var change = createChange("ORD001", "PO001", "CANCEL", "CANCEL_DONE");
            var detail = createDetail("PO001", null);

            List<ExternalClaimPayload> result =
                    sut.toExternalClaimPayloads(List.of(change), List.of(detail));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).claimId()).isEqualTo("CLM001");
            assertThat(result.get(0).claimReason()).isNull();
            assertThat(result.get(0).externalReasonCode()).isNull();
        }
    }
}
