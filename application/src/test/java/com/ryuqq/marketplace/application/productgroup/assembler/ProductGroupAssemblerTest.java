package com.ryuqq.marketplace.application.productgroup.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupExcelPageResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupAssembler 단위 테스트")
class ProductGroupAssemblerTest {

    private ProductGroupAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ProductGroupAssembler();
    }

    @Nested
    @DisplayName("toPageResult() - 목록 번들 → PageResult 조립")
    class ToPageResultTest {

        @Test
        @DisplayName("빈 번들로 빈 PageResult를 생성한다")
        void toPageResult_EmptyBundle_ReturnsEmptyPageResult() {
            // given
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            int page = 0;
            int size = 20;

            // when
            ProductGroupPageResult result = sut.toPageResult(bundle, page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isZero();
        }

        @Test
        @DisplayName("totalElements가 0인 빈 번들은 empty PageResult를 반환한다")
        void toPageResult_ZeroTotalElements_ReturnsEmptyPageResult() {
            // given
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);

            // when
            ProductGroupPageResult result = sut.toPageResult(bundle, 0, 20);

            // then
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toExcelPageResult() - 엑셀 번들 → ExcelPageResult 조립")
    class ToExcelPageResultTest {

        @Test
        @DisplayName("빈 번들로 빈 PageResult를 반환한다")
        void toExcelPageResult_EmptyBundle_ReturnsEmptyPageResult() {
            // given
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 0L);

            // when
            ProductGroupExcelPageResult result = sut.toExcelPageResult(bundle, 0, 20);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("baseComposites가 있으면 각 항목에 대해 ExcelCompositeResult를 포함한 PageResult를 생성한다")
        void toExcelPageResult_WithBaseComposites_ReturnsExcelPageResult() {
            // given
            Instant now = Instant.now();
            ProductGroupListCompositeResult base = createListCompositeResult(1L, now);
            ProductGroupEnrichmentResult enrichment =
                    new ProductGroupEnrichmentResult(1L, 10000, 20000, 10, List.of());
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(base),
                            List.of(enrichment),
                            Map.of(),
                            Map.of(),
                            Map.of(),
                            Map.of(),
                            1L);

            // when
            ProductGroupExcelPageResult result = sut.toExcelPageResult(bundle, 0, 20);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.results().get(0).base()).isNotNull();
            assertThat(result.results().get(0).images()).isEmpty();
            assertThat(result.results().get(0).products()).isEmpty();
            assertThat(result.results().get(0).descriptionCdnUrl()).isNull();
            assertThat(result.results().get(0).notice()).isNull();
            assertThat(result.pageMeta().totalElements()).isEqualTo(1L);
        }

        @Test
        @DisplayName("여러 baseComposites가 있으면 동일한 수의 결과를 포함한 PageResult를 반환한다")
        void toExcelPageResult_WithMultipleBaseComposites_ReturnsAllResults() {
            // given
            Instant now = Instant.now();
            List<ProductGroupListCompositeResult> baseComposites =
                    List.of(
                            createListCompositeResult(1L, now),
                            createListCompositeResult(2L, now),
                            createListCompositeResult(3L, now));
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            baseComposites, List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 3L);

            // when
            ProductGroupExcelPageResult result = sut.toExcelPageResult(bundle, 0, 20);

            // then
            assertThat(result.results()).hasSize(3);
            assertThat(result.pageMeta().totalElements()).isEqualTo(3L);
        }

        @Test
        @DisplayName("descriptionCdnUrl이 있으면 결과에 포함된다")
        void toExcelPageResult_WithDescriptionCdnUrl_IncludesCdnUrlInResult() {
            // given
            Instant now = Instant.now();
            Long productGroupId = 1L;
            ProductGroupListCompositeResult base = createListCompositeResult(productGroupId, now);
            String cdnUrl = "https://cdn.example.com/products/desc.html";
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(base),
                            List.of(),
                            Map.of(),
                            Map.of(),
                            Map.of(productGroupId, cdnUrl),
                            Map.of(),
                            1L);

            // when
            ProductGroupExcelPageResult result = sut.toExcelPageResult(bundle, 0, 20);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.results().get(0).descriptionCdnUrl()).isEqualTo(cdnUrl);
        }
    }

    @Nested
    @DisplayName("toDetailResult() - 상세 번들 → DetailCompositeResult 조립")
    class ToDetailResultTest {

        @Test
        @DisplayName("NONE 옵션 타입의 ProductGroup으로 DetailCompositeResult를 생성한다")
        void toDetailResult_NoneOptionGroup_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(productGroupId);
            assertThat(result.sellerId()).isEqualTo(ProductGroupFixtures.DEFAULT_SELLER_ID);
            assertThat(result.productGroupName())
                    .isEqualTo(ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME);
        }

        @Test
        @DisplayName("이미지 없는 ProductGroup으로 상세 조회 시 이미지 목록이 비어있다")
        void toDetailResult_GroupWithImages_MapsImages() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.images()).isNotNull();
        }

        @Test
        @DisplayName("description이 없을 때 description 필드가 null이다")
        void toDetailResult_WithoutDescription_DescriptionIsNull() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle =
                    new ProductGroupDetailBundle(
                            createQueryResult(productGroupId, now),
                            group,
                            List.of(),
                            Optional.empty(),
                            Optional.empty(),
                            Map.of());

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result.description()).isNull();
        }

        @Test
        @DisplayName("notice가 없을 때 productNotice 필드가 null이다")
        void toDetailResult_WithoutNotice_NoticeIsNull() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle =
                    new ProductGroupDetailBundle(
                            createQueryResult(productGroupId, now),
                            group,
                            List.of(),
                            Optional.empty(),
                            Optional.empty(),
                            Map.of());

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result.productNotice()).isNull();
        }

        @Test
        @DisplayName("단일 옵션 ProductGroup으로 DetailCompositeResult를 생성한다")
        void toDetailResult_SingleOptionGroup_ReturnsResultWithOptionMatrix() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.newProductGroupWithSingleOption();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.optionProductMatrix()).isNotNull();
            assertThat(result.optionProductMatrix().optionGroups()).isNotEmpty();
        }
    }

    private ProductGroupListCompositeResult createListCompositeResult(
            Long productGroupId, Instant now) {
        return ProductGroupListCompositeResult.ofBase(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                2,
                "FASHION",
                "TOPS",
                ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                "NONE",
                "ACTIVE",
                "https://example.com/thumb.jpg",
                1,
                now,
                now);
    }

    private ProductGroupDetailBundle createDetailBundle(
            ProductGroup group, Long productGroupId, Instant now) {
        return new ProductGroupDetailBundle(
                createQueryResult(productGroupId, now),
                group,
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }

    private ProductGroupDetailCompositeQueryResult createQueryResult(
            Long productGroupId, Instant now) {
        return new ProductGroupDetailCompositeQueryResult(
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
    }
}
