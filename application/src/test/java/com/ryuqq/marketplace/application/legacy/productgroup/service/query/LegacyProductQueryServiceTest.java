package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
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
@DisplayName("LegacyProductQueryService 단위 테스트")
class LegacyProductQueryServiceTest {

    @InjectMocks private LegacyProductQueryService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private GetProductGroupUseCase getProductGroupUseCase;
    @Mock private LegacyProductGroupFromMarketAssembler assembler;

    @Nested
    @DisplayName("execute() - 레거시 상품 조회 실행")
    class ExecuteTest {

        @Test
        @DisplayName("레거시 productGroupId를 resolve하고 표준 UseCase로 조회 후 레거시 결과로 변환한다")
        void execute_ValidLegacyGroupId_ResolvesAndReturnsLegacyResult() {
            // given
            long legacyGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();
            ProductGroupDetailCompositeResult composite = createCompositeResult(
                    resolved.resolvedProductGroupId().value());
            LegacyProductGroupDetailResult expectedResult =
                    LegacyProductGroupQueryFixtures.detailResult(legacyGroupId);

            given(resolveFactory.resolve(legacyGroupId)).willReturn(resolved);
            given(getProductGroupUseCase.execute(resolved.resolvedProductGroupId().value()))
                    .willReturn(composite);
            given(assembler.toDetailResult(composite, resolved)).willReturn(expectedResult);

            // when
            LegacyProductGroupDetailResult result = sut.execute(legacyGroupId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(resolveFactory).should().resolve(legacyGroupId);
            then(getProductGroupUseCase)
                    .should()
                    .execute(resolved.resolvedProductGroupId().value());
            then(assembler).should().toDetailResult(composite, resolved);
        }

        @Test
        @DisplayName("레거시 PK와 market PK가 동일한 경우(매핑 없음)도 정상 처리한다")
        void execute_NoMappingExists_UsesSamePkAndReturnsResult() {
            // given
            long marketGroupId = LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID;
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();
            ProductGroupDetailCompositeResult composite = createCompositeResult(marketGroupId);
            LegacyProductGroupDetailResult expectedResult =
                    LegacyProductGroupQueryFixtures.detailResult(marketGroupId);

            given(resolveFactory.resolve(marketGroupId)).willReturn(resolved);
            given(getProductGroupUseCase.execute(resolved.resolvedProductGroupId().value()))
                    .willReturn(composite);
            given(assembler.toDetailResult(composite, resolved)).willReturn(expectedResult);

            // when
            LegacyProductGroupDetailResult result = sut.execute(marketGroupId);

            // then
            assertThat(result).isNotNull();
        }
    }

    private ProductGroupDetailCompositeResult createCompositeResult(long productGroupId) {
        Instant now = Instant.now();
        return new ProductGroupDetailCompositeResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "의류",
                "의류 > 상의",
                String.valueOf(ProductGroupFixtures.DEFAULT_CATEGORY_ID),
                "테스트 상품그룹",
                "SINGLE",
                "ACTIVE",
                now,
                now,
                List.of(),
                null,
                null,
                null,
                null,
                null);
    }
}
