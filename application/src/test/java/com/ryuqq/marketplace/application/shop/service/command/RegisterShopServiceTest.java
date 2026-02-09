package com.ryuqq.marketplace.application.shop.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.ShopCommandFixtures;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.factory.ShopCommandFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopWriteManager;
import com.ryuqq.marketplace.application.shop.validator.ShopValidator;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.time.Instant;
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
@DisplayName("RegisterShopService 단위 테스트")
class RegisterShopServiceTest {

    @InjectMocks private RegisterShopService sut;

    @Mock private ShopValidator validator;
    @Mock private ShopCommandFactory commandFactory;
    @Mock private ShopWriteManager writeManager;

    @Nested
    @DisplayName("execute() - Shop 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 Shop을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsShopId() {
            // given
            RegisterShopCommand command = ShopCommandFixtures.registerCommand();
            Shop newShop = createNewShop();
            Long expectedShopId = 1L;

            given(commandFactory.create(command)).willReturn(newShop);
            given(writeManager.persist(newShop)).willReturn(expectedShopId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedShopId);
            then(validator).should().validateShopNameNotDuplicate(command.shopName());
            then(validator).should().validateAccountIdNotDuplicate(command.accountId());
            then(commandFactory).should().create(command);
            then(writeManager).should().persist(newShop);
        }

        @Test
        @DisplayName("Shop명과 계정ID 중복 검증을 수행한다")
        void execute_ValidatesShopNameAndAccountId() {
            // given
            RegisterShopCommand command =
                    ShopCommandFixtures.registerCommand("신규외부몰", "new-account-999");
            Shop newShop = createNewShop();
            Long expectedShopId = 1L;

            given(commandFactory.create(command)).willReturn(newShop);
            given(writeManager.persist(newShop)).willReturn(expectedShopId);

            // when
            sut.execute(command);

            // then
            then(validator).should().validateShopNameNotDuplicate("신규외부몰");
            then(validator).should().validateAccountIdNotDuplicate("new-account-999");
        }

        private Shop createNewShop() {
            Instant now = Instant.now();
            return Shop.reconstitute(
                    ShopId.forNew(),
                    "테스트 외부몰",
                    "test-account-123",
                    com.ryuqq.marketplace.domain.shop.vo.ShopStatus.ACTIVE,
                    null,
                    now,
                    now);
        }
    }
}
