package com.ryuqq.marketplace.application.claimsync.port.in;

import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;

/** 외부몰 클레임 폴링 UseCase. */
public interface PollExternalClaimsUseCase {

    ClaimSyncResult execute(long salesChannelId);
}
