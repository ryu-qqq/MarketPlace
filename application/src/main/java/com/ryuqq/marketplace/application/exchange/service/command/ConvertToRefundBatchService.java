package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.ConvertToRefundBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.RefundBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 건 환불 전환 일괄 처리 서비스.
 *
 * <p>교환 건을 취소하고 환불 요청을 생성합니다 (크로스 도메인). Exchange는 CANCELLED 처리, Refund는 신규 생성됩니다.
 *
 * <p><strong>크로스 도메인 패턴</strong>: UseCase 호출 대신 대상 도메인의 Factory + PersistenceFacade를 직접 사용합니다. 순환
 * 의존성 방지를 위해 UseCase(Port-In) 호출은 금지합니다.
 */
@Service
public class ConvertToRefundBatchService implements ConvertToRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConvertToRefundBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory exchangeCommandFactory;
    private final ExchangePersistenceFacade exchangePersistenceFacade;
    private final RefundCommandFactory refundCommandFactory;
    private final RefundPersistenceFacade refundPersistenceFacade;

    public ConvertToRefundBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory exchangeCommandFactory,
            ExchangePersistenceFacade exchangePersistenceFacade,
            RefundCommandFactory refundCommandFactory,
            RefundPersistenceFacade refundPersistenceFacade) {
        this.validator = validator;
        this.exchangeCommandFactory = exchangeCommandFactory;
        this.exchangePersistenceFacade = exchangePersistenceFacade;
        this.refundCommandFactory = refundCommandFactory;
        this.refundPersistenceFacade = refundPersistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(ConvertToRefundBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("CONVERT");
        List<RefundClaim> refundClaims = new ArrayList<>();
        List<RefundOutbox> refundOutboxes = new ArrayList<>();
        List<ClaimHistory> refundHistories = new ArrayList<>();

        for (ExchangeClaim claim : claims) {
            try {
                ClaimHistory exchangeHistory =
                        exchangeCommandFactory.createConvertToRefundBundle(
                                claim, command.processedBy());
                batchResult.addSuccess(claim, exchangeHistory);

                RefundRequestItem refundItem =
                        new RefundRequestItem(
                                claim.orderItemIdValue(),
                                claim.exchangeQty(),
                                RefundReasonType.OTHER,
                                "교환 건 환불 전환 (교환번호: " + claim.claimNumberValue() + ")");
                RefundBundle refundBundle =
                        refundCommandFactory.createRefundRequest(
                                refundItem, command.processedBy(), claim.sellerId());
                refundClaims.add(refundBundle.claim());
                refundOutboxes.add(refundBundle.outbox());
                refundHistories.add(refundBundle.history());
            } catch (Exception e) {
                log.warn(
                        "교환→환불 전환 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            exchangePersistenceFacade.persistAll(
                    ExchangePersistenceBundle.withoutOutboxes(
                            batchResult.claims(), batchResult.histories()));
        }

        if (!refundClaims.isEmpty()) {
            refundPersistenceFacade.persistAll(
                    RefundPersistenceBundle.of(refundClaims, refundOutboxes, refundHistories));
        }

        return batchResult.toBatchProcessingResult();
    }
}
