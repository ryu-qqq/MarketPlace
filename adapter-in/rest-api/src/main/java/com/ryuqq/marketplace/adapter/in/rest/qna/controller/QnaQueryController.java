package com.ryuqq.marketplace.adapter.in.rest.qna.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.SearchQnaApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.mapper.QnaQueryApiMapper;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaDetailUseCase;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaListUseCase;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** QnA Query Controller. */
@RestController
@RequestMapping(QnaEndpoints.QNAS)
public class QnaQueryController {

    private final GetQnaListUseCase getQnaListUseCase;
    private final GetQnaDetailUseCase getQnaDetailUseCase;
    private final QnaQueryApiMapper mapper;

    public QnaQueryController(
            GetQnaListUseCase getQnaListUseCase,
            GetQnaDetailUseCase getQnaDetailUseCase,
            QnaQueryApiMapper mapper) {
        this.getQnaListUseCase = getQnaListUseCase;
        this.getQnaDetailUseCase = getQnaDetailUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<QnaApiResponse>>> searchQnasByOffset(
            @ParameterObject @Valid SearchQnaApiRequest request) {
        int offset = request.resolvedPage() * request.resolvedSize();
        QnaListResult result = getQnaListUseCase.execute(
                request.sellerId(), request.status(), offset, request.resolvedSize());
        PageApiResponse<QnaApiResponse> pageResponse = mapper.toPageResponse(result);
        return ResponseEntity.ok(ApiResponse.of(pageResponse));
    }

    @GetMapping(QnaEndpoints.QNA_ID)
    public ResponseEntity<ApiResponse<QnaApiResponse>> getQna(
            @PathVariable(QnaEndpoints.PATH_QNA_ID) long qnaId) {
        QnaResult result = getQnaDetailUseCase.execute(qnaId);
        QnaApiResponse response = mapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
