package com.ryuqq.marketplace.domain.productgroup.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;

/**
 * 셀러 옵션 그룹 (Child Entity of ProductGroup).
 * 셀러가 정의한 옵션 그룹 (예: "색상", "사이즈"). 캐노니컬 옵션 그룹에 매핑 가능 (nullable).
 */
public class SellerOptionGroup {

    private final SellerOptionGroupId id;
    private final ProductGroupId productGroupId;
    private OptionGroupName optionGroupName;
    private CanonicalOptionGroupId canonicalOptionGroupId;
    private int sortOrder;
    private final List<SellerOptionValue> optionValues;

    private SellerOptionGroup(
            SellerOptionGroupId id,
            ProductGroupId productGroupId,
            OptionGroupName optionGroupName,
            CanonicalOptionGroupId canonicalOptionGroupId,
            int sortOrder,
            List<SellerOptionValue> optionValues) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.optionGroupName = optionGroupName;
        this.canonicalOptionGroupId = canonicalOptionGroupId;
        this.sortOrder = sortOrder;
        this.optionValues = new ArrayList<>(optionValues);
    }

    /** 신규 셀러 옵션 그룹 생성. */
    public static SellerOptionGroup forNew(
            ProductGroupId productGroupId,
            OptionGroupName optionGroupName,
            int sortOrder,
            List<SellerOptionValue> optionValues) {
        return new SellerOptionGroup(
                SellerOptionGroupId.forNew(),
                productGroupId,
                optionGroupName,
                null,
                sortOrder,
                optionValues);
    }

    /** 캐노니컬 매핑과 함께 신규 생성. */
    public static SellerOptionGroup forNewWithCanonical(
            ProductGroupId productGroupId,
            OptionGroupName optionGroupName,
            CanonicalOptionGroupId canonicalOptionGroupId,
            int sortOrder,
            List<SellerOptionValue> optionValues) {
        return new SellerOptionGroup(
                SellerOptionGroupId.forNew(),
                productGroupId,
                optionGroupName,
                canonicalOptionGroupId,
                sortOrder,
                optionValues);
    }

    /** 영속성에서 복원 시 사용. */
    public static SellerOptionGroup reconstitute(
            SellerOptionGroupId id,
            ProductGroupId productGroupId,
            OptionGroupName optionGroupName,
            CanonicalOptionGroupId canonicalOptionGroupId,
            int sortOrder,
            List<SellerOptionValue> optionValues) {
        return new SellerOptionGroup(id, productGroupId, optionGroupName, canonicalOptionGroupId, sortOrder, optionValues);
    }

    /** 옵션 그룹명 수정. */
    public void updateName(OptionGroupName optionGroupName) {
        this.optionGroupName = optionGroupName;
    }

    /** 캐노니컬 옵션 그룹 매핑. */
    public void mapToCanonical(CanonicalOptionGroupId canonicalOptionGroupId) {
        this.canonicalOptionGroupId = canonicalOptionGroupId;
    }

    /** 캐노니컬 옵션 그룹 매핑 해제. */
    public void unmapCanonical() {
        this.canonicalOptionGroupId = null;
    }

    /** 캐노니컬 매핑 여부 확인. */
    public boolean isMappedToCanonical() {
        return canonicalOptionGroupId != null;
    }

    /** 옵션 값 추가. */
    public void addOptionValue(SellerOptionValue optionValue) {
        this.optionValues.add(optionValue);
    }

    /** 정렬 순서 변경. */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /** 옵션 값 수 반환. */
    public int optionValueCount() {
        return optionValues.size();
    }

    /** 모든 옵션 값이 캐노니컬에 매핑되었는지 확인. */
    public boolean isFullyMapped() {
        if (!isMappedToCanonical()) {
            return false;
        }
        return optionValues.stream().allMatch(SellerOptionValue::isMappedToCanonical);
    }

    public SellerOptionGroupId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public OptionGroupName optionGroupName() {
        return optionGroupName;
    }

    public String optionGroupNameValue() {
        return optionGroupName.value();
    }

    public CanonicalOptionGroupId canonicalOptionGroupId() {
        return canonicalOptionGroupId;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public List<SellerOptionValue> optionValues() {
        return Collections.unmodifiableList(optionValues);
    }
}
