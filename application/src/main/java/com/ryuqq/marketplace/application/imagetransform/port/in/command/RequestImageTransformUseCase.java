package com.ryuqq.marketplace.application.imagetransform.port.in.command;

import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;

/** 수동 이미지 변환 요청 UseCase. */
public interface RequestImageTransformUseCase {

    void execute(RequestImageTransformCommand command);
}
