package com.ryuqq.marketplace.application.outboundsync.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundsync.OutboundSyncExecutionContextFixtures;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.SalesChannelMappingResult;
import com.ryuqq.marketplace.application.outboundsync.manager.SalesChannelProductClientManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@DisplayName("SellicCreateProductStrategy 단위 테스트")
class SellicCreateProductStrategyTest {

    @InjectMocks private SellicCreateProductStrategy sut;

    @Mock private ProductGroupReadFacade productGroupReadFacade;
    @Mock private OutboundMappingResolver mappingResolver;
    @Mock private SalesChannelProductClientManager productClientManager;

    @Nested
    @DisplayName("supports() - 채널/SyncType 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SELLIC + CREATE 조합은 true를 반환한다")
        void supports_SellicCreate_ReturnsTrue() {
            boolean result = sut.supports("SELLIC", SyncType.CREATE);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SELLIC + UPDATE 조합은 false를 반환한다")
        void supports_SellicUpdate_ReturnsFalse() {
            boolean result = sut.supports("SELLIC", SyncType.UPDATE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("NAVER + CREATE 조합은 false를 반환한다")
        void supports_NaverCreate_ReturnsFalse() {
            boolean result = sut.supports("NAVER", SyncType.CREATE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SELLIC + DELETE 조합은 false를 반환한다")
        void supports_SellicDelete_ReturnsFalse() {
            boolean result = sut.supports("SELLIC", SyncType.DELETE);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("execute() - 상품 등록 실행")
    class ExecuteTest {

        @Test
        @DisplayName("정상 흐름에서 외부 상품 ID를 포함한 성공 결과를 반환한다")
        void execute_ValidContext_ReturnsSuccessResult() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicCreateContext();
            ProductGroupDetailBundle bundle = defaultDetailBundle();
            SalesChannelMappingResult mapping =
                    new SalesChannelMappingResult(
                            OutboundSyncExecutionContextFixtures.DEFAULT_CATEGORY_ID,
                            OutboundSyncExecutionContextFixtures.DEFAULT_BRAND_ID,
                            OutboundSyncExecutionContextFixtures.DEFAULT_EXTERNAL_CATEGORY_CODE,
                            OutboundSyncExecutionContextFixtures.DEFAULT_EXTERNAL_BRAND_CODE);
            String externalProductId = "SELLIC-EXT-001";

            given(productGroupReadFacade.getDetailBundle(context.productGroupId()))
                    .willReturn(bundle);
            given(mappingResolver.resolve(anyLong(), anyLong(), anyLong())).willReturn(mapping);
            given(
                            productClientManager.registerProduct(
                                    eq("SELLIC"), any(), anyLong(), anyLong(), any(), any()))
                    .willReturn(externalProductId);

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isTrue();
            assertThat(result.externalProductId()).isEqualTo(externalProductId);
            then(productGroupReadFacade).should().getDetailBundle(context.productGroupId());
            then(mappingResolver).should().resolve(anyLong(), anyLong(), anyLong());
            then(productClientManager)
                    .should()
                    .registerProduct(eq("SELLIC"), any(), anyLong(), anyLong(), any(), any());
        }

        @Test
        @DisplayName("DomainException 발생 시 재시도 불가 실패 결과를 반환한다")
        void execute_DomainExceptionThrown_ReturnsNonRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicCreateContext();

            given(productGroupReadFacade.getDetailBundle(context.productGroupId()))
                    .willThrow(new CategoryMappingNotFoundException(10L, 300L));

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
            assertThat(result.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("일반 Exception 발생 시 재시도 가능 실패 결과를 반환한다")
        void execute_GeneralExceptionThrown_ReturnsRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicCreateContext();

            given(productGroupReadFacade.getDetailBundle(context.productGroupId()))
                    .willThrow(new RuntimeException("인프라 오류"));

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isTrue();
            assertThat(result.errorMessage()).isEqualTo("인프라 오류");
        }

        @Test
        @DisplayName("매핑 조회 실패(DomainException) 시 재시도 불가 실패 결과를 반환한다")
        void execute_MappingResolveDomainException_ReturnsNonRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicCreateContext();
            ProductGroupDetailBundle bundle = defaultDetailBundle();

            given(productGroupReadFacade.getDetailBundle(context.productGroupId()))
                    .willReturn(bundle);
            given(mappingResolver.resolve(anyLong(), anyLong(), anyLong()))
                    .willThrow(new CategoryMappingNotFoundException(10L, 300L));

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
        }
    }

    private ProductGroupDetailBundle defaultDetailBundle() {
        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        100L, 1L, "테스트셀러",
                        OutboundSyncExecutionContextFixtures.DEFAULT_BRAND_ID, "테스트브랜드",
                        OutboundSyncExecutionContextFixtures.DEFAULT_CATEGORY_ID, "카테고리명",
                        "상의>긴팔", "1/5", "테스트상품", "SINGLE", "ACTIVE",
                        Instant.now(), Instant.now(), null, null);

        ProductGroup group = ProductGroupFixtures.activeProductGroup();

        return new ProductGroupDetailBundle(
                queryResult, group, List.of(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }
}
