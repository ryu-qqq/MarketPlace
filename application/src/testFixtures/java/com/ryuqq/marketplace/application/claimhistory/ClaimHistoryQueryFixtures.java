package com.ryuqq.marketplace.application.claimhistory;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.domain.claimhistory.vo.ActorType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * ClaimHistory Query 테스트 Fixtures.
 *
 * <p>ClaimHistory 관련 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ClaimHistoryQueryFixtures {

    private ClaimHistoryQueryFixtures() {}

    // ===== ClaimHistoryResult Fixtures =====

    public static ClaimHistoryResult statusChangeResult(String historyId) {
        Instant now = CommonVoFixtures.now();
        return new ClaimHistoryResult(
                historyId,
                ClaimHistoryType.STATUS_CHANGE.name(),
                "승인",
                "REQUESTED → APPROVED",
                ActorType.SYSTEM.name(),
                "system",
                "시스템",
                now);
    }

    public static ClaimHistoryResult manualResult(String historyId) {
        Instant now = CommonVoFixtures.now();
        return new ClaimHistoryResult(
                historyId,
                ClaimHistoryType.MANUAL.name(),
                "CS 메모",
                "고객 요청으로 취소 처리 확인",
                ActorType.ADMIN.name(),
                "admin-001",
                "관리자",
                now);
    }

    public static ClaimHistoryResult claimHistoryResult(
            String historyId, ClaimHistoryType type, String title, String message) {
        Instant now = CommonVoFixtures.now();
        return new ClaimHistoryResult(
                historyId,
                type.name(),
                title,
                message,
                ActorType.SYSTEM.name(),
                "system",
                "시스템",
                now);
    }

    // ===== List Fixtures =====

    public static List<ClaimHistoryResult> statusChangeResults() {
        return List.of(
                statusChangeResult("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70"),
                statusChangeResult("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f71"));
    }

    public static List<ClaimHistoryResult> mixedResults() {
        return List.of(
                statusChangeResult("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70"),
                manualResult("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f71"));
    }
}
