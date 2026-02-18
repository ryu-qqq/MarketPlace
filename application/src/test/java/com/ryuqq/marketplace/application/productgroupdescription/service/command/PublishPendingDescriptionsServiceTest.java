package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.PublishPendingDescriptionsCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionPublishCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
@DisplayName("PublishPendingDescriptionsService 단위 테스트")
class PublishPendingDescriptionsServiceTest {

    @InjectMocks private PublishPendingDescriptionsService sut;

    @Mock private ProductGroupDescriptionReadManager descriptionReadManager;
    @Mock private DescriptionPublishCoordinator descriptionPublishCoordinator;

    @Nested
    @DisplayName("execute() - PUBLISH_READY 상태 Description CDN 퍼블리시")
    class ExecuteTest {

        @Test
        @DisplayName("PUBLISH_READY 상태의 Description을 모두 퍼블리시하고 성공 결과를 반환한다")
        void execute_AllSucceeds_ReturnsAllSuccessResult() {
            // given
            PublishPendingDescriptionsCommand command = new PublishPendingDescriptionsCommand(10);
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            List<ProductGroupDescription> descriptions = List.of(description);

            given(descriptionReadManager.findPublishReady(10)).willReturn(descriptions);
            given(descriptionPublishCoordinator.publish(description)).willReturn(true);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isZero();
        }

        @Test
        @DisplayName("퍼블리시가 실패하면 실패 카운트가 증가한다")
        void execute_PublishFails_IncreasesFailureCount() {
            // given
            PublishPendingDescriptionsCommand command = new PublishPendingDescriptionsCommand(10);
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            List<ProductGroupDescription> descriptions = List.of(description);

            given(descriptionReadManager.findPublishReady(10)).willReturn(descriptions);
            given(descriptionPublishCoordinator.publish(description)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }

        @Test
        @DisplayName("PUBLISH_READY 상태의 Description이 없으면 빈 결과를 반환한다")
        void execute_NoDescriptions_ReturnsEmptyResult() {
            // given
            PublishPendingDescriptionsCommand command = new PublishPendingDescriptionsCommand(10);
            given(descriptionReadManager.findPublishReady(10)).willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
            assertThat(result.failed()).isZero();
            then(descriptionPublishCoordinator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("일부 퍼블리시가 실패하면 성공/실패 카운트가 각각 반영된다")
        void execute_PartialFailure_ReturnsPartialResult() {
            // given
            PublishPendingDescriptionsCommand command = new PublishPendingDescriptionsCommand(10);
            ProductGroupDescription desc1 = ProductGroupFixtures.defaultProductGroupDescription();
            ProductGroupDescription desc2 = ProductGroupFixtures.defaultProductGroupDescription();
            List<ProductGroupDescription> descriptions = List.of(desc1, desc2);

            given(descriptionReadManager.findPublishReady(10)).willReturn(descriptions);
            given(descriptionPublishCoordinator.publish(desc1)).willReturn(true);
            given(descriptionPublishCoordinator.publish(desc2)).willReturn(false);

            // when
            SchedulerBatchProcessingResult result = sut.execute(command);

            // then
            assertThat(result.total()).isEqualTo(2);
            assertThat(result.success()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
