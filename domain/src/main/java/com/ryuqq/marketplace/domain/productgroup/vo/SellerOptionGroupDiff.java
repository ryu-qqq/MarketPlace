package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SellerOptionGroup 변경 비교 결과.
 *
 * <p>기존 옵션 그룹과 entry 목록을 ID 기반으로 비교하여 추가/삭제/유지 목록을 제공합니다. retained 그룹은 내부 Value 레벨의 nested diff를
 * 포함합니다.
 */
public record SellerOptionGroupDiff(
        List<SellerOptionGroup> addedGroups,
        List<SellerOptionGroup> removedGroups,
        List<RetainedGroupDiff> retainedGroups,
        List<SellerOptionValueId> orderedActiveValueIds,
        Instant occurredAt) {

    public SellerOptionGroupDiff {
        addedGroups = List.copyOf(addedGroups);
        removedGroups = List.copyOf(removedGroups);
        retainedGroups = List.copyOf(retainedGroups);
        orderedActiveValueIds = List.copyOf(orderedActiveValueIds);
    }

    public static SellerOptionGroupDiff of(
            List<SellerOptionGroup> addedGroups,
            List<SellerOptionGroup> removedGroups,
            List<RetainedGroupDiff> retainedGroups,
            List<SellerOptionValueId> orderedActiveValueIds,
            Instant occurredAt) {
        return new SellerOptionGroupDiff(
                addedGroups, removedGroups, retainedGroups, orderedActiveValueIds, occurredAt);
    }

    public boolean hasNoChanges() {
        return addedGroups.isEmpty()
                && removedGroups.isEmpty()
                && retainedGroups.stream().allMatch(RetainedGroupDiff::hasNoValueChanges);
    }

    /** retained 그룹 내에서 유지된 모든 SellerOptionValue를 반환합니다. */
    public List<SellerOptionValue> allRetainedValues() {
        List<SellerOptionValue> result = new ArrayList<>();
        for (RetainedGroupDiff retained : retainedGroups) {
            result.addAll(retained.retainedValues());
        }
        return result;
    }

    /** 모든 그룹 (removed + added + retained)을 하나의 리스트로 반환합니다. */
    public List<SellerOptionGroup> allGroups() {
        List<SellerOptionGroup> result = new ArrayList<>();
        result.addAll(removedGroups);
        result.addAll(addedGroups);
        for (RetainedGroupDiff retained : retainedGroups) {
            result.add(retained.group());
        }
        return result;
    }

    /** 삭제 대상 값 전체 (removed 그룹의 모든 값 + retained 그룹 내 removed 값). */
    public List<SellerOptionValue> allRemovedValues() {
        List<SellerOptionValue> result = new ArrayList<>();
        for (SellerOptionGroup removed : removedGroups) {
            result.addAll(removed.optionValues());
        }
        for (RetainedGroupDiff retained : retainedGroups) {
            result.addAll(retained.removedValues());
        }
        return result;
    }

    /** added 그룹 내의 모든 SellerOptionValue를 반환합니다. */
    public List<SellerOptionValue> allAddedValues() {
        List<SellerOptionValue> result = new ArrayList<>();
        for (SellerOptionGroup added : addedGroups) {
            result.addAll(added.optionValues());
        }
        for (RetainedGroupDiff retained : retainedGroups) {
            result.addAll(retained.addedValues());
        }
        return result;
    }

    /** diff 결과에서 활성 상태인 모든 SellerOptionValueId를 반환합니다. */
    public List<SellerOptionValueId> allActiveValueIds() {
        List<SellerOptionValueId> result = new ArrayList<>();
        for (SellerOptionGroup added : addedGroups) {
            for (SellerOptionValue v : added.optionValues()) {
                result.add(v.id());
            }
        }
        for (RetainedGroupDiff retained : retainedGroups) {
            for (SellerOptionValue v : retained.retainedValues()) {
                result.add(v.id());
            }
            for (SellerOptionValue v : retained.addedValues()) {
                result.add(v.id());
            }
        }
        return result;
    }

    /**
     * retained SellerOptionGroup 내부의 Value diff 정보.
     *
     * @param group 유지된 SellerOptionGroup (기존 객체, in-place 수정됨)
     * @param valueDiff 해당 그룹 내 SellerOptionValue의 diff
     */
    public record RetainedGroupDiff(SellerOptionGroup group, SellerOptionValueDiff valueDiff) {

        /** 내부 Value에 변경이 없는지 판별합니다. */
        public boolean hasNoValueChanges() {
            return valueDiff.hasNoChanges();
        }

        /** 유지된 SellerOptionValue 목록을 반환합니다. */
        public List<SellerOptionValue> retainedValues() {
            return valueDiff.retained();
        }

        /** 삭제된 SellerOptionValue 목록을 반환합니다. */
        public List<SellerOptionValue> removedValues() {
            return valueDiff.removed();
        }

        /** 추가된 SellerOptionValue 목록을 반환합니다. */
        public List<SellerOptionValue> addedValues() {
            return valueDiff.added();
        }
    }
}
