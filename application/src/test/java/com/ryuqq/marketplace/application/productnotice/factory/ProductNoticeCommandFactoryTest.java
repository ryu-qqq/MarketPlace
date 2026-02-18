package com.ryuqq.marketplace.application.productnotice.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeUpdateData;
import java.time.Instant;
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
@DisplayName("ProductNoticeCommandFactory 단위 테스트")
class ProductNoticeCommandFactoryTest {

    @InjectMocks private ProductNoticeCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 신규 고시정보 생성")
    class CreateTest {

        @Test
        @DisplayName("등록 Command로 ProductNotice 도메인 객체를 생성한다")
        void create_ValidCommand_ReturnsProductNotice() {
            // given
            List<RegisterProductNoticeCommand.NoticeEntryCommand> entries =
                    List.of(
                            new RegisterProductNoticeCommand.NoticeEntryCommand(100L, "제조국"),
                            new RegisterProductNoticeCommand.NoticeEntryCommand(101L, "제조사"));
            RegisterProductNoticeCommand command =
                    new RegisterProductNoticeCommand(1L, 10L, entries);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ProductNotice result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entries()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("createUpdateData() - 수정 UpdateData 생성")
    class CreateUpdateDataTest {

        @Test
        @DisplayName("수정 Command로 ProductNoticeUpdateData를 생성한다")
        void createUpdateData_ValidCommand_ReturnsUpdateData() {
            // given
            List<UpdateProductNoticeCommand.NoticeEntryCommand> entries =
                    List.of(new UpdateProductNoticeCommand.NoticeEntryCommand(100L, "수정된 제조국"));
            UpdateProductNoticeCommand command = new UpdateProductNoticeCommand(1L, 10L, entries);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ProductNoticeUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entries()).hasSize(1);
        }
    }
}
