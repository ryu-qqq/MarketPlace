package com.ryuqq.marketplace.adapter.in.rest.canonicaloption;

/** CanonicalOption Admin API 엔드포인트 상수. */
public final class CanonicalOptionAdminEndpoints {

    private CanonicalOptionAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String CANONICAL_OPTION_GROUPS = BASE + "/canonical-option-groups";
    public static final String CANONICAL_OPTION_GROUP_ID = "/{canonicalOptionGroupId}";
    public static final String PATH_CANONICAL_OPTION_GROUP_ID = "canonicalOptionGroupId";
}
