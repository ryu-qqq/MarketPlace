package com.ryuqq.marketplace.application.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;
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
@DisplayName("GetProductGroupService 단위 테스트")
class GetProductGroupServiceTest {

    @InjectMocks private GetProductGroupService sut;

    @Mock private ProductGroupReadFacade readFacade;
    @Mock private ProductGroupAssembler assembler;

    @Nested
    @DisplayName("execute() - 상품 그룹 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상품 그룹 ID로 상세 정보를 조회하고 결과를 반환한다")
        void execute_ValidId_ReturnsDetailCompositeResult() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);
            ProductGroupDetailCompositeResult expected =
                    createDetailCompositeResult(productGroupId, now);

            given(readFacade.getDetailBundle(productGroupId)).willReturn(bundle);
            given(assembler.toDetailResult(bundle)).willReturn(expected);

            // when
            ProductGroupDetailCompositeResult result = sut.execute(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expected);
            assertThat(result.id()).isEqualTo(productGroupId);
            then(readFacade).should().getDetailBundle(productGroupId);
            then(assembler).should().toDetailResult(bundle);
        }
    }

    private ProductGroupDetailBundle createDetailBundle(
            ProductGroup group, Long productGroupId, Instant now) {
        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        productGroupId,
                        ProductGroupFixtures.DEFAULT_SELLER_ID,
                        "테스트 셀러",
                        ProductGroupFixtures.DEFAULT_BRAND_ID,
                        "테스트 브랜드",
                        ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                        "테스트 카테고리",
                        "카테고리 > 테스트 카테고리",
                        "1/200",
                        ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                        "NONE",
                        "ACTIVE",
                        now,
                        now,
                        null,
                        null);

        return new ProductGroupDetailBundle(
                queryResult, group, List.of(), Optional.empty(), Optional.empty());
    }

    private ProductGroupDetailCompositeResult createDetailCompositeResult(
            Long productGroupId, Instant now) {
        return new ProductGroupDetailCompositeResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트 셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트 브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "테스트 카테고리",
                "카테고리 > 테스트 카테고리",
                "1/200",
                ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                "NONE",
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
