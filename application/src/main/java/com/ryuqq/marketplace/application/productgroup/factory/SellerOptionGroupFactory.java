package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** SellerOptionGroup 생성 서브 팩토리. */
@Component
public class SellerOptionGroupFactory {

    public SellerOptionGroups create(
            ProductGroupId productGroupId, List<OptionGroupData> optionGroupDataList) {
        List<SellerOptionGroup> optionGroups = new ArrayList<>();
        int groupSortOrder = 0;

        for (OptionGroupData groupData : optionGroupDataList) {
            SellerOptionGroupId tempGroupId = SellerOptionGroupId.forNew();
            OptionGroupName groupName = OptionGroupName.of(groupData.optionGroupName());

            List<SellerOptionValue> optionValues =
                    groupData.optionValues().stream()
                            .map(
                                    valueData -> {
                                        OptionValueName valueName =
                                                OptionValueName.of(valueData.optionValueName());
                                        if (valueData.canonicalOptionValueId() != null) {
                                            return SellerOptionValue.forNewWithCanonical(
                                                    tempGroupId,
                                                    valueName,
                                                    CanonicalOptionValueId.of(
                                                            valueData.canonicalOptionValueId()),
                                                    valueData.sortOrder());
                                        }
                                        return SellerOptionValue.forNew(
                                                tempGroupId, valueName, valueData.sortOrder());
                                    })
                            .toList();

            SellerOptionGroup optionGroup;
            if (groupData.canonicalOptionGroupId() != null) {
                optionGroup =
                        SellerOptionGroup.forNewWithCanonical(
                                productGroupId,
                                groupName,
                                CanonicalOptionGroupId.of(groupData.canonicalOptionGroupId()),
                                groupSortOrder++,
                                optionValues);
            } else {
                optionGroup =
                        SellerOptionGroup.forNew(
                                productGroupId, groupName, groupSortOrder++, optionValues);
            }

            optionGroups.add(optionGroup);
        }

        return SellerOptionGroups.of(optionGroups);
    }

    public record OptionGroupData(
            String optionGroupName,
            Long canonicalOptionGroupId,
            List<OptionValueData> optionValues) {}

    public record OptionValueData(
            String optionValueName, Long canonicalOptionValueId, int sortOrder) {}
}
