package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.QnaUpdatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.inboundqna.dto.external.QnaUpdatePayload;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.UpdateQnaWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * QnA 수정 웹훅 Controller.
 *
 * <p>자사몰에서 고객이 QnA를 수정했을 때 호출. 기존 Qna의 질문 내용을 업데이트합니다.
 */
@Tag(name = "내부 웹훅", description = "자사몰 내부 웹훅 수신 API")
@RestController
public class QnaUpdatedWebhookController {

    private final UpdateQnaWebhookUseCase updateQnaWebhookUseCase;
    private final InternalWebhookApiMapper mapper;

    public QnaUpdatedWebhookController(
            UpdateQnaWebhookUseCase updateQnaWebhookUseCase,
            InternalWebhookApiMapper mapper) {
        this.updateQnaWebhookUseCase = updateQnaWebhookUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "QnA 수정 웹훅", description = "자사몰에서 고객이 QnA를 수정했을 때 수신합니다.")
    @PostMapping(InternalWebhookEndpoints.QNA_UPDATED)
    public ResponseEntity<ApiResponse<Map<String, Integer>>> handleQnaUpdated(
            @RequestBody @Valid QnaUpdatedWebhookRequest request) {

        List<QnaUpdatePayload> payloads = mapper.toQnaUpdatePayloads(request);
        int updated = updateQnaWebhookUseCase.execute(payloads, request.salesChannelId());

        return ResponseEntity.ok(
                ApiResponse.of(Map.of("total", payloads.size(), "updated", updated)));
    }
}
