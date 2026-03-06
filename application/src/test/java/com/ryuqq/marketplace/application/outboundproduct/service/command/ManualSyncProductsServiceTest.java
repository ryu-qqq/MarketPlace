package com.ryuqq.marketplace.application.outboundproduct.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.application.outboundproduct.internal.ManualSyncProductsCoordinator;
import java.util.List;
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
@DisplayName("ManualSyncProductsService 단위 테스트")
class ManualSyncProductsServiceTest {

    @InjectMocks private ManualSyncProductsService sut;
    @Mock private ManualSyncProductsCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 수동 전송 실행")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드를 코디네이터에 위임하고 결과를 반환한다")
        void execute_DelegatesToCoordinator_ReturnsResult() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L, 2L), List.of(10L));
            ManualSyncResult expected = ManualSyncResult.of(2, 0, 0);
            given(coordinator.execute(command)).willReturn(expected);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expected);
            then(coordinator).should().execute(command);
        }
    }
}
