package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
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
@DisplayName("RegisterProductGroupDescriptionService 단위 테스트")
class RegisterProductGroupDescriptionServiceTest {

    @InjectMocks private RegisterProductGroupDescriptionService sut;

    @Mock private DescriptionCommandCoordinator descriptionCommandCoordinator;

    @Nested
    @DisplayName("execute() - 상세설명 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 상세설명을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsDescriptionId() {
            // given
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, "<p>상품 상세설명</p>");
            Long expectedId = 10L;
            given(descriptionCommandCoordinator.register(command)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(descriptionCommandCoordinator).should().register(command);
        }
    }
}
