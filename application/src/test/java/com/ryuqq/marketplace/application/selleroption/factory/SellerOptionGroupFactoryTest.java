package com.ryuqq.marketplace.application.selleroption.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
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
@DisplayName("SellerOptionGroupFactory 단위 테스트")
class SellerOptionGroupFactoryTest {

    @InjectMocks private SellerOptionGroupFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createFromRegistration() - 등록 Command로 SellerOptionGroups 생성")
    class CreateFromRegistrationTest {

        @Test
        @DisplayName("등록 Command의 옵션 그룹 목록으로 SellerOptionGroups를 생성한다")
        void createFromRegistration_ValidCommand_ReturnsSellerOptionGroups() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups =
                    List.of(
                            new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                    "색상",
                                    null,
                                    null,
                                    List.of(
                                            new RegisterSellerOptionGroupsCommand
                                                    .OptionValueCommand("검정", null, 0))));

            // when
            SellerOptionGroups result = sut.createFromRegistration(productGroupId, optionGroups);

            // then
            assertThat(result).isNotNull();
            assertThat(result.groups()).hasSize(1);
            assertThat(result.groups().get(0).optionGroupNameValue()).isEqualTo("색상");
        }

        @Test
        @DisplayName("캐노니컬 ID가 있으면 매핑된 SellerOptionGroup을 생성한다")
        void createFromRegistration_WithCanonicalIds_ReturnsMappedGroups() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups =
                    List.of(
                            new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                    "색상",
                                    1L,
                                    null,
                                    List.of(
                                            new RegisterSellerOptionGroupsCommand
                                                    .OptionValueCommand("검정", 1L, 0))));

            // when
            SellerOptionGroups result = sut.createFromRegistration(productGroupId, optionGroups);

            // then
            assertThat(result).isNotNull();
            assertThat(result.groups().get(0).isFullyMapped()).isTrue();
        }
    }

    @Nested
    @DisplayName("toUpdateData() - 수정 Command로 SellerOptionGroupUpdateData 생성")
    class ToUpdateDataTest {

        @Test
        @DisplayName("수정 Command로 SellerOptionGroupUpdateData를 생성한다")
        void toUpdateData_ValidCommand_ReturnsUpdateData() {
            // given
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups =
                    List.of(
                            new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                                    1L,
                                    "색상",
                                    null,
                                    null,
                                    List.of(
                                            new UpdateSellerOptionGroupsCommand.OptionValueCommand(
                                                    1L, "검정", null, 0))));

            // when
            SellerOptionGroupUpdateData result = sut.toUpdateData(productGroupId, optionGroups);

            // then
            assertThat(result).isNotNull();
            assertThat(result.groupEntries()).hasSize(1);
        }
    }
}
