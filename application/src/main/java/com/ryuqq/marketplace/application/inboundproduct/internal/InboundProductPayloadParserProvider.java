package com.ryuqq.marketplace.application.inboundproduct.internal;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * sourceCode별 파서 객체를 제공하는 Provider.
 *
 * <p>등록된 파서 목록에서 sourceCode를 지원하는 파서를 런타임에 조회합니다.
 */
@Component
public class InboundProductPayloadParserProvider {

    private final List<InboundProductPayloadParser> parsers;

    public InboundProductPayloadParserProvider(List<InboundProductPayloadParser> parsers) {
        this.parsers = List.copyOf(parsers);
    }

    /**
     * sourceCode에 해당하는 파서 객체를 반환한다.
     *
     * @param sourceCode 인바운드 소스 코드
     * @return 해당 파서 구현체
     * @throws IllegalArgumentException 지원하지 않는 sourceCode인 경우
     */
    public InboundProductPayloadParser getParser(String sourceCode) {
        return parsers.stream()
                .filter(parser -> parser.supports(sourceCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소스 코드: " + sourceCode));
    }
}
