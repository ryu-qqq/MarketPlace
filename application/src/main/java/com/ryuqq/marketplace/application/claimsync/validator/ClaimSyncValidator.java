package com.ryuqq.marketplace.application.claimsync.validator;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.manager.ClaimSyncLogReadManager;
import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 클레임 동기화 사전 검증 컴포넌트.
 *
 * <p>매핑 존재 여부와 멱등성(중복 처리 방지)을 검증합니다.
 * null 반환 시 해당 클레임은 SKIP 처리됩니다.
 */
@Component
public class ClaimSyncValidator {

    private static final Logger log = LoggerFactory.getLogger(ClaimSyncValidator.class);

    private final ExternalOrderItemMappingReadManager mappingReadManager;
    private final ClaimSyncLogReadManager syncLogReadManager;

    public ClaimSyncValidator(
            ExternalOrderItemMappingReadManager mappingReadManager,
            ClaimSyncLogReadManager syncLogReadManager) {
        this.mappingReadManager = mappingReadManager;
        this.syncLogReadManager = syncLogReadManager;
    }

    /**
     * 동기화 전 사전 검증을 수행합니다.
     *
     * <p>다음 조건 중 하나라도 해당되면 null을 반환합니다:
     * <ul>
     *   <li>외부 주문상품번호에 대한 내부 매핑이 없는 경우</li>
     *   <li>동일한 (salesChannelId, externalProductOrderId, claimType, claimStatus) 조합이
     *       이미 처리된 경우 (멱등성 체크)</li>
     * </ul>
     *
     * @param claim 외부 클레임 페이로드
     * @param salesChannelId 판매채널 ID
     * @return 매핑 정보, SKIP이 필요한 경우 null
     */
    public ExternalOrderItemMapping validate(ExternalClaimPayload claim, long salesChannelId) {
        ExternalOrderItemMapping mapping =
                mappingReadManager.getMapping(salesChannelId, claim.externalProductOrderId());

        if (mapping == null) {
            log.debug(
                    "매핑 없음 - 스킵: externalProductOrderId={}", claim.externalProductOrderId());
            return null;
        }

        if (syncLogReadManager.isAlreadyProcessed(
                salesChannelId,
                claim.externalProductOrderId(),
                claim.claimType(),
                claim.claimStatus())) {
            log.debug(
                    "이미 처리된 클레임 - 스킵: externalProductOrderId={}, claimType={}, claimStatus={}",
                    claim.externalProductOrderId(),
                    claim.claimType(),
                    claim.claimStatus());
            return null;
        }

        return mapping;
    }
}
