package com.ryuqq.marketplace.application.shop.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** Shop 페이징 조회 결과 DTO. */
public record ShopPageResult(List<ShopResult> results, PageMeta pageMeta) {

    public static ShopPageResult of(List<ShopResult> results, PageMeta pageMeta) {
        return new ShopPageResult(results, pageMeta);
    }

    public static ShopPageResult of(
            List<ShopResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ShopPageResult(results, pageMeta);
    }

    public static ShopPageResult empty(int size) {
        return new ShopPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
