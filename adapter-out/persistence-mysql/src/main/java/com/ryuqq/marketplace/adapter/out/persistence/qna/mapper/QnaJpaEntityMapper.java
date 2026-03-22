package com.ryuqq.marketplace.adapter.out.persistence.qna.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.id.QnaReplyId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.util.List;
import org.springframework.stereotype.Component;

/** Qna JPA Entity Mapper. */
@Component
public class QnaJpaEntityMapper {

    public QnaJpaEntity toEntity(Qna qna) {
        return QnaJpaEntity.create(
                qna.idValue(),
                qna.sellerId(),
                qna.productGroupId(),
                qna.orderId(),
                qna.qnaType().name(),
                qna.source().salesChannelId(),
                qna.source().externalQnaId(),
                qna.questionTitle(),
                qna.questionContent(),
                qna.questionAuthor(),
                QnaJpaEntity.Status.valueOf(qna.status().name()),
                qna.createdAt(),
                qna.updatedAt());
    }

    public QnaReplyJpaEntity toReplyEntity(QnaReply reply, long qnaId) {
        return QnaReplyJpaEntity.create(
                reply.idValue(),
                qnaId,
                reply.parentReplyId(),
                reply.content(),
                reply.authorName(),
                reply.replyType().name(),
                reply.createdAt());
    }

    public List<QnaReplyJpaEntity> toReplyEntities(List<QnaReply> replies, long qnaId) {
        return replies.stream().map(reply -> toReplyEntity(reply, qnaId)).toList();
    }

    public Qna toDomain(QnaJpaEntity entity, List<QnaReplyJpaEntity> replyEntities) {
        List<QnaReply> replies = replyEntities.stream().map(this::toReplyDomain).toList();

        return Qna.reconstitute(
                QnaId.of(entity.getId()),
                entity.getSellerId(),
                entity.getProductGroupId(),
                entity.getOrderId(),
                QnaType.valueOf(entity.getQnaType()),
                new QnaSource(entity.getSalesChannelId(), entity.getExternalQnaId()),
                entity.getQuestionTitle(),
                entity.getQuestionContent(),
                entity.getQuestionAuthor(),
                QnaStatus.valueOf(entity.getStatus().name()),
                replies,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private QnaReply toReplyDomain(QnaReplyJpaEntity entity) {
        return QnaReply.reconstitute(
                QnaReplyId.of(entity.getId()),
                entity.getParentReplyId(),
                entity.getContent(),
                entity.getAuthorName(),
                QnaReplyType.valueOf(entity.getReplyType()),
                entity.getCreatedAt());
    }
}
