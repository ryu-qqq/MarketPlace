package com.ryuqq.marketplace.application.commoncode.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.commoncode.CommonCodeCommandFixtures;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCodeUpdateData;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
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
@DisplayName("CommonCodeCommandFactory 단위 테스트")
class CommonCodeCommandFactoryTest {

    @InjectMocks private CommonCodeCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - RegisterCommonCodeCommand → CommonCode 변환")
    class CreateTest {

        @Test
        @DisplayName("RegisterCommonCodeCommand로부터 CommonCode를 생성한다")
        void create_CreatesCommonCode() {
            // given
            Long commonCodeTypeId = 1L;
            Instant now = Instant.now();
            RegisterCommonCodeCommand command =
                    CommonCodeCommandFixtures.registerCommand(commonCodeTypeId);

            given(timeProvider.now()).willReturn(now);

            // when
            CommonCode result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.commonCodeTypeId().value()).isEqualTo(commonCodeTypeId);
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.displayName().value()).isEqualTo(command.displayName());
            assertThat(result.displayOrder().value()).isEqualTo(command.displayOrder());
            assertThat(result.createdAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - UpdateCommonCodeCommand → UpdateContext 변환")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateCommonCodeCommand로부터 UpdateContext를 생성한다")
        void createUpdateContext_CreatesUpdateContext() {
            // given
            Long id = 100L;
            Instant now = Instant.now();
            UpdateCommonCodeCommand command = CommonCodeCommandFixtures.updateCommand(id);

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<CommonCodeId, CommonCodeUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(id);
            assertThat(result.changedAt()).isEqualTo(now);
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().displayName().value()).isEqualTo(command.displayName());
        }
    }

    @Nested
    @DisplayName(
            "createStatusChangeContexts() - ChangeCommonCodeStatusCommand → StatusChangeContext"
                    + " 목록 변환")
    class CreateStatusChangeContextsTest {

        @Test
        @DisplayName("ChangeCommonCodeStatusCommand로부터 StatusChangeContext 목록을 생성한다")
        void createStatusChangeContexts_CreatesContextList() {
            // given
            Long id1 = 100L;
            Long id2 = 101L;
            Instant now = Instant.now();
            ChangeCommonCodeStatusCommand command =
                    CommonCodeCommandFixtures.activateCommand(id1, id2);

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<CommonCodeId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id().value()).isEqualTo(id1);
            assertThat(result.get(1).id().value()).isEqualTo(id2);
            assertThat(result.get(0).changedAt()).isEqualTo(now);
        }
    }
}
