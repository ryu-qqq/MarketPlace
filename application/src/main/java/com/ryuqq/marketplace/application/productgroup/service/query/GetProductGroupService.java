package com.ryuqq.marketplace.application.productgroup.service.query;

import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import org.springframework.stereotype.Service;

/** 상품 그룹 상세 조회 Service (ID 기반, 연관 Aggregate + 정책 포함). */
@Service
public class GetProductGroupService implements GetProductGroupUseCase {

    private final ProductGroupReadFacade readFacade;
    private final ProductGroupAssembler assembler;

    public GetProductGroupService(
            ProductGroupReadFacade readFacade, ProductGroupAssembler assembler) {
        this.readFacade = readFacade;
        this.assembler = assembler;
    }

    @Override
    public ProductGroupDetailCompositeResult execute(Long productGroupId) {
        ProductGroupDetailBundle bundle = readFacade.getDetailBundle(productGroupId);
        return assembler.toDetailResult(bundle);
    }
}
