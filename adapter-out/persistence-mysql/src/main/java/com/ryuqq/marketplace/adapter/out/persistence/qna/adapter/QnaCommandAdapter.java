package com.ryuqq.marketplace.adapter.out.persistence.qna.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.mapper.QnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.application.qna.port.out.command.QnaCommandPort;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import java.util.List;
import org.springframework.stereotype.Component;

/** Qna 저장 어댑터. */
@Component
public class QnaCommandAdapter implements QnaCommandPort {

    private final QnaJpaRepository qnaRepository;
    private final QnaReplyJpaRepository replyRepository;
    private final QnaJpaEntityMapper mapper;

    public QnaCommandAdapter(
            QnaJpaRepository qnaRepository,
            QnaReplyJpaRepository replyRepository,
            QnaJpaEntityMapper mapper) {
        this.qnaRepository = qnaRepository;
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    @Override
    public long persist(Qna qna) {
        QnaJpaEntity qnaEntity = mapper.toEntity(qna);
        QnaJpaEntity saved = qnaRepository.save(qnaEntity);

        List<QnaReplyJpaEntity> replyEntities =
                mapper.toReplyEntities(qna.replies(), saved.getId());
        replyRepository.saveAll(replyEntities);
        return saved.getId();
    }
}
