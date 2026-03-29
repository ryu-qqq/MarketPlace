package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.dto.LegacyQnaCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaAnswerEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaImageEntity;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaAnswerResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaImageResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 QnA Persistence → Application DTO 변환 매퍼. */
@Component
public class LegacyQnaApiMapper {

    /** flat DTO + 답변 + 이미지 → Application 상세 결과. */
    public LegacyQnaDetailResult toDetailResult(
            LegacyQnaCompositeQueryDto dto,
            List<LegacyQnaAnswerEntity> answers,
            List<LegacyQnaImageEntity> qnaImages,
            java.util.Map<Long, List<LegacyQnaImageEntity>> answerImagesMap) {

        List<LegacyQnaAnswerResult> answerResults =
                answers.stream()
                        .map(
                                a -> {
                                    List<LegacyQnaImageEntity> answerImages =
                                            answerImagesMap.getOrDefault(a.getId(), List.of());
                                    return toAnswerResult(a, answerImages);
                                })
                        .toList();

        List<LegacyQnaImageResult> imageResults =
                qnaImages.stream().map(this::toImageResult).toList();

        return new LegacyQnaDetailResult(
                dto.qnaId(),
                nullToEmpty(dto.title()),
                nullToEmpty(dto.content()),
                nullToEmpty(dto.privateYn()),
                nullToEmpty(dto.qnaStatus()),
                nullToEmpty(dto.qnaType()),
                nullToEmpty(dto.qnaDetailType()),
                dto.userId(),
                dto.sellerId(),
                "",
                nullToEmpty(dto.userType()),
                "",
                dto.insertDate(),
                dto.updateDate(),
                dto.productGroupId(),
                dto.orderId(),
                answerResults,
                imageResults);
    }

    /** flat DTO → Application 상세 결과 (답변/이미지 없음 - 목록용). */
    public LegacyQnaDetailResult toDetailResultSimple(LegacyQnaCompositeQueryDto dto) {
        return new LegacyQnaDetailResult(
                dto.qnaId(),
                nullToEmpty(dto.title()),
                nullToEmpty(dto.content()),
                nullToEmpty(dto.privateYn()),
                nullToEmpty(dto.qnaStatus()),
                nullToEmpty(dto.qnaType()),
                nullToEmpty(dto.qnaDetailType()),
                dto.userId(),
                dto.sellerId(),
                "",
                nullToEmpty(dto.userType()),
                "",
                dto.insertDate(),
                dto.updateDate(),
                dto.productGroupId(),
                dto.orderId(),
                List.of(),
                List.of());
    }

    private LegacyQnaAnswerResult toAnswerResult(
            LegacyQnaAnswerEntity entity, List<LegacyQnaImageEntity> images) {
        List<LegacyQnaImageResult> imageResults = images.stream().map(this::toImageResult).toList();

        return new LegacyQnaAnswerResult(
                entity.getId(),
                entity.getQnaParentId(),
                nullToEmpty(entity.getQnaWriterType()),
                nullToEmpty(entity.getTitle()),
                nullToEmpty(entity.getContent()),
                nullToEmpty(entity.getInsertOperator()),
                nullToEmpty(entity.getUpdateOperator()),
                entity.getInsertDate(),
                entity.getUpdateDate(),
                imageResults);
    }

    private LegacyQnaImageResult toImageResult(LegacyQnaImageEntity entity) {
        return new LegacyQnaImageResult(
                nullToEmpty(entity.getQnaIssueType()),
                entity.getId(),
                entity.getQnaId(),
                entity.getQnaAnswerId(),
                nullToEmpty(entity.getImageUrl()),
                entity.getDisplayOrder() != null ? entity.getDisplayOrder().intValue() : 0);
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
