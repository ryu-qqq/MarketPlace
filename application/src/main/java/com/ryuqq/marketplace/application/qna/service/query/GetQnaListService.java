package com.ryuqq.marketplace.application.qna.service.query;

import com.ryuqq.marketplace.application.qna.assembler.QnaAssembler;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaListUseCase;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetQnaListService implements GetQnaListUseCase {

    private final QnaReadManager readManager;
    private final QnaAssembler assembler;

    public GetQnaListService(QnaReadManager readManager, QnaAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public QnaListResult execute(long sellerId, QnaStatus status, int offset, int limit) {
        List<Qna> qnas = readManager.findBySellerId(sellerId, status, offset, limit);
        long totalCount = readManager.countBySellerId(sellerId, status);
        List<QnaResult> results = assembler.toResults(qnas);
        return new QnaListResult(results, totalCount, offset, limit);
    }

    @Override
    public QnaListResult execute(QnaSearchCondition condition) {
        List<Qna> qnas = readManager.search(condition);
        long totalCount = readManager.countByCondition(condition);
        List<QnaResult> results = assembler.toResults(qnas);
        return new QnaListResult(results, totalCount, 0, condition.size());
    }
}
