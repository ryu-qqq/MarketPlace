package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
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
@DisplayName("UpdateProductGroupDescriptionService 단위 테스트")
class UpdateProductGroupDescriptionServiceTest {

    @InjectMocks private UpdateProductGroupDescriptionService sut;

    @Mock private ProductGroupDescriptionCommandFactory commandFactory;
    @Mock private ProductGroupDescriptionReadManager readManager;
    @Mock private DescriptionCommandCoordinator descriptionCommandCoordinator;

    @Nested
    @DisplayName("execute() - 상세설명 수정")
    class ExecuteTest {

        @Test
        @DisplayName("수정 커맨드로 UpdateData를 생성하고 기존 Description을 조회한 후 Coordinator에 위임한다")
        void execute_ValidCommand_DelegatesToCoordinator() {
            // given
            UpdateProductGroupDescriptionCommand command =
                    new UpdateProductGroupDescriptionCommand(1L, "<p>수정된 상세설명</p>");
            // 이미지가 없는 Description을 사용 (computeImageDiff 중복 URL 에러 방지)
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            // 이미지 목록이 비어있는 updateData를 실제 객체로 생성
            DescriptionUpdateData updateData =
                    DescriptionUpdateData.of(
                            DescriptionHtml.of("<p>수정된 상세설명</p>"),
                            List.of(),
                            CommonVoFixtures.now());

            given(commandFactory.createUpdateData(command)).willReturn(updateData);
            given(readManager.getByProductGroupId(ProductGroupId.of(command.productGroupId())))
                    .willReturn(description);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateData(command);
            then(readManager)
                    .should()
                    .getByProductGroupId(ProductGroupId.of(command.productGroupId()));
            then(descriptionCommandCoordinator)
                    .should()
                    .update(
                            org.mockito.ArgumentMatchers.eq(description),
                            org.mockito.ArgumentMatchers.any());
        }
    }
}
