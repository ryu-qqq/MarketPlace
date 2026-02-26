package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.List;
import java.util.Optional;
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
    @Mock private ShippingPolicyReadManager shippingPolicyReadManager;
    @Mock private RefundPolicyReadManager refundPolicyReadManager;

    @BeforeEach
    void setUp() {
        ExecutorService directExecutor = Executors.newSingleThreadExecutor();
        sut =
                new BatchRegisterProductGroupFullService(
                        bundleFactory,
                        coordinator,
                        shippingPolicyReadManager,
                        refundPolicyReadManager,
                        directExecutor);
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
            given(coordinator.register(bundle))
                    .willReturn(
                            new ProductGroupRegistrationResult(1L, List.of()),
                            new ProductGroupRegistrationResult(2L, List.of()),
                            new ProductGroupRegistrationResult(3L, List.of()));

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
            given(coordinator.register(bundle))
                    .willReturn(new ProductGroupRegistrationResult(expectedId, List.of()));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isZero();

            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isTrue();
            assertThat(itemResult.id()).isEqualTo(expectedId);
            assertThat(itemResult.itemName()).isNotBlank();
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
            given(coordinator.register(bundle))
                    .willReturn(
                            new ProductGroupRegistrationResult(1L, List.of()),
                            new ProductGroupRegistrationResult(3L, List.of()));

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
            assertThat(itemResult.id()).isNull();
            assertThat(itemResult.itemName()).isNotBlank();
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
            assertThat(itemResult.itemName()).isNotBlank();
        }

        @Test
        @DisplayName("일반 Exception 발생 시 id는 null이고 itemName은 유지된다")
        void execute_UnexpectedException_RecordsNullIdButKeepsItemName() {
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
            assertThat(itemResult.itemName()).isNotBlank();
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
            given(coordinator.register(any()))
                    .willReturn(new ProductGroupRegistrationResult(1L, List.of()));

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
            given(coordinator.register(bundle))
                    .willReturn(
                            new ProductGroupRegistrationResult(10L, List.of()),
                            new ProductGroupRegistrationResult(11L, List.of()));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.successCount()).isEqualTo(2);
            assertThat(result.failureCount()).isZero();
        }

        @Test
        @DisplayName("미해결 정책 ID(0)인 경우 셀러 기본 정책으로 보정 후 등록한다")
        void execute_UnresolvedPolicyIds_ResolvesDefaultPolicies() {
            // given
            RegisterProductGroupCommand unresolvedCommand =
                    new RegisterProductGroupCommand(
                            ProductGroupCommandFixtures.DEFAULT_SELLER_ID,
                            ProductGroupCommandFixtures.DEFAULT_BRAND_ID,
                            ProductGroupCommandFixtures.DEFAULT_CATEGORY_ID,
                            0L,
                            0L,
                            ProductGroupCommandFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                            ProductGroupCommandFixtures.DEFAULT_OPTION_TYPE,
                            ProductGroupCommandFixtures.defaultImageCommands(),
                            ProductGroupCommandFixtures.defaultOptionGroupCommands(),
                            ProductGroupCommandFixtures.defaultProductCommands(),
                            ProductGroupCommandFixtures.defaultDescriptionCommand(),
                            ProductGroupCommandFixtures.defaultNoticeCommand());
            List<RegisterProductGroupCommand> commands = List.of(unresolvedCommand);

            ShippingPolicy shippingPolicy = org.mockito.Mockito.mock(ShippingPolicy.class);
            given(shippingPolicy.id()).willReturn(ShippingPolicyId.of(11L));
            RefundPolicy refundPolicy = org.mockito.Mockito.mock(RefundPolicy.class);
            given(refundPolicy.id()).willReturn(RefundPolicyId.of(22L));
            given(shippingPolicyReadManager.findDefaultBySellerId(SellerId.of(1L)))
                    .willReturn(Optional.of(shippingPolicy));
            given(refundPolicyReadManager.findDefaultBySellerId(SellerId.of(1L)))
                    .willReturn(Optional.of(refundPolicy));

            ProductGroupRegistrationBundle bundle = createRegistrationBundle();
            given(bundleFactory.createProductGroupBundle(any())).willReturn(bundle);
            given(coordinator.register(bundle))
                    .willReturn(new ProductGroupRegistrationResult(100L, List.of()));

            // when
            BatchProcessingResult<Long> result = sut.execute(commands);

            // then
            assertThat(result.successCount()).isEqualTo(1);
            then(shippingPolicyReadManager).should().findDefaultBySellerId(SellerId.of(1L));
            then(refundPolicyReadManager).should().findDefaultBySellerId(SellerId.of(1L));
        }

        @Test
        @DisplayName("기본 배송 정책이 없으면 SHP-015 에러로 실패 처리된다")
        void execute_DefaultShippingPolicyMissing_ReturnsSpecificError() {
            // given
            RegisterProductGroupCommand unresolvedCommand =
                    new RegisterProductGroupCommand(
                            ProductGroupCommandFixtures.DEFAULT_SELLER_ID,
                            ProductGroupCommandFixtures.DEFAULT_BRAND_ID,
                            ProductGroupCommandFixtures.DEFAULT_CATEGORY_ID,
                            0L,
                            0L,
                            ProductGroupCommandFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                            ProductGroupCommandFixtures.DEFAULT_OPTION_TYPE,
                            ProductGroupCommandFixtures.defaultImageCommands(),
                            ProductGroupCommandFixtures.defaultOptionGroupCommands(),
                            ProductGroupCommandFixtures.defaultProductCommands(),
                            ProductGroupCommandFixtures.defaultDescriptionCommand(),
                            ProductGroupCommandFixtures.defaultNoticeCommand());

            given(shippingPolicyReadManager.findDefaultBySellerId(SellerId.of(1L)))
                    .willReturn(Optional.empty());

            // when
            BatchProcessingResult<Long> result = sut.execute(List.of(unresolvedCommand));

            // then
            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isFalse();
            assertThat(itemResult.errorCode()).isEqualTo("SHP-015");
            assertThat(itemResult.errorMessage()).contains("기본 배송 정책이 없습니다");
            assertThat(itemResult.itemName()).isNotBlank();
        }

        @Test
        @DisplayName("기본 환불 정책이 없으면 RFP-015 에러로 실패 처리된다")
        void execute_DefaultRefundPolicyMissing_ReturnsSpecificError() {
            // given
            RegisterProductGroupCommand unresolvedCommand =
                    new RegisterProductGroupCommand(
                            ProductGroupCommandFixtures.DEFAULT_SELLER_ID,
                            ProductGroupCommandFixtures.DEFAULT_BRAND_ID,
                            ProductGroupCommandFixtures.DEFAULT_CATEGORY_ID,
                            0L,
                            0L,
                            ProductGroupCommandFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                            ProductGroupCommandFixtures.DEFAULT_OPTION_TYPE,
                            ProductGroupCommandFixtures.defaultImageCommands(),
                            ProductGroupCommandFixtures.defaultOptionGroupCommands(),
                            ProductGroupCommandFixtures.defaultProductCommands(),
                            ProductGroupCommandFixtures.defaultDescriptionCommand(),
                            ProductGroupCommandFixtures.defaultNoticeCommand());

            ShippingPolicy shippingPolicy = org.mockito.Mockito.mock(ShippingPolicy.class);
            given(shippingPolicy.id()).willReturn(ShippingPolicyId.of(11L));
            given(shippingPolicyReadManager.findDefaultBySellerId(SellerId.of(1L)))
                    .willReturn(Optional.of(shippingPolicy));
            given(refundPolicyReadManager.findDefaultBySellerId(SellerId.of(1L)))
                    .willReturn(Optional.empty());

            // when
            BatchProcessingResult<Long> result = sut.execute(List.of(unresolvedCommand));

            // then
            BatchItemResult<Long> itemResult = result.results().get(0);
            assertThat(itemResult.success()).isFalse();
            assertThat(itemResult.errorCode()).isEqualTo("RFP-015");
            assertThat(itemResult.errorMessage()).contains("기본 환불 정책이 없습니다");
            assertThat(itemResult.itemName()).isNotBlank();
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupRegistrationBundle createRegistrationBundle() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                List.of(),
                "SINGLE",
                List.of(),
                "<p>상세설명</p>",
                10L,
                List.of(),
                List.of(),
                CommonVoFixtures.now());
    }

    private ProductGroupRegistrationBundle createRegistrationBundleNoOption() {
        return new ProductGroupRegistrationBundle(
                ProductGroupFixtures.newProductGroup(),
                List.of(),
                "NONE",
                List.of(),
                "<p>상세설명</p>",
                10L,
                List.of(),
                List.of(),
                CommonVoFixtures.now());
    }
}
