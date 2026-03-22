package com.ryuqq.marketplace.adapter.in.rest.qna.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.AnswerQnaApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.qna.mapper.QnaCommandApiMapper;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.port.in.command.AnswerQnaUseCase;
import com.ryuqq.marketplace.application.qna.port.in.command.CloseQnaUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** QnA Command Controller. */
@RestController
@RequestMapping(QnaEndpoints.QNAS)
public class QnaCommandController {

    private final AnswerQnaUseCase answerQnaUseCase;
    private final CloseQnaUseCase closeQnaUseCase;
    private final QnaCommandApiMapper mapper;

    public QnaCommandController(
            AnswerQnaUseCase answerQnaUseCase,
            CloseQnaUseCase closeQnaUseCase,
            QnaCommandApiMapper mapper) {
        this.answerQnaUseCase = answerQnaUseCase;
        this.closeQnaUseCase = closeQnaUseCase;
        this.mapper = mapper;
    }

    @PostMapping(QnaEndpoints.ANSWER)
    public ResponseEntity<ApiResponse<Void>> answerQna(
            @PathVariable(QnaEndpoints.PATH_QNA_ID) long qnaId,
            @Valid @RequestBody AnswerQnaApiRequest request) {
        AnswerQnaCommand command = mapper.toCommand(qnaId, request);
        answerQnaUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of());
    }

    @PostMapping(QnaEndpoints.CLOSE)
    public ResponseEntity<Void> closeQna(
            @PathVariable(QnaEndpoints.PATH_QNA_ID) long qnaId) {
        CloseQnaCommand command = mapper.toCloseCommand(qnaId);
        closeQnaUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
