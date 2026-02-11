package com.ryuqq.marketplace.adapter.in.rest.canonicaloption;

import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.query.SearchCanonicalOptionGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionValueApiResponse;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionValueResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * CanonicalOption API 테스트 Fixtures.
 *
 * <p>CanonicalOption REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CanonicalOptionApiFixtures {

    private CanonicalOptionApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_GROUP_CODE = "SIZE";
    public static final String DEFAULT_GROUP_NAME_KO = "사이즈";
    public static final String DEFAULT_GROUP_NAME_EN = "Size";
    public static final String DEFAULT_VALUE_CODE = "SIZE_L";
    public static final String DEFAULT_VALUE_NAME_KO = "라지";
    public static final String DEFAULT_VALUE_NAME_EN = "Large";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23T10:30:00+09:00";

    // ===== SearchCanonicalOptionGroupsApiRequest =====

    public static SearchCanonicalOptionGroupsApiRequest searchRequest() {
        return new SearchCanonicalOptionGroupsApiRequest(
                null, null, null, null, null, 0, 20);
    }

    public static SearchCanonicalOptionGroupsApiRequest searchRequest(
            Boolean active, String searchField, String searchWord, int page, int size) {
        return new SearchCanonicalOptionGroupsApiRequest(
                active, searchField, searchWord, "createdAt", "DESC", page, size);
    }

    // ===== CanonicalOptionValueResult (Application) =====

    public static CanonicalOptionValueResult valueResult(Long id) {
        return new CanonicalOptionValueResult(
                id,
                DEFAULT_VALUE_CODE + "_" + id,
                DEFAULT_VALUE_NAME_KO + id,
                DEFAULT_VALUE_NAME_EN + id,
                id.intValue());
    }

    public static List<CanonicalOptionValueResult> valueResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> valueResult((long) i))
                .toList();
    }

    // ===== CanonicalOptionGroupResult (Application) =====

    public static CanonicalOptionGroupResult groupResult(Long id) {
        return new CanonicalOptionGroupResult(
                id,
                DEFAULT_GROUP_CODE + "_" + id,
                DEFAULT_GROUP_NAME_KO + id,
                DEFAULT_GROUP_NAME_EN + id,
                true,
                valueResults(2),
                DEFAULT_INSTANT);
    }

    public static CanonicalOptionGroupResult groupResult(Long id, String code, boolean active) {
        return new CanonicalOptionGroupResult(
                id, code, DEFAULT_GROUP_NAME_KO + id, DEFAULT_GROUP_NAME_EN + id, active,
                valueResults(2), DEFAULT_INSTANT);
    }

    public static List<CanonicalOptionGroupResult> groupResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                groupResult(
                                        (long) i, DEFAULT_GROUP_CODE + "_" + i, true))
                .toList();
    }

    public static CanonicalOptionGroupPageResult pageResult(int count, int page, int size) {
        List<CanonicalOptionGroupResult> results = groupResults(count);
        return CanonicalOptionGroupPageResult.of(results, page, size, count);
    }

    public static CanonicalOptionGroupPageResult emptyPageResult() {
        return CanonicalOptionGroupPageResult.empty(20);
    }

    // ===== CanonicalOptionValueApiResponse =====

    public static CanonicalOptionValueApiResponse valueApiResponse(Long id) {
        return new CanonicalOptionValueApiResponse(
                id,
                DEFAULT_VALUE_CODE + "_" + id,
                DEFAULT_VALUE_NAME_KO + id,
                DEFAULT_VALUE_NAME_EN + id,
                id.intValue());
    }

    public static List<CanonicalOptionValueApiResponse> valueApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> valueApiResponse((long) i))
                .toList();
    }

    // ===== CanonicalOptionGroupApiResponse =====

    public static CanonicalOptionGroupApiResponse groupApiResponse(Long id) {
        return new CanonicalOptionGroupApiResponse(
                id,
                DEFAULT_GROUP_CODE + "_" + id,
                DEFAULT_GROUP_NAME_KO + id,
                DEFAULT_GROUP_NAME_EN + id,
                true,
                valueApiResponses(2),
                DEFAULT_FORMATTED_TIME);
    }

    public static List<CanonicalOptionGroupApiResponse> groupApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> groupApiResponse((long) i))
                .toList();
    }
}
