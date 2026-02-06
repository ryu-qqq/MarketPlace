package com.ryuqq.marketplace.application.sellerapplication.internal;

import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.application.sellerapplication.dto.bundle.SellerCreationBundle;
import com.ryuqq.marketplace.application.sellerapplication.factory.SellerApplicationCommandFactory;
import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationReadManager;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import org.springframework.stereotype.Component;

/**
 * 셀러 입점 신청 승인 Coordinator.
 *
 * <p>검증 → Seller 생성 → 신청 상태 업데이트를 조율합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationApprovalCoordinator {

    private final SellerApplicationReadManager applicationReadManager;
    private final SellerApplicationCommandFactory commandFactory;
    private final SellerValidator sellerValidator;
    private final SellerBusinessInfoValidator businessInfoValidator;
    private final SellerCreationFacade sellerCreationFacade;

    public SellerApplicationApprovalCoordinator(
            SellerApplicationReadManager applicationReadManager,
            SellerApplicationCommandFactory commandFactory,
            SellerValidator sellerValidator,
            SellerBusinessInfoValidator businessInfoValidator,
            SellerCreationFacade sellerCreationFacade) {
        this.applicationReadManager = applicationReadManager;
        this.commandFactory = commandFactory;
        this.sellerValidator = sellerValidator;
        this.businessInfoValidator = businessInfoValidator;
        this.sellerCreationFacade = sellerCreationFacade;
    }

    /**
     * 입점 신청을 승인합니다.
     *
     * <p>1. 신청 조회 및 검증
     *
     * <p>2. Seller 관련 도메인 객체 생성
     *
     * <p>3. Seller 저장
     *
     * <p>4. 신청 상태 업데이트
     *
     * @param sellerApplicationId 신청 ID
     * @param processedBy 처리자 식별자
     * @return 승인 처리된 SellerApplication (이벤트 포함)
     */
    public SellerApplication approve(Long sellerApplicationId, String processedBy) {
        SellerApplicationId applicationId = SellerApplicationId.of(sellerApplicationId);
        SellerApplication application = applicationReadManager.getById(applicationId);

        SellerCreationBundle bundle = commandFactory.createSellerBundle(application);
        validateBeforeApprove(bundle);

        sellerCreationFacade.approveAndPersist(
                bundle, application, processedBy, bundle.createdAt());

        return application;
    }

    private void validateBeforeApprove(SellerCreationBundle bundle) {
        sellerValidator.validateSellerNameNotDuplicate(bundle.sellerNameValue());
        businessInfoValidator.validateRegistrationNumberNotDuplicate(
                bundle.registrationNumberValue());
    }
}
