package com.ryuqq.marketplace.domain.outboundproductimage.vo;

import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.time.Instant;
import java.util.List;

/**
 * OutboundProductImage 변경 비교 결과.
 *
 * <p>기존 캐시된 이미지와 현재 이미지를 originUrl + imageType 기준으로 비교하여 추가/삭제/유지 목록을 제공합니다.
 */
public record OutboundProductImageDiff(
        List<OutboundProductImage> added,
        List<OutboundProductImage> removed,
        List<OutboundProductImage> retained,
        Instant occurredAt) {

    public OutboundProductImageDiff {
        added = List.copyOf(added);
        removed = List.copyOf(removed);
        retained = List.copyOf(retained);
    }

    public static OutboundProductImageDiff of(
            List<OutboundProductImage> added,
            List<OutboundProductImage> removed,
            List<OutboundProductImage> retained,
            Instant occurredAt) {
        return new OutboundProductImageDiff(added, removed, retained, occurredAt);
    }

    /** 변경 사항이 없는지 확인. */
    public boolean hasNoChanges() {
        return added.isEmpty() && removed.isEmpty();
    }
}
