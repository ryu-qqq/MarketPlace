package com.ryuqq.marketplace.application.inboundqna.internal;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RECEIVED 상태 InboundQna를 내부 Qna로 변환하는 프로세서.
 *
 * <p>externalProductId가 있으면 InboundProductIdResolver를 통해 내부 productGroupId를 역조회합니다.
 * 조회 실패 또는 externalProductId 부재 시 productGroupId=0, sellerId=0으로 생성합니다.
 */
@Component
public class InboundQnaConversionProcessor {

    private static final Logger log = LoggerFactory.getLogger(InboundQnaConversionProcessor.class);

    private final InboundProductIdResolver productIdResolver;
    private final QnaCommandManager qnaCommandManager;
    private final QnaReadManager qnaReadManager;
    private final InboundQnaCommandManager inboundQnaCommandManager;

    public InboundQnaConversionProcessor(
            InboundProductIdResolver productIdResolver,
            QnaCommandManager qnaCommandManager,
            QnaReadManager qnaReadManager,
            InboundQnaCommandManager inboundQnaCommandManager) {
        this.productIdResolver = productIdResolver;
        this.qnaCommandManager = qnaCommandManager;
        this.qnaReadManager = qnaReadManager;
        this.inboundQnaCommandManager = inboundQnaCommandManager;
    }

    /**
     * InboundQna를 Qna로 변환하고 저장합니다.
     *
     * <p>변환 성공 시 inboundQna.markConverted(), 실패 시 inboundQna.markFailed()를 호출하고 상태를 저장합니다.
     *
     * @param inboundQna 변환할 InboundQna (RECEIVED 상태)
     * @param externalProductId 외부 상품 ID (null 허용)
     * @param externalOrderId 외부 주문 ID (null 허용)
     */
    @Transactional
    public void convert(InboundQna inboundQna, String externalProductId, String externalOrderId) {
        Instant now = Instant.now();
        try {
            long productGroupId = 0L;
            long sellerId = 0L;

            if (externalProductId != null && !externalProductId.isBlank()) {
                try {
                    ProductGroupId resolved =
                            productIdResolver.resolve(inboundQna.salesChannelId(), externalProductId);
                    productGroupId = resolved.value();
                } catch (Exception e) {
                    log.warn(
                            "externalProductId 역조회 실패 — productGroupId=0으로 저장: inboundQnaId={}, externalProductId={}",
                            inboundQna.idValue(),
                            externalProductId);
                }
            }

            Long orderId = parseOrderId(externalOrderId);

            QnaSource source =
                    new QnaSource(inboundQna.salesChannelId(), inboundQna.externalQnaId());

            Qna qna =
                    Qna.forNew(
                            sellerId,
                            productGroupId,
                            orderId,
                            inboundQna.qnaType(),
                            source,
                            inboundQna.questionContent(),
                            inboundQna.questionContent(),
                            inboundQna.questionAuthor(),
                            now);

            qnaCommandManager.persist(qna);

            Qna saved = qnaReadManager.getById(qna.idValue());
            inboundQna.markConverted(saved.idValue(), now);
            inboundQnaCommandManager.persist(inboundQna);

            log.debug(
                    "InboundQna 변환 완료: inboundQnaId={}, qnaId={}",
                    inboundQna.idValue(),
                    saved.idValue());

        } catch (Exception e) {
            log.warn(
                    "InboundQna 변환 실패: inboundQnaId={}, reason={}",
                    inboundQna.idValue(),
                    e.getMessage());
            try {
                inboundQna.markFailed(e.getMessage(), now);
                inboundQnaCommandManager.persist(inboundQna);
            } catch (Exception persistEx) {
                log.error(
                        "InboundQna FAILED 상태 저장 실패: inboundQnaId={}",
                        inboundQna.idValue(),
                        persistEx);
            }
        }
    }

    private Long parseOrderId(String externalOrderId) {
        if (externalOrderId == null || externalOrderId.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(externalOrderId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
