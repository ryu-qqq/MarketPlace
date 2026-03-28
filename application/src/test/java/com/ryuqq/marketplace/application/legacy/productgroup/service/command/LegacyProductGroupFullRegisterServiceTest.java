package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
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
@DisplayName("LegacyProductGroupFullRegisterService 단위 테스트")
class LegacyProductGroupFullRegisterServiceTest {

    @InjectMocks private LegacyProductGroupFullRegisterService sut;

    @Mock private ProductGroupBundleFactory bundleFactory;
    @Mock private FullProductGroupRegistrationCoordinator registrationCoordinator;

    @Nested
    @DisplayName("execute() - 상품 등록 실행")
    class ExecuteTest {

        @Test
        @DisplayName("표준 Command로 상품을 등록하고 LegacyProductRegistrationResult를 반환한다")
        void execute_ValidCommand_RegistersAndReturnsResult() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            ProductGroupRegistrationBundle bundle = org.mockito.Mockito.mock(ProductGroupRegistrationBundle.class);
            ProductGroupRegistrationResult registrationResult =
                    new ProductGroupRegistrationResult(100L, List.of(201L, 202L));

            given(bundleFactory.createProductGroupBundle(command)).willReturn(bundle);
            given(registrationCoordinator.register(bundle)).willReturn(registrationResult);

            // when
            LegacyProductRegistrationResult result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(registrationResult.productGroupId());
            assertThat(result.sellerId()).isEqualTo(command.sellerId());
            assertThat(result.productIds()).isEqualTo(registrationResult.productIds());
            then(bundleFactory).should().createProductGroupBundle(command);
            then(registrationCoordinator).should().register(bundle);
        }

        @Test
        @DisplayName("등록 결과의 productIds가 빈 목록인 경우도 정상 처리한다")
        void execute_EmptyProductIds_ReturnsResultWithEmptyList() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommandWithNoOption();
            ProductGroupRegistrationBundle bundle = org.mockito.Mockito.mock(ProductGroupRegistrationBundle.class);
            ProductGroupRegistrationResult registrationResult =
                    new ProductGroupRegistrationResult(200L, List.of());

            given(bundleFactory.createProductGroupBundle(command)).willReturn(bundle);
            given(registrationCoordinator.register(bundle)).willReturn(registrationResult);

            // when
            LegacyProductRegistrationResult result = sut.execute(command);

            // then
            assertThat(result.productGroupId()).isEqualTo(200L);
            assertThat(result.productIds()).isEmpty();
        }
    }
}
