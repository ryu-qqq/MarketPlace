package com.ryuqq.marketplace.domain.inboundproduct.vo;

import java.util.Objects;

/** InboundProduct 변경 감지 결과 VO. */
public record InboundProductDiff(
        boolean nameChanged,
        boolean priceChanged,
        boolean brandCodeChanged,
        boolean categoryCodeChanged,
        boolean rawPayloadChanged) {

    /** 변경 사항이 하나라도 있는지 확인. */
    public boolean hasAnyChange() {
        return nameChanged
                || priceChanged
                || brandCodeChanged
                || categoryCodeChanged
                || rawPayloadChanged;
    }

    /** 브랜드/카테고리 코드 변경 시 재매핑 필요 여부. */
    public boolean requiresRemapping() {
        return brandCodeChanged || categoryCodeChanged;
    }

    /** 두 값의 동등성 비교 유틸리티. */
    public static boolean changed(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }

    public static boolean changed(int oldValue, int newValue) {
        return oldValue != newValue;
    }
}
