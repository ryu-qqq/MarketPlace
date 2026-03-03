package com.ryuqq.marketplace.application.outboundproduct.service.query;

import com.ryuqq.marketplace.application.outboundproduct.assembler.OmsProductDetailAssembler;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.GetOmsProductDetailUseCase;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** OMS 상품 상세 조회 Service (상품그룹 상세 + 연동 통계). */
@Service
public class GetOmsProductDetailService implements GetOmsProductDetailUseCase {

    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundSyncOutboxReadManager outboundSyncOutboxReadManager;
    private final OmsProductDetailAssembler assembler;

    public GetOmsProductDetailService(
            ProductGroupReadFacade productGroupReadFacade,
            OutboundSyncOutboxReadManager outboundSyncOutboxReadManager,
            OmsProductDetailAssembler assembler) {
        this.productGroupReadFacade = productGroupReadFacade;
        this.outboundSyncOutboxReadManager = outboundSyncOutboxReadManager;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public OmsProductDetailResult execute(Long productGroupId) {
        ProductGroupDetailBundle bundle = productGroupReadFacade.getDetailBundle(productGroupId);
        SyncStatusSummary summary = outboundSyncOutboxReadManager.getSyncSummary(productGroupId);
        return assembler.toDetailResult(bundle, summary);
    }
}
