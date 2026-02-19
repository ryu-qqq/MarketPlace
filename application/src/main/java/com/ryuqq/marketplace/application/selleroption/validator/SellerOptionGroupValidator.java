package com.ryuqq.marketplace.application.selleroption.validator;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 셀러 옵션 그룹 검증기.
 *
 * <p>1. 도메인 불변식 검증: 그룹 수, 빈 값, 중복 이름 (VO 위임)
 *
 * <p>2. 외부 FK 검증: canonicalOptionGroupId, canonicalOptionValueId 존재 및 소속 검증
 */
@Component
public class SellerOptionGroupValidator {

    private final CanonicalOptionGroupQueryPort canonicalOptionGroupQueryPort;

    public SellerOptionGroupValidator(CanonicalOptionGroupQueryPort canonicalOptionGroupQueryPort) {
        this.canonicalOptionGroupQueryPort = canonicalOptionGroupQueryPort;
    }

    /** 전체 검증: 도메인 불변식 + 외부 FK. */
    public void validate(SellerOptionGroups optionGroups, OptionType optionType) {
        optionGroups.validateStructure(optionType);
        validateCanonicalReferences(optionGroups);
    }

    /** entry 기반 SellerOptionGroupUpdateData에서 canonical 참조 검증. */
    public void validateCanonicalReferences(SellerOptionGroupUpdateData updateData) {
        List<CanonicalOptionGroupId> canonicalGroupIds =
                updateData.groupEntries().stream()
                        .map(SellerOptionGroupUpdateData.GroupEntry::canonicalOptionGroupId)
                        .filter(Objects::nonNull)
                        .map(CanonicalOptionGroupId::of)
                        .toList();

        if (canonicalGroupIds.isEmpty()) {
            return;
        }

        Map<Long, CanonicalOptionGroup> canonicalGroupMap =
                canonicalOptionGroupQueryPort.findByIds(canonicalGroupIds).stream()
                        .collect(
                                Collectors.toMap(
                                        CanonicalOptionGroup::idValue, Function.identity()));

        for (SellerOptionGroupUpdateData.GroupEntry group : updateData.groupEntries()) {
            Long canonicalGroupIdValue = group.canonicalOptionGroupId();
            if (canonicalGroupIdValue == null) {
                continue;
            }

            CanonicalOptionGroup canonicalGroup = canonicalGroupMap.get(canonicalGroupIdValue);
            if (canonicalGroup == null) {
                throw new IllegalArgumentException(
                        "존재하지 않는 캐노니컬 옵션 그룹입니다: " + canonicalGroupIdValue);
            }

            Set<Long> canonicalValueIds =
                    canonicalGroup.values().stream()
                            .map(CanonicalOptionValue::idValue)
                            .collect(Collectors.toSet());

            for (SellerOptionGroupUpdateData.ValueEntry value : group.values()) {
                Long canonicalValueIdValue = value.canonicalOptionValueId();
                if (canonicalValueIdValue != null
                        && !canonicalValueIds.contains(canonicalValueIdValue)) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "캐노니컬 옵션 값 %d은(는) 캐노니컬 옵션 그룹 %d에 속하지 않습니다",
                                    canonicalValueIdValue, canonicalGroupIdValue));
                }
            }
        }
    }

    /** SellerOptionGroups 기반 canonical 참조 검증 (등록 시 사용). */
    private void validateCanonicalReferences(SellerOptionGroups optionGroups) {
        List<CanonicalOptionGroupId> canonicalGroupIds =
                optionGroups.groups().stream()
                        .map(g -> g.canonicalOptionGroupId())
                        .filter(Objects::nonNull)
                        .toList();

        if (canonicalGroupIds.isEmpty()) {
            return;
        }

        Map<Long, CanonicalOptionGroup> canonicalGroupMap =
                canonicalOptionGroupQueryPort.findByIds(canonicalGroupIds).stream()
                        .collect(
                                Collectors.toMap(
                                        CanonicalOptionGroup::idValue, Function.identity()));

        for (var group : optionGroups.groups()) {
            var canonicalGroupId = group.canonicalOptionGroupId();
            if (canonicalGroupId == null) {
                continue;
            }

            CanonicalOptionGroup canonicalGroup = canonicalGroupMap.get(canonicalGroupId.value());
            if (canonicalGroup == null) {
                throw new IllegalArgumentException(
                        "존재하지 않는 캐노니컬 옵션 그룹입니다: " + canonicalGroupId.value());
            }

            Set<Long> canonicalValueIds =
                    canonicalGroup.values().stream()
                            .map(CanonicalOptionValue::idValue)
                            .collect(Collectors.toSet());

            for (var value : group.optionValues()) {
                var canonicalValueId = value.canonicalOptionValueId();
                if (canonicalValueId != null
                        && !canonicalValueIds.contains(canonicalValueId.value())) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "캐노니컬 옵션 값 %d은(는) 캐노니컬 옵션 그룹 %d에 속하지 않습니다",
                                    canonicalValueId.value(), canonicalGroupId.value()));
                }
            }
        }
    }
}
