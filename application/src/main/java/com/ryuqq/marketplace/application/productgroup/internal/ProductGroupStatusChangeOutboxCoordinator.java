package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupActivationOutboxCoordinator;
import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupDeactivationOutboxCoordinator;
import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupUpdateOutboxCoordinator;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 상태 변경 후 외부 연동 Outbox 일괄 처리 코디네이터.
 *
 * <p>상태별로 적절한 Outbox Coordinator에 위임합니다. 이미 조회된 ProductGroup 객체를 그대로 전달하여 soft delete 필터에 의한 재조회
 * 실패를 방지합니다.
 */
@Component
public class ProductGroupStatusChangeOutboxCoordinator {

    private final ProductGroupActivationOutboxCoordinator activationOutboxCoordinator;
    private final ProductGroupDeactivationOutboxCoordinator deactivationOutboxCoordinator;
    private final ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator;

    public ProductGroupStatusChangeOutboxCoordinator(
            ProductGroupActivationOutboxCoordinator activationOutboxCoordinator,
            ProductGroupDeactivationOutboxCoordinator deactivationOutboxCoordinator,
            ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator) {
        this.activationOutboxCoordinator = activationOutboxCoordinator;
        this.deactivationOutboxCoordinator = deactivationOutboxCoordinator;
        this.updateOutboxCoordinator = updateOutboxCoordinator;
    }

    /**
     * 상태 변경된 상품 그룹들의 외부 연동 Outbox를 일괄 생성합니다.
     *
     * @param productGroups 상태 변경이 완료된 상품 그룹 목록
     * @param targetStatus 변경 대상 상태
     */
    @Transactional
    public void processOutboxes(List<ProductGroup> productGroups, ProductGroupStatus targetStatus) {
        for (ProductGroup productGroup : productGroups) {
            if (targetStatus.isActive()) {
                activationOutboxCoordinator.createOutboxAndProducts(productGroup);
            } else if (targetStatus.requiresExternalDeregistration()) {
                deactivationOutboxCoordinator.createDeleteOutboxesIfNeeded(productGroup);
            } else if (targetStatus.isSoldout()) {
                updateOutboxCoordinator.createUpdateOutboxesIfNeeded(
                        productGroup, Set.of(ChangedArea.STATUS));
            }
        }
    }
}
