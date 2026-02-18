package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.exception.OptionGroupDuplicateNameException;
import com.ryuqq.marketplace.domain.productgroup.exception.OptionGroupEmptyValuesException;
import com.ryuqq.marketplace.domain.productgroup.exception.OptionValueDuplicateNameException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidOptionStructureException;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 셀러 옵션 그룹 컬렉션 VO.
 *
 * <p>ProductGroupImages와 동일한 패턴으로, 옵션 그룹 컬렉션의 불변식을 보장합니다.
 */
public record SellerOptionGroups(List<SellerOptionGroup> groups) {

    public SellerOptionGroups {
        groups = List.copyOf(groups);
    }

    /** 신규 생성 또는 수정 시 사용. */
    public static SellerOptionGroups of(List<SellerOptionGroup> groups) {
        return new SellerOptionGroups(groups);
    }

    /** 영속성에서 복원 시 사용. 검증 스킵. */
    public static SellerOptionGroups reconstitute(List<SellerOptionGroup> groups) {
        return new SellerOptionGroups(groups);
    }

    // === 검증 ===

    /** optionType과 옵션 그룹 구조 전체 검증. */
    public void validateStructure(OptionType optionType) {
        validateGroupCount(optionType);
        if (!groups.isEmpty()) {
            validateNoDuplicateGroupNames();
            validateEachGroupHasValues();
            validateNoDuplicateValueNames();
        }
    }

    /** optionType과 옵션 그룹 수 정합성 검증. */
    private void validateGroupCount(OptionType optionType) {
        int expected = optionType.expectedOptionGroupCount();
        int actual = groups.size();
        if (optionType.requiresOptionGroup() && actual != expected) {
            throw new ProductGroupInvalidOptionStructureException(optionType, expected, actual);
        }
        if (!optionType.requiresOptionGroup() && actual > 0) {
            throw new ProductGroupInvalidOptionStructureException(optionType, expected, actual);
        }
    }

    /** 옵션 그룹명 중복 검증. */
    private void validateNoDuplicateGroupNames() {
        Set<String> seen = new HashSet<>();
        for (SellerOptionGroup group : groups) {
            String name = group.optionGroupNameValue();
            if (!seen.add(name)) {
                throw new OptionGroupDuplicateNameException(name);
            }
        }
    }

    /** 각 옵션 그룹에 최소 1개 옵션 값 존재 검증. */
    private void validateEachGroupHasValues() {
        for (SellerOptionGroup group : groups) {
            if (group.optionValueCount() == 0) {
                throw new OptionGroupEmptyValuesException(group.optionGroupNameValue());
            }
        }
    }

    /** 같은 옵션 그룹 내 옵션 값 이름 중복 검증. */
    private void validateNoDuplicateValueNames() {
        for (SellerOptionGroup group : groups) {
            Set<String> seen = new HashSet<>();
            for (SellerOptionValue value : group.optionValues()) {
                String name = value.optionValueNameValue();
                if (!seen.add(name)) {
                    throw new OptionValueDuplicateNameException(group.optionGroupNameValue(), name);
                }
            }
        }
    }

    // === 조회 ===

    /** 모든 셀러 옵션이 캐노니컬에 매핑되었는지 확인. */
    public boolean isFullyMappedToCanonical() {
        if (groups.isEmpty()) {
            return true;
        }
        return groups.stream().allMatch(SellerOptionGroup::isFullyMapped);
    }

    /** 총 옵션 값 수 (전체 그룹 합산). */
    public int totalOptionValueCount() {
        int total = 0;
        for (SellerOptionGroup group : groups) {
            total += group.optionValueCount();
        }
        return total;
    }

