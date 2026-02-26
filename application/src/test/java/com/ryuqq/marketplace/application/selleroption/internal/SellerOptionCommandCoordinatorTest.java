package com.ryuqq.marketplace.application.selleroption.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.factory.SellerOptionGroupFactory;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupReadManager;
import com.ryuqq.marketplace.application.selleroption.validator.SellerOptionGroupValidator;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
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
@DisplayName("SellerOptionCommandCoordinator 단위 테스트")
class SellerOptionCommandCoordinatorTest {

    @InjectMocks private SellerOptionCommandCoordinator sut;

    @Mock private SellerOptionGroupFactory optionGroupFactory;
    @Mock private SellerOptionGroupValidator validator;
    @Mock private SellerOptionGroupReadManager readManager;
    @Mock private SellerOptionPersistFacade persistFacade;

    @Nested
    @DisplayName("register() - 옵션 그룹 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("등록 Command로 옵션 그룹을 생성하고 검증 후 저장한다")
        void register_ValidCommand_ReturnsValueIds() {
            // given
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups =
                    List.of(
                            new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                    "색상",
                                    null,
                                    null,
                                    List.of(
                                            new RegisterSellerOptionGroupsCommand
                                                    .OptionValueCommand("검정", null, 0))));
            RegisterSellerOptionGroupsCommand command =
                    new RegisterSellerOptionGroupsCommand(1L, "SINGLE", optionGroups);

            SellerOptionGroups groups =
                    SellerOptionGroups.of(List.of(ProductGroupFixtures.defaultSellerOptionGroup()));
            List<SellerOptionValueId> expectedIds = List.of(SellerOptionValueId.of(10L));

            given(optionGroupFactory.createFromRegistration(any(ProductGroupId.class), anyList()))
                    .willReturn(groups);
            willDoNothing().given(validator).validate(groups, OptionType.SINGLE);
            given(persistFacade.persistAll(groups.groups())).willReturn(expectedIds);

            // when
            List<SellerOptionValueId> result = sut.register(command);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(optionGroupFactory)
                    .should()
                    .createFromRegistration(any(ProductGroupId.class), anyList());
            then(validator).should().validate(groups, OptionType.SINGLE);
            then(persistFacade).should().persistAll(groups.groups());
        }
    }
}
