package com.ryuqq.marketplace.application.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCompositionReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
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
@DisplayName("ProductGroupReadFacade 단위 테스트")
class ProductGroupReadFacadeTest {

    @InjectMocks private ProductGroupReadFacade sut;

    @Mock private ProductGroupCompositionReadManager compositionReadManager;
    @Mock private ProductGroupReadManager productGroupReadManager;
    @Mock private ProductGroupDescriptionReadManager descriptionReadManager;
    @Mock private ProductReadManager productReadManager;
    @Mock private ProductNoticeReadManager productNoticeReadManager;

    @Nested
    @DisplayName("getListBundle() - Offset 기반 목록 조회 번들")
    class GetListBundleTest {

        @Test
        @DisplayName("검색 조건으로 baseComposites + enrichments + totalElements를 포함한 번들을 반환한다")
        void getListBundle_ValidCriteria_ReturnsListBundle() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            List<ProductGroupListCompositeResult> baseComposites =
                    List.of(createListCompositeResult(1L), createListCompositeResult(2L));
            long totalElements = 2L;
            List<ProductGroupEnrichmentResult> enrichments =
                    List.of(
                            new ProductGroupEnrichmentResult(1L, 10000, 20000, 10, List.of()),
                            new ProductGroupEnrichmentResult(2L, 15000, 25000, 15, List.of()));

            given(compositionReadManager.findCompositeByCriteria(criteria))
                    .willReturn(baseComposites);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);
            given(compositionReadManager.findEnrichments(List.of(1L, 2L))).willReturn(enrichments);

