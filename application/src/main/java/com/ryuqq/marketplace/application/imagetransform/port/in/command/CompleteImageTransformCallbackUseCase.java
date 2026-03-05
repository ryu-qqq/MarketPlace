package com.ryuqq.marketplace.application.imagetransform.port.in.command;

import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;

/**
 * 이미지 변환 콜백 완료 유스케이스.
 *
 * <p>FileFlow에서 변환 완료 시 콜백으로 호출됩니다.
 */
public interface CompleteImageTransformCallbackUseCase {

    void execute(CompleteImageTransformCallbackCommand command);
}
