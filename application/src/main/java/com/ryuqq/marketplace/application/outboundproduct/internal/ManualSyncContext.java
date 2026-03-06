package com.ryuqq.marketplace.application.outboundproduct.internal;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 수동 전송에 필요한 조회 데이터 컨텍스트. */
public record ManualSyncContext(
        Set<Long> salesChannelIds,
        List<ProductGroup> productGroups,
        Map<Long, Set<Long>> connectedChannelIdsBySellerId,
        Set<String> existingProductKeys,
        Set<String> pendingKeys) {}
