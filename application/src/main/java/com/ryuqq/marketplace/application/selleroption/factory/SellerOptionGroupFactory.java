package com.ryuqq.marketplace.application.selleroption.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** SellerOptionGroup 생성 서브 팩토리. */
@Component
public class SellerOptionGroupFactory {

    private final TimeProvider timeProvider;

    public SellerOptionGroupFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 등록 Command의 옵션 그룹 리스트로부터 SellerOptionGroups 생성. */
    public SellerOptionGroups createFromRegistration(
            ProductGroupId productGroupId,
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups) {
        List<SellerOptionGroup> result = new ArrayList<>();
        int groupSortOrder = 0;

        for (var group : optionGroups) {
            SellerOptionGroupId tempGroupId = SellerOptionGroupId.forNew();
            OptionGroupName groupName = OptionGroupName.of(group.optionGroupName());
            OptionInputType inputType =
                    group.inputType() != null
                            ? OptionInputType.valueOf(group.inputType())
                            : OptionInputType.PREDEFINED;

            List<SellerOptionValue> optionValues =
                    group.optionValues().stream()
                            .map(
                                    v -> {
                                        OptionValueName valueName =
                                                OptionValueName.of(v.optionValueName());
                                        if (v.canonicalOptionValueId() != null) {
                                            return SellerOptionValue.forNewWithCanonical(
                                                    tempGroupId,
                                                    valueName,
                                                    CanonicalOptionValueId.of(
                                                            v.canonicalOptionValueId()),
                                                    v.sortOrder());
                                        }
                                        return SellerOptionValue.forNew(
                                                tempGroupId, valueName, v.sortOrder());
                                    })
                            .toList();

            SellerOptionGroup optionGroup;
            if (group.canonicalOptionGroupId() != null) {
                optionGroup =
                        SellerOptionGroup.forNewWithCanonical(
                                productGroupId,
                                groupName,
                                CanonicalOptionGroupId.of(group.canonicalOptionGroupId()),
                                inputType,
                                groupSortOrder++,
                                optionValues);
            } else {
                optionGroup =
                        SellerOptionGroup.forNew(
                                productGroupId,
                                groupName,
                                inputType,
                                groupSortOrder++,
                                optionValues);
            }
            result.add(optionGroup);
        }

        return SellerOptionGroups.of(result);
    }

    /** 수정 Command로부터 SellerOptionGroupUpdateData(entry 기반) 생성. */
    public SellerOptionGroupUpdateData toUpdateData(
            ProductGroupId productGroupId,
            List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups) {
        List<SellerOptionGroupUpdateData.GroupEntry> entries = new ArrayList<>();

        for (var group : optionGroups) {
            OptionInputType inputType =
                    group.inputType() != null
                            ? OptionInputType.valueOf(group.inputType())
                            : OptionInputType.PREDEFINED;

            List<SellerOptionGroupUpdateData.ValueEntry> valueEntries =
                    group.optionValues().stream()
                            .map(
                                    v ->
                                            new SellerOptionGroupUpdateData.ValueEntry(
                                                    v.sellerOptionValueId(),
                                                    v.optionValueName(),
                                                    v.canonicalOptionValueId(),
                                                    v.sortOrder()))
                            .toList();

            entries.add(
                    new SellerOptionGroupUpdateData.GroupEntry(
                            group.sellerOptionGroupId(),
                            group.optionGroupName(),
                            group.canonicalOptionGroupId(),
                            inputType,
                            entries.size(),
                            valueEntries));
        }

        return SellerOptionGroupUpdateData.of(productGroupId, entries, timeProvider.now());
    }
}
