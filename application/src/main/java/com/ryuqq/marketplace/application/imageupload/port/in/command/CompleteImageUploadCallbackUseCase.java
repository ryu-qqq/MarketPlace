package com.ryuqq.marketplace.application.imageupload.port.in.command;

import com.ryuqq.marketplace.application.imageupload.dto.command.CompleteImageUploadCallbackCommand;

/**
 * 이미지 업로드 콜백 완료 UseCase.
 *
 * <p>FileFlow에서 다운로드 완료 시 콜백으로 호출됩니다.
 */
public interface CompleteImageUploadCallbackUseCase {

    void execute(CompleteImageUploadCallbackCommand command);
}
