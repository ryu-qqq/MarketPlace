package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.command.SyncProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.RetrySyncApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncProductsApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** OMS Command API 매퍼. */
@Component
public class OmsProductCommandApiMapper {

    public ManualSyncProductsCommand toCommand(SyncProductsApiRequest request) {
        return new ManualSyncProductsCommand(request.productIds(), List.of(request.shopId()));
    }

    public SyncProductsApiResponse toSyncResponse(ManualSyncResult result) {
        return new SyncProductsApiResponse(
                result.createCount(), result.updateCount(), result.skippedCount(), result.status());
    }

    public RetrySyncApiResponse toRetryResponse(Long newOutboxId) {
        return RetrySyncApiResponse.of(newOutboxId);
    }
}
