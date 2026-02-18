package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BatchRegisterProductGroupFullService 단위 테스트")
class BatchRegisterProductGroupFullServiceTest {

    private BatchRegisterProductGroupFullService sut;

    @Mock private ProductGroupBundleFactory bundleFactory;
    @Mock private FullProductGroupRegistrationCoordinator coordinator;

    @BeforeEach
    void setUp() {
        ExecutorService directExecutor = Executors.newSingleThreadExecutor();
        sut = new BatchRegisterProductGroupFullService(bundleFactory, coordinator, directExecutor);
    }

    @Nested
    @DisplayName("execute() - 상품 그룹 배치 등록")
    class ExecuteTest {

        @Test
        @DisplayName("모든 커맨드가 성공하면 전체 성공 결과를 반환한다")
        void execute_AllCommandsSucceed_ReturnsAllSuccessResult() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(3);

            ProductGroupRegistrationBundle bundle = createRegistrationBundle();

            given(bundleFactory.createProductGroupBundle(commands.get(0))).willReturn(bundle);
            given(bundleFactory.createProductGroupBundle(commands.get(1))).willReturn(bundle);
            given(bundleFactory.createProductGroupBundle(commands.get(2))).willReturn(bundle);
            given(coordinator.register(bundle)).willReturn(1L, 2L, 3L);

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.successCount()).isEqualTo(3);
            assertThat(result.failureCount()).isZero();
            assertThat(result.results()).hasSize(3);
            assertThat(result.results()).allMatch(r -> r.success());
        }

        @Test
        @DisplayName("단일 커맨드 성공 시 성공 결과와 ID를 반환한다")
        void execute_SingleCommandSucceeds_ReturnsSuccessResultWithId() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(1);

            ProductGroupRegistrationBundle bundle = createRegistrationBundle();
            Long expectedId = 100L;

            given(bundleFactory.createProductGroupBundle(commands.get(0))).willReturn(bundle);
            given(coordinator.register(bundle)).willReturn(expectedId);

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isZero();

            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isTrue();
            assertThat(itemResult.id()).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("빈 커맨드 목록이면 빈 배치 결과를 반환한다")
        void execute_EmptyCommands_ReturnsEmptyBatchResult() {
            // given
            List<RegisterProductGroupCommand> commands = List.of();

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isZero();
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isZero();
            assertThat(result.results()).isEmpty();

            then(bundleFactory).shouldHaveNoInteractions();
            then(coordinator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("DomainException 발생 시 해당 항목만 실패 처리되고 나머지는 성공한다")
        void execute_DomainExceptionOnOneCommand_FailsThatItemAndSucceedsOthers() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(3);

            ProductGroupRegistrationBundle bundle = createRegistrationBundle();

            given(bundleFactory.createProductGroupBundle(commands.get(0))).willReturn(bundle);
            given(bundleFactory.createProductGroupBundle(commands.get(1)))
                    .willThrow(new ProductGroupNotFoundException(99L));
            given(bundleFactory.createProductGroupBundle(commands.get(2))).willReturn(bundle);
            given(coordinator.register(bundle)).willReturn(1L, 3L);

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isEqualTo(1);

            long failCount = result.results().stream().filter(r -> !r.success()).count();
            assertThat(failCount).isEqualTo(1);
        }

        @Test
        @DisplayName("DomainException 발생 시 에러 코드가 도메인 에러 코드로 기록된다")
        void execute_DomainException_RecordsDomainErrorCode() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(1);

            ProductGroupNotFoundException domainException = new ProductGroupNotFoundException(99L);
            given(bundleFactory.createProductGroupBundle(commands.get(0)))
                    .willThrow(domainException);

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(1);

            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isFalse();
            assertThat(itemResult.errorCode()).isEqualTo(domainException.code());
            assertThat(itemResult.errorMessage()).isNotBlank();
        }

        @Test
        @DisplayName("일반 Exception 발생 시 INTERNAL_ERROR 코드로 실패 처리된다")
        void execute_UnexpectedException_RecordsInternalErrorCode() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(1);

            given(bundleFactory.createProductGroupBundle(commands.get(0)))
                    .willThrow(new RuntimeException("예기치 못한 오류"));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(1);

            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isFalse();
            assertThat(itemResult.errorCode()).isEqualTo("INTERNAL_ERROR");
        }

        @Test
        @DisplayName("일반 Exception 발생 시 id는 null로 기록된다")
        void execute_UnexpectedException_RecordsNullId() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(1);

            given(bundleFactory.createProductGroupBundle(commands.get(0)))
                    .willThrow(new RuntimeException("예기치 못한 오류"));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.id()).isNull();
        }

        @Test
        @DisplayName("모든 커맨드가 실패하면 전체 실패 결과를 반환한다")
        void execute_AllCommandsFail_ReturnsAllFailureResult() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(2);

            given(bundleFactory.createProductGroupBundle(any()))
                    .willThrow(new ProductGroupNotFoundException(99L));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isZero();
            assertThat(result.failureCount()).isEqualTo(2);
            assertThat(result.results()).noneMatch(r -> r.success());
        }

        @Test
        @DisplayName("각 커맨드마다 BundleFactory를 독립적으로 호출한다")
        void execute_MultipleCommands_CallsBundleFactoryForEachCommand() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommands(3);

            ProductGroupRegistrationBundle bundle = createRegistrationBundle();

            given(bundleFactory.createProductGroupBundle(any())).willReturn(bundle);
            given(coordinator.register(any())).willReturn(1L);

            // when
            sut.execute(commands);

            // then
            then(bundleFactory)
                    .should(org.mockito.Mockito.times(3))
                    .createProductGroupBundle(any());
        }

        @Test
        @DisplayName("NONE 옵션 타입 커맨드도 배치 등록에 포함된다")
        void execute_CommandsWithNoOption_IncludesInBatchResult() {
            // given
            List<RegisterProductGroupCommand> commands =
                    ProductGroupCommandFixtures.batchRegisterCommandsWithNoOption(2);

            ProductGroupRegistrationBundle bundle = createRegistrationBundleNoOption();

            given(bundleFactory.createProductGroupBundle(commands.get(0))).willReturn(bundle);
            given(bundleFactory.createProductGroupBundle(commands.get(1))).willReturn(bundle);
            given(coordinator.register(bundle)).willReturn(10L, 11L);

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isZero();
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupRegistrationBundle createRegistrationBundle() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                new RegisterProductGroupImagesCommand(0L, List.of()),
                new RegisterSellerOptionGroupsCommand(0L, "SINGLE", List.of()),
                new RegisterProductGroupDescriptionCommand(0L, "<p>상세설명</p>"),
                new RegisterProductNoticeCommand(0L, 10L, List.of()),
                new RegisterProductsCommand(0L, List.of(), List.of()),
                CommonVoFixtures.now());
    }

    private ProductGroupRegistrationBundle createRegistrationBundleNoOption() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                new RegisterProductGroupImagesCommand(0L, List.of()),
                new RegisterSellerOptionGroupsCommand(0L, "NONE", List.of()),
                new RegisterProductGroupDescriptionCommand(0L, "<p>상세설명</p>"),
                new RegisterProductNoticeCommand(0L, 10L, List.of()),
                new RegisterProductsCommand(0L, List.of(), List.of()),
                CommonVoFixtures.now());
    }
}
