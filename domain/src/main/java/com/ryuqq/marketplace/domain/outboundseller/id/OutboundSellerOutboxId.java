package com.ryuqq.marketplace.domain.outboundseller.id;

public record OutboundSellerOutboxId(Long value) {

    public static OutboundSellerOutboxId of(Long value) {
        return new OutboundSellerOutboxId(value);
    }

    public static OutboundSellerOutboxId forNew() {
        return new OutboundSellerOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
