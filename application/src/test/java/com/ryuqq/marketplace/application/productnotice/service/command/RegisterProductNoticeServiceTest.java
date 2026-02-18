package com.ryuqq.marketplace.application.productnotice.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.factory.ProductNoticeCommandFactory;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.validator.NoticeEntriesValidator;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
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
@DisplayName("RegisterProductNoticeService 단위 테스트")
class RegisterProductNoticeServiceTest {

    @InjectMocks private RegisterProductNoticeService sut;

    @Mock private ProductNoticeCommandFactory commandFactory;
    @Mock private NoticeEntriesValidator noticeEntriesValidator;
    @Mock private ProductNoticeCommandCoordinator productNoticeCommandCoordinator;

    @Nested
    @DisplayName("execute() - 고시정보 등록")
    class ExecuteTest {

        @Test
        @DisplayName("등록 커맨드로 고시정보를 생성하고 저장 후 noticeId를 반환한다")
        void execute_ValidCommand_ReturnsNoticeId() {
            // given
            List<RegisterProductNoticeCommand.NoticeEntryCommand> entries =
                    List.of(
                            new RegisterProductNoticeCommand.NoticeEntryCommand(100L, "제조국"),
                            new RegisterProductNoticeCommand.NoticeEntryCommand(101L, "제조사"));
            RegisterProductNoticeCommand command =
                    new RegisterProductNoticeCommand(1L, 10L, entries);
            ProductNotice productNotice = ProductNoticeFixtures.newProductNotice();
            Long expectedNoticeId = 1L;

            given(commandFactory.create(command)).willReturn(productNotice);
            willDoNothing().given(noticeEntriesValidator).validate(productNotice);
            given(productNoticeCommandCoordinator.persist(productNotice))
                    .willReturn(expectedNoticeId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedNoticeId);
            then(commandFactory).should().create(command);
            then(noticeEntriesValidator).should().validate(productNotice);
            then(productNoticeCommandCoordinator).should().persist(productNotice);
        }
    }
}