    /**
     * 옵션 변경 없이 모든 그룹/값을 retained로 처리하는 identity diff를 생성합니다.
     *
     * <p>Product만 단독 수정할 때, 옵션 구조는 그대로이므로 이 메서드로 diff를 만들어 updateWithDiff()에 전달합니다.
     *
     * @param now diff 발생 시각
     * @return 모든 그룹/값이 retained인 SellerOptionGroupDiff
     */
    public SellerOptionGroupDiff identityDiff(java.time.Instant now) {
        List<SellerOptionGroupDiff.RetainedGroupDiff> retained = new ArrayList<>();
        List<SellerOptionValueId> orderedActiveValueIds = new ArrayList<>();

        for (SellerOptionGroup group : groups) {
            SellerOptionValueDiff valueDiff =
                    SellerOptionValueDiff.of(List.of(), List.of(), group.optionValues());
            retained.add(new SellerOptionGroupDiff.RetainedGroupDiff(group, valueDiff));
            for (SellerOptionValue v : group.optionValues()) {
                orderedActiveValueIds.add(v.id());
            }
        }

        return SellerOptionGroupDiff.of(List.of(), List.of(), retained, orderedActiveValueIds, now);
    }

    // === Update ===

    /**
     * ID 기반으로 entry 목록과 비교하여 추가/삭제/유지를 판단하고 상태를 갱신합니다.
     *
     * <p>GroupEntry.sellerOptionGroupId가 non-null이면 기존 그룹에서 매칭하여 retained, null이면 신규 그룹으로 added,
     * 매칭되지 않은 기존 그룹은 soft delete 후 removed.
     *
     * <p>retained Group 내부에서는 ValueEntry.sellerOptionValueId 기반으로 동일 패턴 적용.
     *
     * @param updateData entry 기반 수정 데이터
     * @return 변경 비교 결과
     */
    public SellerOptionGroupDiff update(SellerOptionGroupUpdateData updateData) {
        Map<Long, SellerOptionGroup> existingById =
                groups.stream()
                        .filter(g -> g.idValue() != null)
                        .collect(Collectors.toMap(SellerOptionGroup::idValue, g -> g));

        List<SellerOptionGroup> added = new ArrayList<>();
        List<SellerOptionGroupDiff.RetainedGroupDiff> retained = new ArrayList<>();
        List<SellerOptionValueId> orderedActiveValueIds = new ArrayList<>();
        Set<Long> matchedGroupIds = new HashSet<>();

        for (SellerOptionGroupUpdateData.GroupEntry entry : updateData.groupEntries()) {
            if (entry.sellerOptionGroupId() != null) {
                SellerOptionGroup existing = existingById.get(entry.sellerOptionGroupId());
                if (existing == null) {
                    throw new IllegalArgumentException(
                            "존재하지 않는 셀러 옵션 그룹입니다: " + entry.sellerOptionGroupId());
                }
                matchedGroupIds.add(entry.sellerOptionGroupId());

                existing.updateName(OptionGroupName.of(entry.optionGroupName()));
                existing.updateSortOrder(entry.sortOrder());
                if (entry.canonicalOptionGroupId() != null) {
                    existing.mapToCanonical(
                            CanonicalOptionGroupId.of(entry.canonicalOptionGroupId()));
                }

                SellerOptionValueDiff valueDiff =
                        computeValueDiffById(existing, entry.values(), updateData.updatedAt());
                retained.add(new SellerOptionGroupDiff.RetainedGroupDiff(existing, valueDiff));
                collectOrderedActiveValueIds(valueDiff, entry.values(), orderedActiveValueIds);
            } else {
                SellerOptionGroup newGroup =
                        createGroupFromEntry(entry, updateData.productGroupId());
                added.add(newGroup);
                for (SellerOptionValue v : newGroup.optionValues()) {
                    orderedActiveValueIds.add(v.id());
                }
            }
        }

        List<SellerOptionGroup> removed =
                groups.stream()
                        .filter(g -> g.idValue() != null && !matchedGroupIds.contains(g.idValue()))
                        .toList();

        for (SellerOptionGroup group : removed) {
            group.delete(updateData.updatedAt());
        }

        return SellerOptionGroupDiff.of(
                added, removed, retained, orderedActiveValueIds, updateData.updatedAt());
    }

