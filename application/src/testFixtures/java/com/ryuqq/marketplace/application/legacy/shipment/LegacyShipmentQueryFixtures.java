package com.ryuqq.marketplace.application.legacy.shipment;

import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.time.Instant;
import java.util.List;

/**
 * LegacyShipment Application Query 테스트 Fixtures.
 *
 * <p>레거시 택배사 코드 조회 관련 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyShipmentQueryFixtures {

    private LegacyShipmentQueryFixtures() {}

    // ===== LegacyCommonCode Fixtures =====

    public static LegacyCommonCode legacyCommonCode(
            Long id, String codeDetail, String displayName) {
        return LegacyCommonCode.reconstitute(id, 2L, codeDetail, displayName, 1);
    }

    public static LegacyCommonCode legacyCommonCodeWithNullDisplayOrder(
            Long id, String codeDetail, String displayName) {
        return LegacyCommonCode.reconstitute(id, 2L, codeDetail, displayName, null);
    }

    public static List<LegacyCommonCode> legacyCommonCodeList() {
        return List.of(
                legacyCommonCode(1L, "CJ", "CJ대한통운"),
                legacyCommonCode(2L, "HANJIN", "한진택배"),
                legacyCommonCode(3L, "LOTTE", "롯데택배"));
    }

    public static List<LegacyCommonCode> emptyLegacyCommonCodeList() {
        return List.of();
    }

    // ===== CommonCode Fixtures =====

    public static CommonCode commonCode(Long id, String codeDetail, String displayName) {
        Instant now = Instant.parse("2026-03-20T00:00:00Z");
        return CommonCode.reconstitute(
                CommonCodeId.of(id),
                CommonCodeTypeId.of(2L),
                codeDetail,
                displayName,
                1,
                true,
                null,
                now,
                now);
    }

    public static List<CommonCode> commonCodeList() {
        return List.of(
                commonCode(1L, "CJ", "CJ대한통운"),
                commonCode(2L, "HANJIN", "한진택배"),
                commonCode(3L, "LOTTE", "롯데택배"));
    }
}
