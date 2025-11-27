package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;

/**
 * AliasStatus Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class AliasStatusFixture {

    private AliasStatusFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 AliasStatus Fixture (확정)
     */
    public static AliasStatus defaultAliasStatus() {
        return AliasStatus.CONFIRMED;
    }

    /**
     * 자동 제안 AliasStatus Fixture
     */
    public static AliasStatus autoSuggestedAliasStatus() {
        return AliasStatus.AUTO_SUGGESTED;
    }

    /**
     * 검수 대기 AliasStatus Fixture
     */
    public static AliasStatus pendingReviewAliasStatus() {
        return AliasStatus.PENDING_REVIEW;
    }

    /**
     * 거부 AliasStatus Fixture
     */
    public static AliasStatus rejectedAliasStatus() {
        return AliasStatus.REJECTED;
    }
}