    /** ID 기반 Value 레벨 diff 계산. */
    private SellerOptionValueDiff computeValueDiffById(
            SellerOptionGroup existing,
            List<SellerOptionGroupUpdateData.ValueEntry> valueEntries,
            java.time.Instant updatedAt) {
        Map<Long, SellerOptionValue> existingById =
                existing.optionValues().stream()
                        .filter(v -> v.idValue() != null)
                        .collect(Collectors.toMap(SellerOptionValue::idValue, v -> v));

        List<SellerOptionValue> addedValues = new ArrayList<>();
        List<SellerOptionValue> retainedValues = new ArrayList<>();
        Set<Long> matchedValueIds = new HashSet<>();

        for (SellerOptionGroupUpdateData.ValueEntry entry : valueEntries) {
            if (entry.sellerOptionValueId() != null) {
                SellerOptionValue existingValue = existingById.get(entry.sellerOptionValueId());
                if (existingValue == null) {
                    throw new IllegalArgumentException(
                            "존재하지 않는 셀러 옵션 값입니다: " + entry.sellerOptionValueId());
                }
                matchedValueIds.add(entry.sellerOptionValueId());

                existingValue.updateName(OptionValueName.of(entry.optionValueName()));
                existingValue.updateSortOrder(entry.sortOrder());
                if (entry.canonicalOptionValueId() != null) {
                    existingValue.mapToCanonical(
                            CanonicalOptionValueId.of(entry.canonicalOptionValueId()));
                }
                retainedValues.add(existingValue);
            } else {
                SellerOptionValue newValue = createValueFromEntry(entry, existing.id());
                addedValues.add(newValue);
            }
        }

        List<SellerOptionValue> removedValues =
                existing.optionValues().stream()
                        .filter(v -> v.idValue() != null && !matchedValueIds.contains(v.idValue()))
                        .toList();

        for (SellerOptionValue value : removedValues) {
            value.delete(updatedAt);
        }

        return SellerOptionValueDiff.of(addedValues, removedValues, retainedValues);
    }

    /** entry → SellerOptionGroup 신규 생성. */
    private SellerOptionGroup createGroupFromEntry(
            SellerOptionGroupUpdateData.GroupEntry entry,
            com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId productGroupId) {
        SellerOptionGroupId tempGroupId = SellerOptionGroupId.forNew();
        OptionGroupName groupName = OptionGroupName.of(entry.optionGroupName());

        List<SellerOptionValue> values =
                entry.values().stream().map(v -> createValueFromEntry(v, tempGroupId)).toList();

        if (entry.canonicalOptionGroupId() != null) {
            return SellerOptionGroup.forNewWithCanonical(
                    productGroupId,
                    groupName,
                    CanonicalOptionGroupId.of(entry.canonicalOptionGroupId()),
                    entry.sortOrder(),
                    values);
        }
        return SellerOptionGroup.forNew(productGroupId, groupName, entry.sortOrder(), values);
    }

    /** entry → SellerOptionValue 신규 생성. */
    private SellerOptionValue createValueFromEntry(
            SellerOptionGroupUpdateData.ValueEntry entry, SellerOptionGroupId groupId) {
        OptionValueName valueName = OptionValueName.of(entry.optionValueName());
        if (entry.canonicalOptionValueId() != null) {
            return SellerOptionValue.forNewWithCanonical(
                    groupId,
                    valueName,
                    CanonicalOptionValueId.of(entry.canonicalOptionValueId()),
                    entry.sortOrder());
        }
        return SellerOptionValue.forNew(groupId, valueName, entry.sortOrder());
    }

    /** retained Group 내 활성 SellerOptionValueId를 entry 순서대로 수집. */
    private void collectOrderedActiveValueIds(
            SellerOptionValueDiff valueDiff,
            List<SellerOptionGroupUpdateData.ValueEntry> valueEntries,
            List<SellerOptionValueId> result) {
        Map<Long, SellerOptionValueId> retainedIdMap = new HashMap<>();
        for (SellerOptionValue v : valueDiff.retained()) {
            retainedIdMap.put(v.idValue(), v.id());
        }

        List<SellerOptionValue> addedValuesList = new ArrayList<>(valueDiff.added());
        int addedIdx = 0;

        for (SellerOptionGroupUpdateData.ValueEntry entry : valueEntries) {
            if (entry.sellerOptionValueId() != null) {
                SellerOptionValueId id = retainedIdMap.get(entry.sellerOptionValueId());
                if (id != null) {
                    result.add(id);
                }
            } else {
                if (addedIdx < addedValuesList.size()) {
                    SellerOptionValue addedValue = addedValuesList.get(addedIdx++);
                    result.add(addedValue.id());
                }
            }
        }
    }
}
