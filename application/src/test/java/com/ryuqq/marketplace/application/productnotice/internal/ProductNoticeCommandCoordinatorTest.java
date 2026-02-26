package com.ryuqq.marketplace.application.productnotice.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.factory.ProductNoticeCommandFactory;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeEntryCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
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
@DisplayName("ProductNoticeCommandCoordinator 단위 테스트")
class ProductNoticeCommandCoordinatorTest {

    @InjectMocks private ProductNoticeCommandCoordinator sut;

    @Mock private ProductNoticeCommandFactory noticeCommandFactory;
    @Mock private NoticeEntriesValidator noticeEntriesValidator;
    @Mock private ProductNoticeCommandManager noticeCommandManager;
    @Mock private ProductNoticeEntryCommandManager entryCommandManager;
    @Mock private ProductNoticeReadManager noticeReadManager;
    @Mock private ProductNoticeCommandFacade noticeCommandFacade;

    @Nested
    @DisplayName("register() - 고시정보 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("등록 Command로 Notice 생성 + 검증 + 저장 후 noticeId를 반환한다")
        void register_ValidCommand_ReturnsNoticeId() {
            // given
            List<RegisterProductNoticeCommand.NoticeEntryCommand> entries =
                    List.of(new RegisterProductNoticeCommand.NoticeEntryCommand(100L, "제조국"));
            RegisterProductNoticeCommand command =
                    new RegisterProductNoticeCommand(1L, 10L, entries);
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();
            Long expectedNoticeId = 1L;

            given(noticeCommandFactory.create(command)).willReturn(notice);
            willDoNothing().given(noticeEntriesValidator).validate(notice);
            given(noticeCommandManager.persist(notice)).willReturn(expectedNoticeId);

            // when
            Long result = sut.register(command);

            // then
            assertThat(result).isEqualTo(expectedNoticeId);
            then(noticeCommandFactory).should().create(command);
            then(noticeEntriesValidator).should().validate(notice);
        }
    }

    @Nested
    @DisplayName("persist() - Notice + Entry 직접 저장")
    class PersistTest {

        @Test
        @DisplayName("Notice와 Entry를 저장하고 noticeId를 반환한다")
        void persist_ValidProductNotice_ReturnsNoticeId() {
            // given
            ProductNotice productNotice = ProductNoticeFixtures.newProductNotice();
            Long expectedNoticeId = 1L;

            given(noticeCommandManager.persist(productNotice)).willReturn(expectedNoticeId);

            // when
            Long result = sut.persist(productNotice);

            // then
            assertThat(result).isEqualTo(expectedNoticeId);
            then(noticeCommandManager).should().persist(productNotice);
            then(entryCommandManager).should().persistAll(productNotice.entries());
        }
    }
}
