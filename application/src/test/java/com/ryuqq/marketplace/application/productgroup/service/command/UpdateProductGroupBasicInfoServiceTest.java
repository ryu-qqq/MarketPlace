package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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
@DisplayName("UpdateProductGroupBasicInfoService 단위 테스트")
class UpdateProductGroupBasicInfoServiceTest {

    @InjectMocks private UpdateProductGroupBasicInfoService sut;

    @Mock private ProductGroupCommandFactory commandFactory;
    @Mock private ProductGroupCommandCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 상품 그룹 기본 정보 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 상품 그룹 기본 정보를 수정한다")
        void execute_UpdatesBasicInfo_NoReturn() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupBasicInfoCommand command =
                    ProductGroupCommandFixtures.updateBasicInfoCommand(productGroupId);
            ProductGroupUpdateData updateData = createUpdateData(productGroupId);

            given(commandFactory.createUpdateData(command)).willReturn(updateData);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateData(command);
            then(coordinator).should().update(updateData);
        }

        @Test
        @DisplayName("상품 그룹명이 변경된 커맨드로도 수정이 동작한다")
        void execute_WithChangedName_UpdatesBasicInfo() {
            // given
            long productGroupId = 2L;
            UpdateProductGroupBasicInfoCommand command =
                    ProductGroupCommandFixtures.updateBasicInfoCommand(productGroupId, "수정된 상품명");
            ProductGroupUpdateData updateData = createUpdateData(productGroupId);

            given(commandFactory.createUpdateData(command)).willReturn(updateData);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateData(command);
            then(coordinator).should().update(updateData);
        }
    }

    private ProductGroupUpdateData createUpdateData(long productGroupId) {
        return ProductGroupUpdateData.of(
                ProductGroupId.of(productGroupId),
                ProductGroupFixtures.defaultProductGroupName(),
                com.ryuqq.marketplace.domain.brand.id.BrandId.of(
                        ProductGroupFixtures.DEFAULT_BRAND_ID),
                com.ryuqq.marketplace.domain.category.id.CategoryId.of(
                        ProductGroupFixtures.DEFAULT_CATEGORY_ID),
                com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId.of(
                        ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID),
                com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId.of(
                        ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID),
                Instant.now());
    }
}
