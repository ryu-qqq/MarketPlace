package com.ryuqq.marketplace.application.canonicaloption.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 캐노니컬 옵션 그룹 페이징 조회 결과 DTO. */
public record CanonicalOptionGroupPageResult(
        List<CanonicalOptionGroupResult> results, PageMeta pageMeta) {

    public static CanonicalOptionGroupPageResult of(
            List<CanonicalOptionGroupResult> results, PageMeta pageMeta) {
        return new CanonicalOptionGroupPageResult(results, pageMeta);
    }

    public static CanonicalOptionGroupPageResult of(
            List<CanonicalOptionGroupResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CanonicalOptionGroupPageResult(results, pageMeta);
    }

    public static CanonicalOptionGroupPageResult empty(int size) {
        return new CanonicalOptionGroupPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
