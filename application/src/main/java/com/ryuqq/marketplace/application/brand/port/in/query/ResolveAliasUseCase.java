package com.ryuqq.marketplace.application.brand.port.in.query;

import com.ryuqq.marketplace.application.brand.dto.response.AliasMatchResponse;

public interface ResolveAliasUseCase {
    AliasMatchResponse resolveByAlias(String aliasName);
}
