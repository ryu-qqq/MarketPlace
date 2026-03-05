package com.ryuqq.marketplace.adapter.out.client.setof.dto;

public record SetofSellerSyncRequest(
        Long sellerId,
        String sellerName,
        String displayName,
        String logoUrl,
        String description,
        boolean active) {}
