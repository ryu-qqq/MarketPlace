package com.ryuqq.marketplace.domain.order.vo;

import java.time.Instant;

/** 클레임 처리 상태. */
public record FulfillmentStatus(
        FulfillmentType type, String status, int qty, Instant requestedAt) {}
