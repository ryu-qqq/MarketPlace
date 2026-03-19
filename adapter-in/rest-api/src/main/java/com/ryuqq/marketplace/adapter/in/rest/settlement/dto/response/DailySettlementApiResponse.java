package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response;

/**
 * 일별 정산 항목 API 응답.
 *
 * <p>스펙의 DailySettlementItem에 대응. 채울 수 없는 필드는 0/null로 처리.
 */
public record DailySettlementApiResponse(
        String settlementDay,
        String settlementCompleteDay,
        long orderCount,
        long ourMallOrderCount,
        long externalMallOrderCount,
        int totalSalesAmount,
        DiscountApiResponse discount,
        MileageApiResponse mileage,
        FeeApiResponse fee,
        int expectedSettlementAmount,
        int settlementAmount) {

    public record DiscountApiResponse(int totalAmount, int sellerAmount, int platformAmount) {
        public static DiscountApiResponse zero() {
            return new DiscountApiResponse(0, 0, 0);
        }
    }

    public record MileageApiResponse(int totalAmount, int sellerAmount, int platformAmount) {
        public static MileageApiResponse zero() {
            return new MileageApiResponse(0, 0, 0);
        }
    }

    public record FeeApiResponse(int totalAmount) {}
}
