package com.ryuqq.marketplace.application.saleschannelcategory.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelcategory.SalesChannelCategoryCommandFixtures;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import com.ryuqq.marketplace.application.saleschannelcategory.factory.SalesChannelCategoryCommandFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryCommandManager;
import com.ryuqq.marketplace.application.saleschannelcategory.validator.SalesChannelCategoryValidator;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
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
@DisplayName("RegisterSalesChannelCategoryService 단위 테스트")
class RegisterSalesChannelCategoryServiceTest {

    @InjectMocks private RegisterSalesChannelCategoryService sut;

    @Mock private SalesChannelCategoryValidator validator;
    @Mock private SalesChannelCategoryCommandFactory commandFactory;
    @Mock private SalesChannelCategoryCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 채널 카테고리 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 외부 채널 카테고리를 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsCategoryId() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerCommand();
            SalesChannelCategory category = SalesChannelCategoryFixtures.newSalesChannelCategory();
            Long expectedCategoryId = 1L;

            given(commandFactory.create(command)).willReturn(category);
            given(commandManager.persist(category)).willReturn(expectedCategoryId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedCategoryId);
            then(validator)
                    .should()
                    .validateExternalCodeNotDuplicate(
                            command.salesChannelId(), command.externalCategoryCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(category);
        }

        @Test
        @DisplayName("하위 카테고리를 등록한다")
        void execute_ChildCategory_ReturnsCategoryId() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerChildCommand(100L);
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.newChildCategory(command.parentId());
            Long expectedCategoryId = 2L;

            given(commandFactory.create(command)).willReturn(category);
            given(commandManager.persist(category)).willReturn(expectedCategoryId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedCategoryId);
            assertThat(command.parentId()).isNotNull();
            assertThat(command.depth()).isEqualTo(2);
        }

        @Test
        @DisplayName("말단 카테고리를 등록한다")
        void execute_LeafCategory_ReturnsCategoryId() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerLeafCommand();
            SalesChannelCategory category = SalesChannelCategoryFixtures.leafCategory();
            Long expectedCategoryId = 3L;

            given(commandFactory.create(command)).willReturn(category);
            given(commandManager.persist(category)).willReturn(expectedCategoryId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedCategoryId);
            assertThat(command.leaf()).isTrue();
        }
    }
}
