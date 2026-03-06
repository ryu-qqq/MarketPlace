package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** ReceiveInboundProductCommand -> InboundProduct 매핑 레코드 생성 팩토리. */
@Component
public class InboundProductCommandFactory {

    private static final Logger log = LoggerFactory.getLogger(InboundProductCommandFactory.class);

    private final TimeProvider timeProvider;
    private final ObjectMapper objectMapper;

    public InboundProductCommandFactory(TimeProvider timeProvider, ObjectMapper objectMapper) {
        this.timeProvider = timeProvider;
        this.objectMapper = objectMapper;
    }

    /** Command에서 매핑 필드를 추출하고 원본 payload를 JSON으로 직렬화하여 InboundProduct를 생성한다. */
    public InboundProduct create(ReceiveInboundProductCommand command) {
        Instant now = timeProvider.now();
        String rawPayload = serializePayload(command);
        return InboundProduct.forNew(
                command.inboundSourceId(),
                ExternalProductCode.of(command.externalProductCode()),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.sellerId(),
                rawPayload,
                now);
    }

    /** ReceiveInboundProductCommand를 JSON 문자열로 직렬화한다. */
    public String serializePayload(ReceiveInboundProductCommand command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            log.error(
                    "인바운드 상품 payload 직렬화 실패: inboundSourceId={}, code={}",
                    command.inboundSourceId(),
                    command.externalProductCode(),
                    e);
            return null;
        }
    }

    /** JSON 문자열에서 ReceiveInboundProductCommand를 역직렬화한다. */
    public ReceiveInboundProductCommand deserializePayload(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, ReceiveInboundProductCommand.class);
        } catch (JsonProcessingException e) {
            log.error("인바운드 상품 payload 역직렬화 실패", e);
            return null;
        }
    }
}
