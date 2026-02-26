package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
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
@DisplayName("RegisterProductGroupFullService 단위 테스트")
class RegisterProductGroupFullServiceTest {

    @InjectMocks private RegisterProductGroupFullService sut;

    @Mock private ProductGroupBundleFactory bundleFactory;
    @Mock private FullProductGroupRegistrationCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 상품 그룹 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 상품 그룹을 등록하고 ID를 반환한다")
        void execute_RegistersProductGroup_ReturnsProductGroupId() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            ProductGroupRegistrationBundle bundle = createRegistrationBundle();
            Long expectedId = 1L;

            given(bundleFactory.createProductGroupBundle(command)).willReturn(bundle);
            given(coordinator.register(bundle))
                    .willReturn(new ProductGroupRegistrationResult(expectedId, List.of()));

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(bundleFactory).should().createProductGroupBundle(command);
            then(coordinator).should().register(bundle);
        }

        @Test
        @DisplayName("NONE 옵션 타입으로도 상품 그룹을 등록한다")
        void execute_WithNoOptionType_RegistersProductGroup() {
            // given
            RegisterProductGroupCommand command =
                    ProductGroupCommandFixtures.registerCommandWithNoOption();
            ProductGroupRegistrationBundle bundle = createRegistrationBundleNoOption();
            Long expectedId = 2L;

            given(bundleFactory.createProductGroupBundle(command)).willReturn(bundle);
            given(coordinator.register(bundle))
                    .willReturn(new ProductGroupRegistrationResult(expectedId, List.of()));

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(bundleFactory).should().createProductGroupBundle(command);
            then(coordinator).should().register(bundle);
        }
    }

    private ProductGroupRegistrationBundle createRegistrationBundle() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                List.of(),
                "SINGLE",
                List.of(),
                "<p>상세설명</p>",
                10L,
                List.of(),
                List.of(),
                CommonVoFixtures.now());
    }

    private ProductGroupRegistrationBundle createRegistrationBundleNoOption() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                List.of(),
                "NONE",
                List.of(),
                "<p>상세설명</p>",
                10L,
                List.of(),
                List.of(),
                CommonVoFixtures.now());
    }
}
