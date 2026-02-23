package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * ExternalSourceType별 파서 객체를 제공하는 Provider.
 *
 * <p>초기화 시 파서 목록을 Map으로 인덱싱하여 O(1) 조회를 지원합니다.
 */
@Component
public class InboundProductPayloadParserProvider {

    private final Map<ExternalSourceType, InboundProductPayloadParser> parserMap;

    public InboundProductPayloadParserProvider(List<InboundProductPayloadParser> parsers) {
        this.parserMap = new EnumMap<>(ExternalSourceType.class);
        for (InboundProductPayloadParser parser : parsers) {
            for (ExternalSourceType sourceType : ExternalSourceType.values()) {
                if (parser.supports(sourceType)) {
                    parserMap.put(sourceType, parser);
                }
            }
        }
    }

    /**
     * sourceType에 해당하는 파서 객체를 반환한다.
     *
     * @param sourceType 외부 소스 타입
     * @return 해당 파서 구현체
     * @throws IllegalArgumentException 지원하지 않는 sourceType인 경우
     */
    public InboundProductPayloadParser getParser(ExternalSourceType sourceType) {
        InboundProductPayloadParser parser = parserMap.get(sourceType);
        if (parser == null) {
            throw new IllegalArgumentException("지원하지 않는 ExternalSourceType: " + sourceType);
        }
        return parser;
    }
}
