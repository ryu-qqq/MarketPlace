package com.ryuqq.marketplace.application.canonicaloption;

import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionValueResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.time.Instant;
import java.util.List;

/**
 * CanonicalOption Application Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CanonicalOptionQueryFixtures {

    private CanonicalOptionQueryFixtures() {}

    // ===== 상수 =====
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    // ===== CanonicalOptionGroupSearchParams Fixtures =====

    public static CanonicalOptionGroupSearchParams searchParams() {
        return new CanonicalOptionGroupSearchParams(
                null, null, null, defaultCommonSearchParams());
    }

    public static CanonicalOptionGroupSearchParams searchParams(Boolean active) {
        return new CanonicalOptionGroupSearchParams(
                active, null, null, defaultCommonSearchParams());
    }

    public static CanonicalOptionGroupSearchParams searchParams(
            String searchField, String searchWord) {
        return new CanonicalOptionGroupSearchParams(
                null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static CanonicalOptionGroupSearchParams searchParams(int page, int size) {
        return new CanonicalOptionGroupSearchParams(
                null, null, null, commonSearchParams(page, size));
    }

    public static CanonicalOptionGroupSearchParams searchParams(
            Boolean active, String searchField, String searchWord) {
        return new CanonicalOptionGroupSearchParams(
                active, searchField, searchWord, defaultCommonSearchParams());
    }

    // ===== CommonSearchParams Fixtures =====

    public static CommonSearchParams defaultCommonSearchParams() {
        return commonSearchParams(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(null, null, null, null, null, page, size);
    }

    // ===== Result Fixtures =====

    public static CanonicalOptionValueResult canonicalOptionValueResult(Long id) {
        return new CanonicalOptionValueResult(id, "RED", "빨강", "Red", 1);
    }

    public static CanonicalOptionGroupResult canonicalOptionGroupResult(Long id) {
        return new CanonicalOptionGroupResult(
                id,
                "COLOR",
                "색상",
                "Color",
                true,
                List.of(canonicalOptionValueResult(1L)),
                Instant.now());
    }

    public static CanonicalOptionGroupResult canonicalOptionGroupResult(
            Long id, List<CanonicalOptionValueResult> values) {
        return new CanonicalOptionGroupResult(
                id, "COLOR", "색상", "Color", true, values, Instant.now());
    }

    public static CanonicalOptionGroupPageResult canonicalOptionGroupPageResult() {
        return CanonicalOptionGroupPageResult.of(
                List.of(canonicalOptionGroupResult(1L)), DEFAULT_PAGE, DEFAULT_SIZE, 1L);
    }

    public static CanonicalOptionGroupPageResult canonicalOptionGroupPageResult(
            List<CanonicalOptionGroupResult> results, int page, int size, long totalElements) {
        return CanonicalOptionGroupPageResult.of(results, page, size, totalElements);
    }

    public static CanonicalOptionGroupPageResult emptyCanonicalOptionGroupPageResult() {
        return CanonicalOptionGroupPageResult.of(List.of(), DEFAULT_PAGE, DEFAULT_SIZE, 0L);
    }
}
