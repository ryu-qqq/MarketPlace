package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundPayloadInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MUSTIT rawPayloadJson 역직렬화 + 검증 전담 Resolver.
 *
 * <p>JSON 파싱 또는 검증 실패 시 {@link InboundPayloadInvalidException}을 던져 복구 불가능한 에러임을 Coordinator에 알립니다.
 */
@Component
public class MustitPayloadResolver {

    private static final Logger log = LoggerFactory.getLogger(MustitPayloadResolver.class);

    private final ObjectMapper objectMapper;

    public MustitPayloadResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
    }

    /**
     * rawPayloadJson을 MustitInboundPayload로 역직렬화합니다.
     *
     * @param rawPayloadJson 크롤링 원본 JSON
     * @param externalProductCode 외부 상품 코드 (에러 로그용)
     * @return 역직렬화된 페이로드
     * @throws InboundPayloadInvalidException 파싱 또는 검증 실패 시 (복구 불가능)
     */
    public MustitInboundPayload resolve(String rawPayloadJson, String externalProductCode) {
        MustitInboundPayload payload = deserialize(rawPayloadJson, externalProductCode);
        validate(payload, externalProductCode);
        return payload;
    }

    private MustitInboundPayload deserialize(String rawPayloadJson, String externalProductCode) {
        try {
            return objectMapper.readValue(rawPayloadJson, MustitInboundPayload.class);
        } catch (UnrecognizedPropertyException e) {
            log.error(
                    "MUSTIT 페이로드 파싱 실패: 알 수 없는 필드 '{}' 발견, externalProductCode={}, path={}",
                    e.getPropertyName(),
                    externalProductCode,
                    e.getPathReference());
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드에 알 수 없는 필드 '"
                            + e.getPropertyName()
                            + "' 존재. "
                            + "크롤러 데이터 포맷 변경 확인 필요. externalProductCode="
                            + externalProductCode,
                    e);
        } catch (MismatchedInputException e) {
            log.error(
                    "MUSTIT 페이로드 파싱 실패: 타입 불일치, expectedType={}, externalProductCode={}, path={}",
                    e.getTargetType() != null ? e.getTargetType().getSimpleName() : "unknown",
                    externalProductCode,
                    e.getPathReference());
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 타입 불일치. path="
                            + e.getPathReference()
                            + ", externalProductCode="
                            + externalProductCode,
                    e);
        } catch (JsonMappingException e) {
            log.error(
                    "MUSTIT 페이로드 파싱 실패: 매핑 오류, externalProductCode={}, path={}, message={}",
                    externalProductCode,
                    e.getPathReference(),
                    e.getOriginalMessage());
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 매핑 실패. externalProductCode=" + externalProductCode, e);
        } catch (JsonProcessingException e) {
            log.error(
                    "MUSTIT 페이로드 파싱 실패: JSON 구문 오류, externalProductCode={}, location={}",
                    externalProductCode,
                    e.getLocation() != null
                            ? "line="
                                    + e.getLocation().getLineNr()
                                    + ",col="
                                    + e.getLocation().getColumnNr()
                            : "unknown");
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 JSON 구문 오류. externalProductCode=" + externalProductCode, e);
        }
    }

    private void validate(MustitInboundPayload payload, String externalProductCode) {
        if (payload.itemName() == null || payload.itemName().isBlank()) {
            log.error(
                    "MUSTIT 페이로드 검증 실패: itemName 누락, externalProductCode={}", externalProductCode);
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 itemName 필수. externalProductCode=" + externalProductCode);
        }
        if (payload.currentPrice() <= 0) {
            log.error(
                    "MUSTIT 페이로드 검증 실패: currentPrice가 0 이하 ({}), externalProductCode={}",
                    payload.currentPrice(),
                    externalProductCode);
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 currentPrice는 0보다 커야 합니다. currentPrice="
                            + payload.currentPrice()
                            + ", externalProductCode="
                            + externalProductCode);
        }
        if (payload.images() == null) {
            log.error("MUSTIT 페이로드 검증 실패: images 누락, externalProductCode={}", externalProductCode);
            throw new InboundPayloadInvalidException(
                    "MUSTIT 페이로드 images 필수. externalProductCode=" + externalProductCode);
        }
    }
}
