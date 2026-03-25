package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.dto.LegacyQnaCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaAnswerEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.mapper.LegacyQnaApiMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.repository.LegacyQnaQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.port.out.LegacyQnaQueryPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 QnA 조회 Adapter.
 *
 * <p>{@link LegacyQnaQueryPort} 구현체. QueryDSL Repository에서 조회 후 Mapper로 변환합니다.
 */
@Component
public class LegacyQnaQueryAdapter implements LegacyQnaQueryPort {

    private final LegacyQnaQueryDslRepository repository;
    private final LegacyQnaApiMapper mapper;

    public LegacyQnaQueryAdapter(
            LegacyQnaQueryDslRepository repository, LegacyQnaApiMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyQnaDetailResult> fetchQnaDetail(long qnaId) {
        Optional<LegacyQnaCompositeQueryDto> dtoOpt = repository.fetchQnaComposite(qnaId);
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }

        LegacyQnaCompositeQueryDto dto = dtoOpt.get();
        List<LegacyQnaAnswerEntity> answers = repository.fetchAnswersByQnaId(qnaId);
        List<LegacyQnaImageEntity> qnaImages = repository.fetchImagesByQnaId(qnaId);

        Map<Long, List<LegacyQnaImageEntity>> answerImagesMap = new HashMap<>();
        for (LegacyQnaAnswerEntity answer : answers) {
            List<LegacyQnaImageEntity> answerImages =
                    repository.fetchImagesByAnswerId(answer.getId());
            answerImagesMap.put(answer.getId(), answerImages);
        }

        return Optional.of(mapper.toDetailResult(dto, answers, qnaImages, answerImagesMap));
    }

    @Override
    public List<LegacyQnaDetailResult> fetchQnaList(LegacyQnaSearchParams params) {
        List<LegacyQnaCompositeQueryDto> dtos = repository.fetchQnaList(params);
        return dtos.stream().map(mapper::toDetailResultSimple).toList();
    }

    @Override
    public long countQnas(LegacyQnaSearchParams params) {
        return repository.countQnas(params);
    }
}
