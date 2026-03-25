package com.ryuqq.marketplace.adapter.out.client.setof.dto;

public record SetofSellerTokenRequest(String apiKey, String apiSecret) {
    public static SetofSellerTokenRequest of(String apiKey, String apiSecret) {
        return new SetofSellerTokenRequest(apiKey, apiSecret);
    }
}
