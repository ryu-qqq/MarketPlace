package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;

/**
 * 셀러 옵션 값 (Child Entity of SellerOptionGroup).
 * 셀러가 자유 입력한 옵션 값. 캐노니컬 옵션에 매핑 가능 (nullable).
 */
public class SellerOptionValue {

    private final SellerOptionValueId id;
    private final SellerOptionGroupId sellerOptionGroupId;
    private OptionValueName optionValueName;
    private CanonicalOptionValueId canonicalOptionValueId;
    private int sortOrder;

    private SellerOptionValue(
            SellerOptionValueId id,
            SellerOptionGroupId sellerOptionGroupId,
            OptionValueName optionValueName,
            CanonicalOptionValueId canonicalOptionValueId,
            int sortOrder) {
        this.id = id;
        this.sellerOptionGroupId = sellerOptionGroupId;
        this.optionValueName = optionValueName;
        this.canonicalOptionValueId = canonicalOptionValueId;
        this.sortOrder = sortOrder;
    }

    /** 신규 셀러 옵션 값 생성. */
    public static SellerOptionValue forNew(
            SellerOptionGroupId sellerOptionGroupId,
            OptionValueName optionValueName,
            int sortOrder) {
        return new SellerOptionValue(
                SellerOptionValueId.forNew(),
                sellerOptionGroupId,
                optionValueName,
                null,
                sortOrder);
    }

    /** 캐노니컬 매핑과 함께 신규 생성. */
    public static SellerOptionValue forNewWithCanonical(
            SellerOptionGroupId sellerOptionGroupId,
            OptionValueName optionValueName,
            CanonicalOptionValueId canonicalOptionValueId,
            int sortOrder) {
        return new SellerOptionValue(
                SellerOptionValueId.forNew(),
                sellerOptionGroupId,
                optionValueName,
                canonicalOptionValueId,
                sortOrder);
    }

    /** 영속성에서 복원 시 사용. */
    public static SellerOptionValue reconstitute(
            SellerOptionValueId id,
            SellerOptionGroupId sellerOptionGroupId,
            OptionValueName optionValueName,
            CanonicalOptionValueId canonicalOptionValueId,
            int sortOrder) {
        return new SellerOptionValue(id, sellerOptionGroupId, optionValueName, canonicalOptionValueId, sortOrder);
    }

    /** 옵션 값 이름 수정. */
    public void updateName(OptionValueName optionValueName) {
        this.optionValueName = optionValueName;
    }

    /** 캐노니컬 옵션 매핑. */
    public void mapToCanonical(CanonicalOptionValueId canonicalOptionValueId) {
        this.canonicalOptionValueId = canonicalOptionValueId;
    }

    /** 캐노니컬 옵션 매핑 해제. */
    public void unmapCanonical() {
        this.canonicalOptionValueId = null;
    }

    /** 캐노니컬 매핑 여부 확인. */
    public boolean isMappedToCanonical() {
        return canonicalOptionValueId != null;
    }

    /** 정렬 순서 변경. */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SellerOptionValueId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerOptionGroupId sellerOptionGroupId() {
        return sellerOptionGroupId;
    }

    public Long sellerOptionGroupIdValue() {
        return sellerOptionGroupId.value();
    }

    public OptionValueName optionValueName() {
        return optionValueName;
    }

    public String optionValueNameValue() {
        return optionValueName.value();
    }

    public CanonicalOptionValueId canonicalOptionValueId() {
        return canonicalOptionValueId;
    }

    public int sortOrder() {
        return sortOrder;
    }
}
