package com.ryuqq.marketplace.domain.sellerapplication.vo;

import java.time.Instant;

/**
 * 동의 정보 Value Object.
 *
 * @param privacyAgreed 개인정보 처리방침 동의 여부
 * @param termsAgreed 이용약관 동의 여부
 * @param agreedAt 동의 시각
 */
public record Agreement(boolean privacyAgreed, boolean termsAgreed, Instant agreedAt) {

    public Agreement {
        if (!privacyAgreed || !termsAgreed) {
            throw new IllegalArgumentException("개인정보 처리방침과 이용약관에 모두 동의해야 합니다");
        }
        if (agreedAt == null) {
            throw new IllegalArgumentException("동의 시각은 필수입니다");
        }
    }

    public static Agreement of(boolean privacyAgreed, boolean termsAgreed, Instant agreedAt) {
        return new Agreement(privacyAgreed, termsAgreed, agreedAt);
    }

    public static Agreement agreedAt(Instant now) {
        return new Agreement(true, true, now);
    }

    /** DB에서 재구성 (검증 없이). */
    public static Agreement reconstitute(Instant agreedAt) {
        return new Agreement(true, true, agreedAt);
    }

    public Instant agreedAtValue() {
        return agreedAt;
    }

    public boolean isPrivacyAgreed() {
        return privacyAgreed;
    }

    public boolean isTermsAgreed() {
        return termsAgreed;
    }
}
