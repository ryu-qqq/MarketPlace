package com.ryuqq.marketplace.application.legacy.commoncode;

import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;

/**
 * LegacyCommonCode Application Query 테스트 Fixtures.
 *
 * <p>레거시 공통 코드 조회 관련 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyCommonCodeQueryFixtures {

    private LegacyCommonCodeQueryFixtures() {}

    // ===== LegacyCommonCode 단건 Fixtures =====

    public static LegacyCommonCode legacyCommonCode(
            Long id, Long codeGroupId, String codeDetail, String displayName, Integer displayOrder) {
        return LegacyCommonCode.reconstitute(id, codeGroupId, codeDetail, displayName, displayOrder);
    }

    public static LegacyCommonCode legacyCommonCode(
            Long id, Long codeGroupId, String codeDetail, String displayName) {
        return LegacyCommonCode.reconstitute(id, codeGroupId, codeDetail, displayName, 1);
    }

    public static LegacyCommonCode legacyCommonCodeWithNullDisplayOrder(
            Long id, Long codeGroupId, String codeDetail, String displayName) {
        return LegacyCommonCode.reconstitute(id, codeGroupId, codeDetail, displayName, null);
    }

    // ===== LegacyCommonCode 목록 Fixtures =====

    public static List<LegacyCommonCode> legacyCommonCodeList(Long codeGroupId) {
        return List.of(
                legacyCommonCode(1L, codeGroupId, "CODE_A", "코드A 표시명"),
                legacyCommonCode(2L, codeGroupId, "CODE_B", "코드B 표시명"),
                legacyCommonCode(3L, codeGroupId, "CODE_C", "코드C 표시명"));
    }

    public static List<LegacyCommonCode> emptyLegacyCommonCodeList() {
        return List.of();
    }
}
