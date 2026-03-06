package com.ryuqq.marketplace.adapter.out.client.setof.dto;

public record SetofSellerAddressSyncRequest(
        Long id,
        Long sellerId,
        String addressType,
        String addressName,
        String zipCode,
        String roadAddress,
        String detailAddress,
        boolean defaultAddress) {}
