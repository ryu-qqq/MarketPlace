package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformRequestCoordinator;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.RequestImageTransformUseCase;
import org.springframework.stereotype.Service;

/**
 * 수동 이미지 변환 요청 서비스.
 *
 * <p>ImageTransformRequestCoordinator에 위임합니다.
 */
@Service
public class RequestImageTransformService implements RequestImageTransformUseCase {

    private final ImageTransformRequestCoordinator coordinator;

    public RequestImageTransformService(ImageTransformRequestCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void execute(RequestImageTransformCommand command) {
        coordinator.request(command);
    }
}
