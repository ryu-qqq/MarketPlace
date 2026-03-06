package com.ryuqq.marketplace.application.outboundproduct.assembler;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncSummaryResult;
import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
import org.springframework.stereotype.Component;

/** OMS 상품 상세 조립기. Bundle + SyncStatusSummary → OmsProductDetailResult 변환. */
@Component
public class OmsProductDetailAssembler {

    private final ProductGroupAssembler productGroupAssembler;

    public OmsProductDetailAssembler(ProductGroupAssembler productGroupAssembler) {
        this.productGroupAssembler = productGroupAssembler;
    }

    public OmsProductDetailResult toDetailResult(
            ProductGroupDetailBundle bundle, SyncStatusSummary summary) {
        ProductGroupDetailCompositeResult productGroup =
                productGroupAssembler.toDetailResult(bundle);

        SyncSummaryResult syncSummary =
                new SyncSummaryResult(
                        summary.totalCount(),
                        summary.completedCount(),
                        summary.failedCount(),
                        summary.pendingCount(),
                        summary.lastSyncAt());

        return new OmsProductDetailResult(productGroup, syncSummary);
    }
}
