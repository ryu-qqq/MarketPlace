package com.ryuqq.marketplace.application.legacyconversion.internal;

import org.springframework.stereotype.Component;

/**
 * 레거시 주문 채널 식별기.
 *
 * <p>레거시 주문의 EXTERNAL_ORDER_PK_ID 패턴과 site 정보를 기반으로 salesChannelId를 식별합니다.
 * 외부 의존성(DB/API 호출) 없는 순수 비즈니스 로직 컴포넌트입니다.
 */
@Component
public class LegacyOrderChannelResolver {

    /**
     * 레거시 주문의 채널을 식별합니다.
     *
     * @param externalOrderPkId    external_order.EXTERNAL_ORDER_PK_ID (nullable - 자사몰이면 null)
     * @param externalSiteId       external_order.SITE_ID (nullable)
     * @param interlockingSiteName interlocking_order.SITE_NAME (nullable)
     * @return 채널 식별 결과
     */
    public ChannelResolution resolve(
            String externalOrderPkId,
            Long externalSiteId,
            String interlockingSiteName) {

        // Case 1: external_order 없음 → 자사몰
        if (externalOrderPkId == null) {
            return new ChannelResolution(1L, "SET_OF");
        }

        // Case 2: site_id별 분기
        if (externalSiteId != null) {
            return switch (externalSiteId.intValue()) {
                case 1 -> new ChannelResolution(15L, "OCO");
                case 2 -> resolveSellicChannel(externalOrderPkId);
                case 8 -> new ChannelResolution(12L, "BUYMA");
                case 9 -> new ChannelResolution(13L, "LF");
                default -> new ChannelResolution(1L, "SET_OF");
            };
        }

        // Case 3: interlocking_order만 있는 경우
        if (interlockingSiteName != null) {
            return switch (interlockingSiteName) {
                case "OCO" -> new ChannelResolution(15L, "OCO");
                case "SEWON" -> resolveSellicChannel(externalOrderPkId);
                default -> new ChannelResolution(1L, "SET_OF");
            };
        }

        return new ChannelResolution(1L, "SET_OF");
    }

    /**
     * externalOrderNo를 결정합니다.
     *
     * <ul>
     *   <li>자사몰: 레거시 orderId 그대로
     *   <li>외부몰: EXTERNAL_ORDER_PK_ID
     * </ul>
     *
     * @param externalOrderPkId external_order.EXTERNAL_ORDER_PK_ID (nullable)
     * @param legacyOrderId     레거시 주문 ID
     * @return 결정된 externalOrderNo
     */
    public String resolveExternalOrderNo(String externalOrderPkId, long legacyOrderId) {
        if (externalOrderPkId == null) {
            return String.valueOf(legacyOrderId);
        }
        return externalOrderPkId;
    }

    /**
     * SELLIC 경유 주문의 실제 채널을 EXTERNAL_ORDER_PK_ID 패턴으로 식별합니다.
     *
     * <ul>
     *   <li>OD 시작 → SSF SHOP (salesChannelId=14)
     *   <li>8자리 순수 숫자 → LF (salesChannelId=13)
     *   <li>그 외 → 네이버 스마트스토어 (salesChannelId=2, SELLIC 경유 주문의 98%가 네이버)
     * </ul>
     */
    private ChannelResolution resolveSellicChannel(String externalOrderPkId) {
        if (externalOrderPkId == null) {
            return new ChannelResolution(2L, "NAVER");
        }

        if (externalOrderPkId.startsWith("OD")) {
            return new ChannelResolution(14L, "SSF");
        }

        if (externalOrderPkId.matches("^\\d{8}$")) {
            return new ChannelResolution(13L, "LF");
        }

        return new ChannelResolution(2L, "NAVER");
    }

    /**
     * 채널 식별 결과.
     *
     * @param salesChannelId 내부 채널 ID
     * @param channelName    채널명
     */
    public record ChannelResolution(long salesChannelId, String channelName) {}
}
