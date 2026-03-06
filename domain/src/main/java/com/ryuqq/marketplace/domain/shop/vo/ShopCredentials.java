package com.ryuqq.marketplace.domain.shop.vo;

/** Shop의 외부 채널 API 자격증명 Value Object. */
public record ShopCredentials(
        String channelCode, String apiKey, String apiSecret, String accessToken, String vendorId) {

    public static ShopCredentials of(
            String channelCode,
            String apiKey,
            String apiSecret,
            String accessToken,
            String vendorId) {
        return new ShopCredentials(channelCode, apiKey, apiSecret, accessToken, vendorId);
    }

    public static ShopCredentials empty() {
        return new ShopCredentials(null, null, null, null, null);
    }
}
