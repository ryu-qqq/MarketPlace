package com.ryuqq.marketplace.application.uploadsession.port.in.command;

import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;

/** 업로드 세션 완료 처리 UseCase. */
public interface CompleteUploadSessionUseCase {

    void execute(CompleteUploadSessionCommand command);
}
