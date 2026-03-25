package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAEndpoints.QNAS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAEndpoints.QNA_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAEndpoints.QNA_REPLY;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyCreateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyUpdateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyCreateQnaAnswerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper.LegacyQnaCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper.LegacyQnaQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaPageResult;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaDetailQueryUseCase;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaListQueryUseCase;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.port.in.command.AnswerQnaUseCase;
import com.ryuqq.marketplace.application.qna.port.in.command.UpdateQnaReplyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 QnA(문의) API 호환 컨트롤러.
 *
 * <p>조회는 luxurydb(레거시 DB)를 직접 조회하고, 답변 등록/수정은 표준 UseCase를 호출합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyQnAController {

    private final LegacyQnaDetailQueryUseCase legacyQnaDetailUseCase;
    private final LegacyQnaListQueryUseCase legacyQnaListUseCase;
    private final AnswerQnaUseCase answerQnaUseCase;
    private final UpdateQnaReplyUseCase updateQnaReplyUseCase;
    private final LegacyQnaQueryApiMapper queryApiMapper;
    private final LegacyQnaCommandApiMapper commandApiMapper;
    private final com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker legacyAccessChecker;

    public LegacyQnAController(
            LegacyQnaDetailQueryUseCase legacyQnaDetailUseCase,
            LegacyQnaListQueryUseCase legacyQnaListUseCase,
            AnswerQnaUseCase answerQnaUseCase,
            UpdateQnaReplyUseCase updateQnaReplyUseCase,
            LegacyQnaQueryApiMapper queryApiMapper,
            LegacyQnaCommandApiMapper commandApiMapper,
            com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker legacyAccessChecker) {
        this.legacyQnaDetailUseCase = legacyQnaDetailUseCase;
        this.legacyQnaListUseCase = legacyQnaListUseCase;
        this.answerQnaUseCase = answerQnaUseCase;
        this.updateQnaReplyUseCase = updateQnaReplyUseCase;
        this.queryApiMapper = queryApiMapper;
        this.commandApiMapper = commandApiMapper;
        this.legacyAccessChecker = legacyAccessChecker;
    }

    @Operation(summary = "QnA 단건 상세 조회", description = "QnA ID로 질문 + 답변 상세 정보를 조회합니다. (luxurydb)")
    @GetMapping(QNA_ID)
    public ResponseEntity<LegacyApiResponse<LegacyDetailQnaResponse>> fetchQna(
            @PathVariable long qnaId) {
        LegacyQnaDetailResult result = legacyQnaDetailUseCase.execute(qnaId);
        LegacyDetailQnaResponse response = queryApiMapper.toDetailResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "QnA 목록 조회", description = "페이징 기반으로 문의(QnA) 목록을 조회합니다. (luxurydb)")
    @GetMapping(QNAS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyFetchQnaResponse>>> getQnas(
            @Validated @ModelAttribute LegacyQnaSearchRequest request, Pageable pageable) {
        Long effectiveSellerId = legacyAccessChecker.resolveSellerIdOrNull();
        LegacyQnaSearchParams params = queryApiMapper.toSearchParams(request, pageable.getPageSize(), effectiveSellerId);
        LegacyQnaPageResult pageResult = legacyQnaListUseCase.execute(params);

        List<LegacyFetchQnaResponse> responses =
                queryApiMapper.toFetchResponses(pageResult.items());

        Long lastDomainId = responses.isEmpty() ? null : responses.getLast().qnaId();

        Pageable resolvedPageable = PageRequest.of(0, pageable.getPageSize());
        LegacyCustomPageable<LegacyFetchQnaResponse> page =
                new LegacyCustomPageable<>(
                        responses, resolvedPageable, pageResult.totalElements(), lastDomainId);

        return ResponseEntity.ok(LegacyApiResponse.success(page));
    }

    @Operation(summary = "QnA 답변 등록", description = "QnA에 답변을 등록합니다.")
    @PostMapping(QNA_REPLY)
    public ResponseEntity<LegacyApiResponse<LegacyCreateQnaAnswerResponse>> replyQna(
            @RequestBody LegacyCreateQnaAnswerRequest request) {
        AnswerQnaCommand command = commandApiMapper.toAnswerCommand(request);
        QnaReplyResult replyResult = answerQnaUseCase.execute(command);
        LegacyCreateQnaAnswerResponse response =
                commandApiMapper.toCreateAnswerResponse(request.qnaId(), replyResult);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "QnA 답변 수정", description = "기존 QnA 답변을 수정합니다.")
    @PutMapping(QNA_REPLY)
    public ResponseEntity<LegacyApiResponse<LegacyCreateQnaAnswerResponse>> updateReplyQna(
            @RequestBody LegacyUpdateQnaAnswerRequest request) {
        UpdateQnaReplyCommand command = commandApiMapper.toUpdateCommand(request);
        QnaReplyResult replyResult = updateQnaReplyUseCase.execute(command);
        LegacyCreateQnaAnswerResponse response =
                commandApiMapper.toCreateAnswerResponse(request.qnaId(), replyResult);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }
}
