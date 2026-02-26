package com.ryuqq.marketplace.application.seller.service.query;

import com.ryuqq.marketplace.application.seller.assembler.SellerCompositeAssembler;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerForAdminUseCase;
import org.springframework.stereotype.Service;

/**
 * GetSellerForAdminService - 어드민용 셀러 상세 조회 Service.
 *
 * <p>셀러 기본정보 + CS + 계약정보 + 정산정보 + 정책정보를 조합하여 반환.
 */
@Service
public class GetSellerForAdminService implements GetSellerForAdminUseCase {

    private final SellerCompositionReadManager compositionReadManager;
    private final SellerCompositeAssembler compositeAssembler;

    public GetSellerForAdminService(
            SellerCompositionReadManager compositionReadManager,
            SellerCompositeAssembler compositeAssembler) {
        this.compositionReadManager = compositionReadManager;
        this.compositeAssembler = compositeAssembler;
    }

    @Override
    public SellerFullCompositeResult execute(Long sellerId) {
        SellerAdminCompositeResult adminComposite =
                compositionReadManager.getAdminComposite(sellerId);
        SellerPolicyCompositeResult policyComposite =
                compositionReadManager.getPolicyComposite(sellerId);

        return compositeAssembler.assemble(adminComposite, policyComposite);
    }
}
