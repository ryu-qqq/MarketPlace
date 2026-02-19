package com.ryuqq.marketplace.application.imagetransform.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformCommandFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformRequestCoordinator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestImageTransformService 단위 테스트")
class RequestImageTransformServiceTest {

    @InjectMocks private RequestImageTransformService sut;

    @Mock private ImageTransformRequestCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 수동 이미지 변환 요청")
    class ExecuteTest {

        @Test
        @DisplayName("전체 Variant 타입 변환 요청을 Coordinator에 위임한다")
        void execute_AllVariantsCommand_DelegatesToCoordinator() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestAllVariantsCommand();

            // when
            sut.execute(command);

            // then
            then(coordinator).should().request(command);
            then(coordinator).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("지정 Variant 타입 변환 요청을 Coordinator에 위임한다")
        void execute_SpecificVariantsCommand_DelegatesToCoordinator() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestSpecificVariantsCommand();

            // when
            sut.execute(command);

            // then
            then(coordinator).should().request(command);
            then(coordinator).shouldHaveNoMoreInteractions();
        }
    }
}
