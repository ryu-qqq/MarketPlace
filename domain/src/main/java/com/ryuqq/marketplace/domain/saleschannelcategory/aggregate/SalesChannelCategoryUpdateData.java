package com.ryuqq.marketplace.domain.saleschannelcategory.aggregate;

import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;

public record SalesChannelCategoryUpdateData(
        String externalCategoryName,
        int sortOrder,
        boolean leaf,
        SalesChannelCategoryStatus status) {

    public static SalesChannelCategoryUpdateData of(
            String externalCategoryName,
            int sortOrder,
            boolean leaf,
            SalesChannelCategoryStatus status) {
        return new SalesChannelCategoryUpdateData(externalCategoryName, sortOrder, leaf, status);
    }
}