            // when
            ProductGroupListBundle result = sut.getListBundle(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.baseComposites()).hasSize(2);
            assertThat(result.enrichments()).hasSize(2);
            assertThat(result.totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("결과가 없으면 빈 번들을 반환하고 enrichment 쿼리를 실행하지 않는다")
        void getListBundle_EmptyResult_ReturnsEmptyBundleWithoutEnrichmentQuery() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            given(compositionReadManager.findCompositeByCriteria(criteria)).willReturn(List.of());
            given(compositionReadManager.countByCriteria(criteria)).willReturn(0L);

            // when
            ProductGroupListBundle result = sut.getListBundle(criteria);

            // then
            assertThat(result.baseComposites()).isEmpty();
            assertThat(result.enrichments()).isEmpty();
            assertThat(result.totalElements()).isZero();
            then(compositionReadManager).should().findCompositeByCriteria(criteria);
            then(compositionReadManager).should().countByCriteria(criteria);
            then(compositionReadManager).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("baseComposites가 있으면 productGroupIds로 enrichment 쿼리를 실행한다")
        void getListBundle_WithBaseComposites_ExecutesEnrichmentQuery() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            List<ProductGroupListCompositeResult> baseComposites =
                    List.of(createListCompositeResult(1L));
            List<ProductGroupEnrichmentResult> enrichments =
                    List.of(new ProductGroupEnrichmentResult(1L, 10000, 20000, 10, List.of()));

            given(compositionReadManager.findCompositeByCriteria(criteria))
                    .willReturn(baseComposites);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(1L);
            given(compositionReadManager.findEnrichments(List.of(1L))).willReturn(enrichments);

            // when
            sut.getListBundle(criteria);

            // then
            then(compositionReadManager).should().findEnrichments(List.of(1L));
        }
    }

    @Nested
    @DisplayName("getDetailBundle() - 상세 조회 번들")
    class GetDetailBundleTest {

        @Test
        @DisplayName("상품 그룹 ID로 상세 번들을 반환한다")
        void getDetailBundle_ValidId_ReturnsDetailBundle() {
            // given
            Long productGroupId = 1L;
            ProductGroupId groupId = ProductGroupId.of(productGroupId);
            ProductGroupDetailCompositeQueryResult queryResult =
                    createDetailCompositeQueryResult(productGroupId);
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            List<Product> products = List.of();
            Optional<ProductGroupDescription> description = Optional.empty();
            Optional<ProductNotice> notice = Optional.empty();

            given(compositionReadManager.getDetailCompositeById(productGroupId))
                    .willReturn(queryResult);
            given(productGroupReadManager.getById(groupId)).willReturn(group);
            given(productReadManager.findByProductGroupId(groupId)).willReturn(products);
            given(descriptionReadManager.findByProductGroupId(groupId)).willReturn(description);
            given(productNoticeReadManager.findByProductGroupId(groupId)).willReturn(notice);

            // when
            ProductGroupDetailBundle result = sut.getDetailBundle(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryResult()).isEqualTo(queryResult);
            assertThat(result.group()).isEqualTo(group);
            assertThat(result.products()).isEmpty();
            assertThat(result.description()).isEmpty();
            assertThat(result.notice()).isEmpty();
        }

        @Test
        @DisplayName("모든 ReadManager에 위임하여 번들을 조립한다")
        void getDetailBundle_DelegatesToAllReadManagers() {
            // given
            Long productGroupId = 1L;
            ProductGroupId groupId = ProductGroupId.of(productGroupId);

            given(compositionReadManager.getDetailCompositeById(productGroupId))
                    .willReturn(createDetailCompositeQueryResult(productGroupId));
            given(productGroupReadManager.getById(groupId))
                    .willReturn(ProductGroupFixtures.activeProductGroup());
            given(productReadManager.findByProductGroupId(groupId)).willReturn(List.of());
            given(descriptionReadManager.findByProductGroupId(groupId))
                    .willReturn(Optional.empty());
            given(productNoticeReadManager.findByProductGroupId(groupId))
                    .willReturn(Optional.empty());

            // when
            sut.getDetailBundle(productGroupId);

            // then
            then(compositionReadManager).should().getDetailCompositeById(productGroupId);
            then(productGroupReadManager).should().getById(groupId);
            then(productReadManager).should().findByProductGroupId(groupId);
            then(descriptionReadManager).should().findByProductGroupId(groupId);
            then(productNoticeReadManager).should().findByProductGroupId(groupId);
        }

        @Test
        @DisplayName("상세설명과 고시정보가 존재하는 경우 Optional.of로 반환한다")
        void getDetailBundle_WithDescriptionAndNotice_ReturnsBundleWithOptionalPresent() {
            // given
            Long productGroupId = 1L;
            ProductGroupId groupId = ProductGroupId.of(productGroupId);
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            ProductNotice notice = createMockProductNotice();

            given(compositionReadManager.getDetailCompositeById(productGroupId))
                    .willReturn(createDetailCompositeQueryResult(productGroupId));
            given(productGroupReadManager.getById(groupId))
                    .willReturn(ProductGroupFixtures.activeProductGroup());
            given(productReadManager.findByProductGroupId(groupId)).willReturn(List.of());
            given(descriptionReadManager.findByProductGroupId(groupId))
                    .willReturn(Optional.of(description));
            given(productNoticeReadManager.findByProductGroupId(groupId))
                    .willReturn(Optional.of(notice));

            // when
            ProductGroupDetailBundle result = sut.getDetailBundle(productGroupId);

            // then
            assertThat(result.description()).isPresent();
            assertThat(result.notice()).isPresent();
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupListCompositeResult createListCompositeResult(Long productGroupId) {
        return ProductGroupListCompositeResult.ofBase(
                productGroupId,
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                2,
                "FASHION",
                "TOPS",
                "테스트 상품 그룹",
                "NONE",
                "ACTIVE",
                "https://example.com/thumb.jpg",
                1,
                Instant.now(),
                Instant.now());
    }

    private ProductGroupDetailCompositeQueryResult createDetailCompositeQueryResult(
            Long productGroupId) {
        return new ProductGroupDetailCompositeQueryResult(
                productGroupId,
                1L,
                "테스트셀러",
                100L,
                "테스트브랜드",
                200L,
                "테스트카테고리",
                "상의 > 긴팔",
                "1/200",
                "테스트 상품 그룹",
                "NONE",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                null,
                null);
    }

    private ProductNotice createMockProductNotice() {
        return com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures
                .existingProductNotice(1L);
    }
}
