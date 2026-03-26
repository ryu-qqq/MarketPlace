package com.ryuqq.marketplace.domain.cancel.vo;

import java.util.List;

/** 취소 유형. */
public enum CancelType {
    BUYER_CANCEL,
    SELLER_CANCEL;

    public static List<CancelType> fromStringList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream().map(CancelType::valueOf).toList();
    }
}
