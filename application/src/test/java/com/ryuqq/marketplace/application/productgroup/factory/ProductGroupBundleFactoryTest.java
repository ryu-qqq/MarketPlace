package com.ryuqq.marketplace.application.productgroup.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
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
@DisplayName("ProductGroupBundleFactory 단위 테스트")
class ProductGroupBundleFactoryTest {

    @InjectMocks private ProductGroupBundleFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createProductGroupBundle() - 등록 번들 생성")
    class CreateProductGroupBundleTest {

        @Test
        @DisplayName("RegisterProductGroupCommand로 ProductGroupRegistrationBundle을 생성한다")
        void createProductGroupBundle_ValidCommand_ReturnsBundle() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroup()).isNotNull();
            assertThat(result.productGroup().productGroupNameValue())
                    .isEqualTo(command.productGroupName());
            assertThat(result.productGroup().sellerIdValue()).isEqualTo(command.sellerId());
            assertThat(result.productGroup().brandIdValue()).isEqualTo(command.brandId());
            assertThat(result.productGroup().id().isNew()).isTrue();
        }

        @Test
        @DisplayName("이미지 Command가 번들 이미지 Command에 매핑된다")
        void createProductGroupBundle_MapsImageCommands() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result.imageCommand()).isNotNull();
            assertThat(result.imageCommand().productGroupId()).isEqualTo(0L);
            assertThat(result.imageCommand().images()).hasSize(command.images().size());
        }

        @Test
        @DisplayName("옵션 그룹 Command가 번들 옵션 Command에 매핑된다")
        void createProductGroupBundle_MapsOptionGroupCommands() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result.optionGroupCommand()).isNotNull();
            assertThat(result.optionGroupCommand().optionType()).isEqualTo(command.optionType());
            assertThat(result.optionGroupCommand().optionGroups())
                    .hasSize(command.optionGroups().size());
        }

        @Test
        @DisplayName("상세설명 Command가 번들 상세설명 Command에 매핑된다")
        void createProductGroupBundle_MapsDescriptionCommand() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result.descriptionCommand()).isNotNull();
            assertThat(result.descriptionCommand().content())
                    .isEqualTo(command.description().content());
        }

        @Test
        @DisplayName("고시정보 Command가 번들 고시정보 Command에 매핑된다")
        void createProductGroupBundle_MapsNoticeCommand() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result.noticeCommand()).isNotNull();
            assertThat(result.noticeCommand().noticeCategoryId())
                    .isEqualTo(command.notice().noticeCategoryId());
            assertThat(result.noticeCommand().entries()).hasSize(command.notice().entries().size());
        }

        @Test
        @DisplayName("번들에 createdAt이 포함되어 Outbox 생성에 사용된다")
        void createProductGroupBundle_IncludesCreatedAt() {
            // given
            RegisterProductGroupCommand command = ProductGroupCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("NONE 옵션 타입으로도 번들을 생성한다")
        void createProductGroupBundle_WithNoOptionType_ReturnsBundle() {
            // given
            RegisterProductGroupCommand command =
                    ProductGroupCommandFixtures.registerCommandWithNoOption();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupRegistrationBundle result = sut.createProductGroupBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.optionGroupCommand().optionType()).isEqualTo("NONE");
            assertThat(result.optionGroupCommand().optionGroups()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createUpdateBundle() - 수정 번들 생성")
    class CreateUpdateBundleTest {

        @Test
        @DisplayName("UpdateProductGroupFullCommand로 ProductGroupUpdateBundle을 생성한다")
        void createUpdateBundle_ValidCommand_ReturnsBundle() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupFullCommand command =
                    ProductGroupCommandFixtures.updateFullCommand(productGroupId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupUpdateBundle result = sut.createUpdateBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.basicInfoUpdateData()).isNotNull();
            assertThat(result.basicInfoUpdateData().productGroupId().value())
                    .isEqualTo(productGroupId);
            assertThat(result.basicInfoUpdateData().productGroupName().value())
                    .isEqualTo(command.productGroupName());
        }

        @Test
        @DisplayName("이미지 수정 Command가 번들 이미지 Command에 매핑된다")
        void createUpdateBundle_MapsImageCommands() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupFullCommand command =
                    ProductGroupCommandFixtures.updateFullCommand(productGroupId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupUpdateBundle result = sut.createUpdateBundle(command);

            // then
            assertThat(result.imageCommand()).isNotNull();
            assertThat(result.imageCommand().productGroupId()).isEqualTo(productGroupId);
            assertThat(result.imageCommand().images()).hasSize(command.images().size());
        }

        @Test
        @DisplayName("상세설명 수정 Command가 번들 상세설명 Command에 매핑된다")
        void createUpdateBundle_MapsDescriptionCommand() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupFullCommand command =
                    ProductGroupCommandFixtures.updateFullCommand(productGroupId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupUpdateBundle result = sut.createUpdateBundle(command);

            // then
            assertThat(result.descriptionCommand()).isNotNull();
            assertThat(result.descriptionCommand().productGroupId()).isEqualTo(productGroupId);
            assertThat(result.descriptionCommand().content())
                    .isEqualTo(command.description().content());
        }
    }
}
