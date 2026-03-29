package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyAnswerQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaContentsResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaImageResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaTargetResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyUserInfoQnaResponse;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaAnswerResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaImageResult;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 QnA 조회 API Mapper.
 *
 * <p>레거시 LegacyQnaDetailResult → 레거시 Response 변환.
 */
@Component
public class LegacyQnaQueryApiMapper {

    /** LegacyQnaSearchRequest → LegacyQnaSearchParams 변환. */
    public LegacyQnaSearchParams toSearchParams(
            LegacyQnaSearchRequest request, int size, Long effectiveSellerId) {
        return new LegacyQnaSearchParams(
                request.qnaStatus(),
                request.qnaType(),
                request.qnaDetailType(),
                request.privateYn(),
                request.lastDomainId(),
                effectiveSellerId != null ? effectiveSellerId : request.sellerId(),
                request.searchText(),
                request.startDate(),
                request.endDate(),
                size);
    }

    /** LegacyQnaDetailResult → LegacyDetailQnaResponse (상세 조회 응답). */
    public LegacyDetailQnaResponse toDetailResponse(LegacyQnaDetailResult result) {
        LegacyFetchQnaResponse qna = toFetchResponse(result);
        Set<LegacyAnswerQnaResponse> answers =
                result.answers().stream().map(this::toAnswerResponse).collect(Collectors.toSet());
        return new LegacyDetailQnaResponse(qna, answers);
    }

    /** LegacyQnaDetailResult → LegacyFetchQnaResponse (목록 항목 응답). */
    public LegacyFetchQnaResponse toFetchResponse(LegacyQnaDetailResult result) {
        LegacyQnaContentsResponse contents =
                new LegacyQnaContentsResponse(
                        nullToEmpty(result.title()), nullToEmpty(result.content()));

        LegacyUserInfoQnaResponse userInfo =
                new LegacyUserInfoQnaResponse(
                        nullToEmpty(result.userType()),
                        result.userId(),
                        nullToEmpty(result.insertOperator()),
                        "",
                        "",
                        null);

        LegacyQnaTargetResponse target;
        if (result.orderId() != null) {
            target =
                    LegacyQnaTargetResponse.order(
                            result.productGroupId() != null ? result.productGroupId() : 0L,
                            "",
                            "",
                            "",
                            0L,
                            result.orderId(),
                            "");
        } else {
            target =
                    LegacyQnaTargetResponse.product(
                            result.productGroupId() != null ? result.productGroupId() : 0L,
                            "",
                            "",
                            "");
        }

        List<LegacyQnaImageResponse> images =
                result.images().stream().map(this::toImageResponse).toList();

        return new LegacyFetchQnaResponse(
                result.qnaId(),
                contents,
                nullToEmpty(result.privateYn()),
                nullToEmpty(result.qnaStatus()),
                nullToEmpty(result.qnaType()),
                nullToEmpty(result.qnaDetailType()),
                nullToEmpty(result.sellerName()),
                userInfo,
                target,
                images,
                result.insertDate(),
                result.updateDate());
    }

    /** LegacyQnaDetailResult 목록 → LegacyFetchQnaResponse 목록 변환. */
    public List<LegacyFetchQnaResponse> toFetchResponses(List<LegacyQnaDetailResult> results) {
        return results.stream().map(this::toFetchResponse).toList();
    }

    private LegacyAnswerQnaResponse toAnswerResponse(LegacyQnaAnswerResult answer) {
        LegacyQnaContentsResponse contents =
                new LegacyQnaContentsResponse(
                        nullToEmpty(answer.title()), nullToEmpty(answer.content()));

        List<LegacyQnaImageResponse> images =
                answer.images().stream().map(this::toImageResponse).toList();

        return new LegacyAnswerQnaResponse(
                answer.qnaAnswerId(),
                answer.qnaAnswerParentId(),
                nullToEmpty(answer.qnaWriterType()),
                contents,
                images,
                nullToEmpty(answer.insertOperator()),
                nullToEmpty(answer.updateOperator()),
                answer.insertDate(),
                answer.updateDate());
    }

    private LegacyQnaImageResponse toImageResponse(LegacyQnaImageResult image) {
        return new LegacyQnaImageResponse(
                nullToEmpty(image.qnaIssueType()),
                image.qnaImageId(),
                image.qnaId(),
                image.qnaAnswerId(),
                nullToEmpty(image.imageUrl()),
                image.displayOrder());
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
