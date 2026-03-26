package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.claimsync.validator.ClaimSyncValidator;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncOutcome;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 클레임 동기화 전체 흐름을 조율하는 코디네이터.
 *
 * <p>외부 클레임 목록을 순회하며 검증(ClaimSyncValidator) → 처리(ClaimSyncProcessor) 단계를 조율합니다. 각 클레임 처리는 독립적으로 예외를
 * 처리하여, 하나의 실패가 전체 배치를 중단시키지 않습니다.
 */
@Component
public class ClaimSyncCoordinator {

    private static final Logger log = LoggerFactory.getLogger(ClaimSyncCoordinator.class);

    private final ClaimSyncValidator validator;
    private final ClaimSyncProcessor processor;

    public ClaimSyncCoordinator(ClaimSyncValidator validator, ClaimSyncProcessor processor) {
        this.validator = validator;
        this.processor = processor;
    }

    /**
     * 외부 클레임 목록을 동기화합니다.
     *
     * @param claims 외부몰에서 수신한 클레임 목록
     * @param salesChannelId 판매 채널 ID
     * @return 동기화 결과
     */
    public ClaimSyncResult syncAll(List<ExternalClaimPayload> claims, long salesChannelId) {
        int cancelSynced = 0;
        int refundSynced = 0;
        int exchangeSynced = 0;
        int skipped = 0;
        int failed = 0;

        for (ExternalClaimPayload claim : claims) {
            try {
                ExternalOrderItemMapping mapping = validator.validate(claim, salesChannelId);
                if (mapping == null) {
                    skipped++;
                    continue;
                }

                ClaimSyncOutcome outcome = processor.process(claim, mapping, salesChannelId);
                switch (outcome) {
                    case CANCEL_SYNCED -> cancelSynced++;
                    case REFUND_SYNCED -> refundSynced++;
                    case EXCHANGE_SYNCED -> exchangeSynced++;
                    case SKIPPED -> skipped++;
                }
            } catch (Exception e) {
                log.error(
                        "클레임 동기화 실패: salesChannelId={}, externalProductOrderId={}, claimType={},"
                                + " claimStatus={}",
                        salesChannelId,
                        claim.externalProductOrderId(),
                        claim.claimType(),
                        claim.claimStatus(),
                        e);
                failed++;
            }
        }

        return new ClaimSyncResult(
                claims.size(), cancelSynced, refundSynced, exchangeSynced, skipped, failed);
    }
}
