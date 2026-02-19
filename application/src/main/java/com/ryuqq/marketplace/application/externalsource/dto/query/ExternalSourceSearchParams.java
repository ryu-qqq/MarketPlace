package com.ryuqq.marketplace.application.externalsource.dto.query;

import java.util.List;

/** 외부 소스 검색 파라미터 DTO. */
public record ExternalSourceSearchParams(
        List<String> types, List<String> statuses, String searchWord, int page, int size) {

    public long offset() {
        return (long) page * size;
    }
}
