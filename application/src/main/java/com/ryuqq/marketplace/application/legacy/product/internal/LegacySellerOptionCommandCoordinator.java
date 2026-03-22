package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyOptionDetailCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyOptionGroupCommandManager;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 옵션그룹/옵션값 Command Coordinator.
 *
 * <p>표준 SellerOptionCommandCoordinator와 동일한 패턴.
 * 옵션그룹/옵션값을 저장하고 SellerOptionValueId 목록을 반환합니다.
 */
@Component
public class LegacySellerOptionCommandCoordinator {

    private final LegacyOptionGroupCommandManager optionGroupCommandManager;
    private final LegacyOptionDetailCommandManager optionDetailCommandManager;

    public LegacySellerOptionCommandCoordinator(
            LegacyOptionGroupCommandManager optionGroupCommandManager,
            LegacyOptionDetailCommandManager optionDetailCommandManager) {
        this.optionGroupCommandManager = optionGroupCommandManager;
        this.optionDetailCommandManager = optionDetailCommandManager;
    }

    @Transactional
    public List<SellerOptionValueId> register(RegisterSellerOptionGroupsCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        List<SellerOptionValueId> allValueIds = new ArrayList<>();

        int groupSortOrder = 0;
        for (RegisterSellerOptionGroupsCommand.OptionGroupCommand groupCmd : command.optionGroups()) {
            SellerOptionGroup group = SellerOptionGroup.forNew(
                    pgId,
                    OptionGroupName.of(groupCmd.optionGroupName()),
                    OptionInputType.PREDEFINED,
                    groupSortOrder++,
                    List.of());

            Long groupId = optionGroupCommandManager.persist(group);

            int valueSortOrder = 0;
            for (RegisterSellerOptionGroupsCommand.OptionValueCommand valueCmd : groupCmd.optionValues()) {
                SellerOptionValue value = SellerOptionValue.forNew(
                        SellerOptionGroupId.of(groupId),
                        OptionValueName.of(valueCmd.optionValueName()),
                        valueSortOrder++);
                Long valueId = optionDetailCommandManager.persist(value);
                allValueIds.add(SellerOptionValueId.of(valueId));
            }
        }

        return allValueIds;
    }
}
