package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;

/** 셀러 옵션 그룹 조회 결과 DTO. */
public record SellerOptionGroupResult(
        Long id,
        String optionGroupName,
        Long canonicalOptionGroupId,
        String inputType,
        int sortOrder,
        List<SellerOptionValueResult> optionValues) {

    public static SellerOptionGroupResult from(SellerOptionGroup group) {
        List<SellerOptionValueResult> values =
                group.optionValues().stream().map(SellerOptionValueResult::from).toList();

        return new SellerOptionGroupResult(
                group.idValue(),
                group.optionGroupNameValue(),
                group.canonicalOptionGroupId() != null
                        ? group.canonicalOptionGroupId().value()
                        : null,
                group.inputType().name(),
                group.sortOrder(),
                values);
    }
}
