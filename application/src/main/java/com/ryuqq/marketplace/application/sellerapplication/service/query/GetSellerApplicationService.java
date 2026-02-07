package com.ryuqq.marketplace.application.sellerapplication.service.query;

import com.ryuqq.marketplace.application.sellerapplication.assembler.SellerApplicationAssembler;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationReadManager;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.GetSellerApplicationUseCase;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import org.springframework.stereotype.Service;

/**
 * GetSellerApplicationService - 셀러 입점 신청 상세 조회 Service.
 *
 * <p>신청 ID로 상세 정보를 조회합니다.
 *
 * @author ryu-qqq
 */
@Service
public class GetSellerApplicationService implements GetSellerApplicationUseCase {

    private final SellerApplicationReadManager readManager;
    private final SellerApplicationAssembler assembler;

    public GetSellerApplicationService(
            SellerApplicationReadManager readManager, SellerApplicationAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public SellerApplicationResult execute(Long sellerApplicationId) {
        SellerApplicationId id = SellerApplicationId.of(sellerApplicationId);
        SellerApplication application = readManager.getById(id);
        return assembler.toResult(application);
    }
}
