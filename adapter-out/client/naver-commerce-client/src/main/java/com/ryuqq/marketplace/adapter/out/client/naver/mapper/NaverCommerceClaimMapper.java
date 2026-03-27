package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeClaimInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnClaimInfo;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 클레임 매퍼.
 *
 * <p>NaverLastChangedStatus + NaverProductOrderDetail → ExternalClaimPayload 변환. lastChangedStatus의
 * claimType/claimStatus와 productOrder의 currentClaim 상세 정보를 결합합니다.
 */
@Component
public class NaverCommerceClaimMapper {

    /**
     * 클레임 변경 상태 목록과 상품주문 상세 목록을 ExternalClaimPayload 목록으로 변환합니다.
     *
     * @param changes claimType이 포함된 변경 상태 목록
     * @param details 상품주문 상세 목록
     * @return 외부 클레임 페이로드 목록
     */
    public List<ExternalClaimPayload> toExternalClaimPayloads(
            List<NaverLastChangedStatus> changes, List<NaverProductOrderDetail> details) {

        Map<String, NaverProductOrderDetail> detailMap =
                details.stream()
                        .collect(
                                Collectors.toMap(
                                        d -> d.productOrder().productOrderId(),
                                        d -> d,
                                        (a, b) -> b));

        return changes.stream()
                .filter(c -> c.claimType() != null)
                .map(change -> toClaimPayload(change, detailMap.get(change.productOrderId())))
                .filter(Objects::nonNull)
                .toList();
    }

    private ExternalClaimPayload toClaimPayload(
            NaverLastChangedStatus change, NaverProductOrderDetail detail) {

        if (detail == null) {
            return null;
        }

        NaverProductOrderDetail.ProductOrderInfo po = detail.productOrder();
        NaverClaimInfo claim = po.currentClaim();
        NaverExchangeClaimInfo exchange = detail.exchange();
        NaverReturnClaimInfo returnInfo = detail.returnInfo();

        String claimType = change.claimType();

        String claimId = claim != null ? claim.claimId() : po.claimId();
        String externalReasonCode;
        String claimDetailedReason;
        Integer requestQuantity;
        String requestChannel;
        String collectDeliveryCompany;
        String collectTrackingNumber;
        String collectStatus;
        String holdbackStatus;
        String holdbackReason;

        if ("EXCHANGE".equals(claimType) && exchange != null) {
            externalReasonCode = exchange.exchangeReason();
            claimDetailedReason = exchange.exchangeDetailedReason();
            holdbackStatus = exchange.holdbackStatus();
            holdbackReason = exchange.holdbackReason();
            requestQuantity = exchange.requestQuantity();
            requestChannel = exchange.requestChannel();
            collectDeliveryCompany = claim != null ? claim.collectDeliveryCompany() : null;
            collectTrackingNumber = claim != null ? claim.collectTrackingNumber() : null;
            collectStatus = claim != null ? claim.collectStatus() : null;
        } else if ("RETURN".equals(claimType) && returnInfo != null) {
            externalReasonCode = returnInfo.returnReason();
            claimDetailedReason = returnInfo.returnDetailedReason();
            holdbackStatus = returnInfo.holdbackStatus();
            holdbackReason = returnInfo.holdbackReason();
            requestQuantity = returnInfo.requestQuantity();
            requestChannel = returnInfo.requestChannel();
            collectDeliveryCompany = returnInfo.collectDeliveryCompany();
            collectTrackingNumber = returnInfo.collectTrackingNumber();
            collectStatus = returnInfo.collectStatus();
        } else {
            externalReasonCode = claim != null ? claim.claimRequestReason() : null;
            claimDetailedReason = claim != null ? claim.claimRequestDetailContent() : null;
            holdbackStatus = claim != null ? claim.holdbackStatus() : null;
            holdbackReason = claim != null ? claim.holdbackReason() : null;
            requestQuantity = claim != null ? claim.requestQuantity() : null;
            requestChannel = claim != null ? claim.requestChannel() : null;
            collectDeliveryCompany = claim != null ? claim.collectDeliveryCompany() : null;
            collectTrackingNumber = claim != null ? claim.collectTrackingNumber() : null;
            collectStatus = claim != null ? claim.collectStatus() : null;
        }

        String reDeliveryCompany = claim != null ? claim.reDeliveryCompany() : null;
        String reDeliveryTrackingNumber = claim != null ? claim.reDeliveryTrackingNumber() : null;
        String reDeliveryStatus = claim != null ? claim.reDeliveryStatus() : null;
        Instant claimRequestDate = claim != null ? parseInstant(claim.claimRequestDate()) : null;

        // 클레임 배송비: 교환은 claimDeliveryFeeDemandAmount, 반품은 deliveryFeeAmount
        Integer claimDeliveryFeeAmount = null;
        if ("EXCHANGE".equals(claimType) && exchange != null) {
            claimDeliveryFeeAmount = exchange.claimDeliveryFeeDemandAmount();
        }
        if (claimDeliveryFeeAmount == null) {
            claimDeliveryFeeAmount = po.deliveryFeeAmount();
        }

        return new ExternalClaimPayload(
                change.orderId(),
                change.productOrderId(),
                claimType,
                change.claimStatus(),
                claimId,
                po.productOrderStatus(),
                externalReasonCode,
                claimDetailedReason,
                externalReasonCode,
                requestQuantity,
                requestChannel,
                collectDeliveryCompany,
                collectTrackingNumber,
                collectStatus,
                reDeliveryCompany,
                reDeliveryTrackingNumber,
                reDeliveryStatus,
                holdbackStatus,
                holdbackReason,
                claimRequestDate,
                parseInstant(change.lastChangedDate()),
                claimDeliveryFeeAmount,
                po.shippingFeeType(),
                po.productOption());
    }

    private Instant parseInstant(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return OffsetDateTime.parse(dateStr).toInstant();
    }
}
