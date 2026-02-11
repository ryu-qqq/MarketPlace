package com.ryuqq.marketplace.application.saleschannelbrand.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelbrand.SalesChannelBrandCommandFixtures;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import com.ryuqq.marketplace.application.saleschannelbrand.factory.SalesChannelBrandCommandFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandCommandManager;
import com.ryuqq.marketplace.application.saleschannelbrand.validator.SalesChannelBrandValidator;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
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
@DisplayName("RegisterSalesChannelBrandService 단위 테스트")
class RegisterSalesChannelBrandServiceTest {

    @InjectMocks private RegisterSalesChannelBrandService sut;

    @Mock private SalesChannelBrandValidator validator;
    @Mock private SalesChannelBrandCommandFactory commandFactory;
    @Mock private SalesChannelBrandCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부채널 브랜드 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 외부채널 브랜드를 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsBrandId() {
            // given
            RegisterSalesChannelBrandCommand command =
                    SalesChannelBrandCommandFixtures.registerCommand();
            SalesChannelBrand brand = SalesChannelBrandFixtures.newSalesChannelBrand();
            Long expectedBrandId = 1L;

            given(commandFactory.create(command)).willReturn(brand);
            given(commandManager.persist(brand)).willReturn(expectedBrandId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedBrandId);
            then(validator)
                    .should()
                    .validateExternalCodeNotDuplicate(
                            command.salesChannelId(), command.externalBrandCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(brand);
        }

        @Test
        @DisplayName("다른 판매채널 ID로 브랜드를 등록한다")
        void execute_DifferentSalesChannelId_ReturnsBrandId() {
            // given
            RegisterSalesChannelBrandCommand command =
                    SalesChannelBrandCommandFixtures.registerCommand(2L, "BRAND-002", "다른 브랜드");
            SalesChannelBrand brand =
                    SalesChannelBrandFixtures.newSalesChannelBrand(2L, "BRAND-002", "다른 브랜드");
            Long expectedBrandId = 2L;

            given(commandFactory.create(command)).willReturn(brand);
            given(commandManager.persist(brand)).willReturn(expectedBrandId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedBrandId);
            then(validator)
                    .should()
                    .validateExternalCodeNotDuplicate(
                            command.salesChannelId(), command.externalBrandCode());
        }
    }
}
