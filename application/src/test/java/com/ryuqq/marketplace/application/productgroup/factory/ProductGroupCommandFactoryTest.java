package com.ryuqq.marketplace.application.productgroup.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
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
@DisplayName("ProductGroupCommandFactory 단위 테스트")
class ProductGroupCommandFactoryTest {

    @InjectMocks private ProductGroupCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createUpdateData() - 기본 정보 수정 UpdateData 생성")
    class CreateUpdateDataTest {

        @Test
        @DisplayName("UpdateProductGroupBasicInfoCommand로 ProductGroupUpdateData를 생성한다")
        void createUpdateData_ValidCommand_ReturnsUpdateData() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupBasicInfoCommand command =
                    ProductGroupCommandFixtures.updateBasicInfoCommand(productGroupId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupUpdateData result = sut.createUpdateData(command, OptionType.SINGLE);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId().value()).isEqualTo(productGroupId);
            assertThat(result.productGroupName().value()).isEqualTo(command.productGroupName());
            assertThat(result.brandId().value()).isEqualTo(command.brandId());
            assertThat(result.categoryId().value()).isEqualTo(command.categoryId());
            assertThat(result.shippingPolicyId().value()).isEqualTo(command.shippingPolicyId());
            assertThat(result.refundPolicyId().value()).isEqualTo(command.refundPolicyId());
            assertThat(result.optionType()).isEqualTo(OptionType.SINGLE);
        }

        @Test
        @DisplayName("상품 그룹명이 다른 커맨드로도 UpdateData를 생성한다")
        void createUpdateData_WithDifferentName_ReturnsUpdateData() {
            // given
            long productGroupId = 2L;
            String newName = "새로운 상품 그룹명";
            UpdateProductGroupBasicInfoCommand command =
                    ProductGroupCommandFixtures.updateBasicInfoCommand(productGroupId, newName);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupUpdateData result = sut.createUpdateData(command, OptionType.SINGLE);

            // then
            assertThat(result.productGroupId().value()).isEqualTo(productGroupId);
            assertThat(result.productGroupName().value()).isEqualTo(newName);
        }

        @Test
        @DisplayName("TimeProvider가 제공하는 시간이 UpdateData updatedAt에 반영된다")
        void createUpdateData_UsesTimeProvider() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupBasicInfoCommand command =
                    ProductGroupCommandFixtures.updateBasicInfoCommand(productGroupId);
            Instant specificTime = Instant.parse("2025-01-15T10:00:00Z");
            given(timeProvider.now()).willReturn(specificTime);

            // when
            ProductGroupUpdateData result = sut.createUpdateData(command, OptionType.SINGLE);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updatedAt()).isEqualTo(specificTime);
        }
    }
}
