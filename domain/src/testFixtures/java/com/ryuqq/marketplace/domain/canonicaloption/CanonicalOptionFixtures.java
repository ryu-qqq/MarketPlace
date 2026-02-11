package com.ryuqq.marketplace.domain.canonicaloption;

import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueName;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * CanonicalOption 도메인 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CanonicalOptionFixtures {

    private CanonicalOptionFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_GROUP_CODE = "COLOR";
    public static final String DEFAULT_GROUP_NAME_KO = "색상";
    public static final String DEFAULT_GROUP_NAME_EN = "Color";
    public static final String DEFAULT_VALUE_CODE = "RED";
    public static final String DEFAULT_VALUE_NAME_KO = "빨강";
    public static final String DEFAULT_VALUE_NAME_EN = "Red";

    // ===== CanonicalOptionGroup Aggregate Fixtures =====

    public static CanonicalOptionGroup activeCanonicalOptionGroup() {
        return activeCanonicalOptionGroup(1L);
    }

    public static CanonicalOptionGroup activeCanonicalOptionGroup(Long id) {
        return CanonicalOptionGroup.reconstitute(
                CanonicalOptionGroupId.of(id),
                CanonicalOptionGroupCode.of(DEFAULT_GROUP_CODE),
                CanonicalOptionGroupName.of(DEFAULT_GROUP_NAME_KO, DEFAULT_GROUP_NAME_EN),
                true,
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CanonicalOptionGroup inactiveCanonicalOptionGroup() {
        return CanonicalOptionGroup.reconstitute(
                CanonicalOptionGroupId.of(2L),
                CanonicalOptionGroupCode.of(DEFAULT_GROUP_CODE),
                CanonicalOptionGroupName.of(DEFAULT_GROUP_NAME_KO, DEFAULT_GROUP_NAME_EN),
                false,
                List.of(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CanonicalOptionGroup canonicalOptionGroupWithValues(
            Long id, List<CanonicalOptionValue> values) {
        return CanonicalOptionGroup.reconstitute(
                CanonicalOptionGroupId.of(id),
                CanonicalOptionGroupCode.of(DEFAULT_GROUP_CODE),
                CanonicalOptionGroupName.of(DEFAULT_GROUP_NAME_KO, DEFAULT_GROUP_NAME_EN),
                true,
                values,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== CanonicalOptionValue Fixtures =====

    public static CanonicalOptionValue canonicalOptionValue() {
        return canonicalOptionValue(1L);
    }

    public static CanonicalOptionValue canonicalOptionValue(Long id) {
        return CanonicalOptionValue.reconstitute(
                CanonicalOptionValueId.of(id),
                CanonicalOptionValueCode.of(DEFAULT_VALUE_CODE),
                CanonicalOptionValueName.of(DEFAULT_VALUE_NAME_KO, DEFAULT_VALUE_NAME_EN),
                1);
    }

    public static CanonicalOptionValue canonicalOptionValue(Long id, int sortOrder) {
        return CanonicalOptionValue.reconstitute(
                CanonicalOptionValueId.of(id),
                CanonicalOptionValueCode.of(DEFAULT_VALUE_CODE),
                CanonicalOptionValueName.of(DEFAULT_VALUE_NAME_KO, DEFAULT_VALUE_NAME_EN),
                sortOrder);
    }

    public static CanonicalOptionValue canonicalOptionValue(
            Long id, String code, String nameKo, int sortOrder) {
        return CanonicalOptionValue.reconstitute(
                CanonicalOptionValueId.of(id),
                CanonicalOptionValueCode.of(code),
                CanonicalOptionValueName.of(nameKo, null),
                sortOrder);
    }

    public static List<CanonicalOptionValue> canonicalOptionValues() {
        return List.of(
                canonicalOptionValue(1L, 1),
                canonicalOptionValue(2L, 2),
                canonicalOptionValue(3L, 3));
    }

    // ===== Time Helper =====

    public static Instant now() {
        return Instant.now();
    }
}
