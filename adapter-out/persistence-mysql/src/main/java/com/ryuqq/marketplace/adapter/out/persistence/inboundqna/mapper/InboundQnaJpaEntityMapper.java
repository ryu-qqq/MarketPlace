package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.id.InboundQnaId;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import org.springframework.stereotype.Component;

/** InboundQna JPA Entity Mapper. */
@Component
public class InboundQnaJpaEntityMapper {

    public InboundQnaJpaEntity toEntity(InboundQna qna) {
        return InboundQnaJpaEntity.create(
                qna.idValue(),
                qna.salesChannelId(),
                qna.externalQnaId(),
                qna.qnaType().name(),
                qna.questionContent(),
                qna.questionAuthor(),
                qna.rawPayload(),
                InboundQnaJpaEntity.Status.valueOf(qna.status().name()),
                qna.internalQnaId(),
                qna.failureReason(),
                qna.createdAt(),
                qna.updatedAt());
    }

    public InboundQna toDomain(InboundQnaJpaEntity entity) {
        return InboundQna.reconstitute(
                InboundQnaId.of(entity.getId()),
                entity.getSalesChannelId(),
                entity.getExternalQnaId(),
                QnaType.valueOf(entity.getQnaType()),
                entity.getQuestionContent(),
                entity.getQuestionAuthor(),
                entity.getRawPayload(),
                InboundQnaStatus.valueOf(entity.getStatus().name()),
                entity.getInternalQnaId(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
