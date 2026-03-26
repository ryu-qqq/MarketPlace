package com.ryuqq.marketplace.application.claimhistory.port.in.command;

import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;

/** 수기 메모 등록 유스케이스. */
public interface AddClaimHistoryMemoUseCase {

    /** 수기 메모를 등록하고 생성된 historyId를 반환한다. */
    String execute(AddClaimHistoryMemoCommand command);
}
