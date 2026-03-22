package com.ryuqq.marketplace.application.qna.service.query;

import com.ryuqq.marketplace.application.qna.assembler.QnaAssembler;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaDetailUseCase;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import org.springframework.stereotype.Service;

@Service
public class GetQnaDetailService implements GetQnaDetailUseCase {

    private final QnaReadManager readManager;
    private final QnaAssembler assembler;

    public GetQnaDetailService(QnaReadManager readManager, QnaAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public QnaResult execute(long qnaId) {
        Qna qna = readManager.getById(qnaId);
        return assembler.toResult(qna);
    }
}
