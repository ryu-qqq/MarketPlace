package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 셀러 옵션 그룹 컬렉션 VO.
 *
 * <p>ProductGroupImages와 동일한 패턴으로, 옵션 그룹 컬렉션의 불변식을 보장합니다.
 */
public class SellerOptionGroups {

    private final List<SellerOptionGroup> groups;

    private SellerOptionGroups(List<SellerOptionGroup> groups) {
        this.groups = groups;
    }

    /** 신규 생성 또는 수정 시 사용. */
    public static SellerOptionGroups of(List<SellerOptionGroup> groups) {
        return new SellerOptionGroups(List.copyOf(groups));
    }

    /** 영속성에서 복원 시 사용. 검증 스킵. */
    public static SellerOptionGroups reconstitute(List<SellerOptionGroup> groups) {
        return new SellerOptionGroups(List.copyOf(groups));
    }

    // === 조회 ===

    public List<SellerOptionGroup> toList() {
        return Collections.unmodifiableList(groups);
    }

    public int size() {
        return groups.size();
    }

    /** 모든 옵션 그룹의 옵션 값을 플랫하게 반환. */
    public List<SellerOptionValue> allOptionValues() {
        List<SellerOptionValue> all = new ArrayList<>();
        for (SellerOptionGroup group : groups) {
            all.addAll(group.optionValues());
        }
        return Collections.unmodifiableList(all);
    }

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
}
