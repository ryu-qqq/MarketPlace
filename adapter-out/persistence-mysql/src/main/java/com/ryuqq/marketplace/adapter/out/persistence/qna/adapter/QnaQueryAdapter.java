package com.ryuqq.marketplace.adapter.out.persistence.qna.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.mapper.QnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.port.out.query.QnaQueryPort;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Qna 조회 어댑터. */
@Component
public class QnaQueryAdapter implements QnaQueryPort {

    private final QnaQueryDslRepository queryDslRepository;
    private final QnaReplyJpaRepository replyRepository;
    private final QnaJpaEntityMapper mapper;

    public QnaQueryAdapter(
            QnaQueryDslRepository queryDslRepository,
            QnaReplyJpaRepository replyRepository,
            QnaJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Qna> findById(long id) {
        return queryDslRepository.findById(id)
                .map(entity -> {
                    List<QnaReplyJpaEntity> replies = replyRepository.findByQnaId(entity.getId());
                    return mapper.toDomain(entity, replies);
                });
    }

    @Override
    public Optional<Qna> findBySalesChannelIdAndExternalQnaId(
            long salesChannelId, String externalQnaId) {
        return queryDslRepository
                .findBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId)
                .map(
                        entity -> {
                            List<QnaReplyJpaEntity> replies =
                                    replyRepository.findByQnaId(entity.getId());
                            return mapper.toDomain(entity, replies);
                        });
    }

    @Override
    public List<Qna> findBySellerId(long sellerId, QnaStatus status, int offset, int limit) {
        QnaJpaEntity.Status entityStatus = status != null
                ? QnaJpaEntity.Status.valueOf(status.name()) : null;

        List<QnaJpaEntity> qnaEntities =
                queryDslRepository.findBySellerIdAndStatus(sellerId, entityStatus, offset, limit);

        if (qnaEntities.isEmpty()) {
            return List.of();
        }

        List<Long> qnaIds = qnaEntities.stream().map(QnaJpaEntity::getId).toList();
        List<QnaReplyJpaEntity> allReplies = replyRepository.findByQnaIdIn(qnaIds);

        Map<Long, List<QnaReplyJpaEntity>> repliesByQnaId = allReplies.stream()
                .collect(Collectors.groupingBy(QnaReplyJpaEntity::getQnaId));

        return qnaEntities.stream()
                .map(entity -> mapper.toDomain(
                        entity, repliesByQnaId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    public long countBySellerId(long sellerId, QnaStatus status) {
        QnaJpaEntity.Status entityStatus = status != null
                ? QnaJpaEntity.Status.valueOf(status.name()) : null;
        return queryDslRepository.countBySellerIdAndStatus(sellerId, entityStatus);
    }

    @Override
    public List<Qna> search(QnaSearchCondition condition) {
        List<QnaJpaEntity> qnaEntities = queryDslRepository.search(condition);

        if (qnaEntities.isEmpty()) {
            return List.of();
        }

        List<Long> qnaIds = qnaEntities.stream().map(QnaJpaEntity::getId).toList();
        List<QnaReplyJpaEntity> allReplies = replyRepository.findByQnaIdIn(qnaIds);

        Map<Long, List<QnaReplyJpaEntity>> repliesByQnaId = allReplies.stream()
                .collect(Collectors.groupingBy(QnaReplyJpaEntity::getQnaId));

        return qnaEntities.stream()
                .map(entity -> mapper.toDomain(
                        entity, repliesByQnaId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    public long countByCondition(QnaSearchCondition condition) {
        return queryDslRepository.countByCondition(condition);
    }
}
