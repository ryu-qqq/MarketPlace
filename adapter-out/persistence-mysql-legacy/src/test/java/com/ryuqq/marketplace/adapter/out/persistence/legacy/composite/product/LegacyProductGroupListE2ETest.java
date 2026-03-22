package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeProductTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter.LegacyProductGroupCompositeListQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupListConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupListQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * 레거시 상품그룹 목록 조회 E2E 테스트.
 *
 * <p>CompositeListQueryAdapter → 3-Phase Query → H2 DB 전체 흐름을 실제 객체로 검증합니다. 검색 필터, 페이징, 순서 보장, 상품
 * 그룹핑 등 End-to-End 시나리오를 커버합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("레거시 상품그룹 목록 조회 E2E 테스트")
class LegacyProductGroupListE2ETest {

    @Autowired private EntityManager entityManager;

    private LegacyProductGroupCompositeListQueryAdapter adapter;
    private LegacyCompositeProductTestHelper helper;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        LegacyProductCompositeMapper productMapper = new LegacyProductCompositeMapper();
        LegacyProductGroupListConditionBuilder conditionBuilder =
                new LegacyProductGroupListConditionBuilder();
        LegacyProductGroupListQueryDslRepository listRepo =
                new LegacyProductGroupListQueryDslRepository(queryFactory, conditionBuilder);

        adapter = new LegacyProductGroupCompositeListQueryAdapter(listRepo, productMapper);
        helper = new LegacyCompositeProductTestHelper(entityManager);
    }

    private LegacyProductGroupSearchCriteria defaultCriteria() {
        return LegacyProductGroupSearchCriteria.of(
                null, null, List.of(), null, null, null, null, null, null, null, null, null, null,
                null, 0, 10);
    }

    // ========================================================================
    // 1. 3-Phase 목록 조회 시나리오
    // ========================================================================

    @Nested
    @DisplayName("3-Phase 목록 조회 E2E 시나리오")
    class SearchProductGroupsE2ETest {

        @Test
        @DisplayName("전체 흐름 — Phase 1~3 결과가 올바르게 조립된 번들 목록을 반환합니다")
        void fullFlow_ReturnsBundleListWithProducts() {
            // given
            helper.insertSeller(10L, "테스트 셀러");
            helper.insertBrand(20L, "나이키");
            helper.insertCategory(30L, "상의", "패션>의류>상의");

            long pgId = helper.persistProductGroup(10L, 20L, 30L);
            long productId = helper.persistProduct(pgId, "N");
            helper.persistProductStock(productId, 10);
            helper.persistDelivery(pgId);
            helper.persistImage(pgId, "MAIN", "https://cdn.example.com/main.jpg");

            long colorGroup = helper.persistOptionGroup("색상");
            long redDetail = helper.persistOptionDetail(colorGroup, "빨강");
            helper.persistProductOption(productId, colorGroup, redDetail);
            helper.flushAndClear();

            // when
            List<LegacyProductGroupDetailBundle> bundles =
                    adapter.searchProductGroups(defaultCriteria());

            // then
            assertThat(bundles).hasSize(1);
            LegacyProductGroupDetailBundle bundle = bundles.get(0);

            // composite 검증
            LegacyProductGroupCompositeResult composite = bundle.composite();
            assertThat(composite.productGroupId()).isEqualTo(pgId);
            assertThat(composite.sellerName()).isEqualTo("테스트 셀러");
            assertThat(composite.brandName()).isEqualTo("나이키");
            assertThat(composite.categoryPath()).isEqualTo("패션>의류>상의");
            assertThat(composite.images()).hasSize(1);
            assertThat(composite.images().get(0).imageType()).isEqualTo("MAIN");

            // products 검증
            assertThat(bundle.products()).hasSize(1);
            assertThat(bundle.products().get(0).stockQuantity()).isEqualTo(10);
            assertThat(bundle.products().get(0).optionMappings()).hasSize(1);
            assertThat(bundle.products().get(0).optionMappings().get(0).optionValue())
                    .isEqualTo("빨강");
        }

        @Test
        @DisplayName("빈 결과 — 데이터 없으면 빈 목록 반환")
        void emptyResult_ReturnsEmptyList() {
            // when
            List<LegacyProductGroupDetailBundle> bundles =
                    adapter.searchProductGroups(defaultCriteria());

            // then
            assertThat(bundles).isEmpty();
        }

        @Test
        @DisplayName("다중 상품그룹 — ID 내림차순 순서가 보장됩니다")
        void multipleGroups_OrderPreservedDescending() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            long pg1 = helper.persistProductGroup(10L, 20L, 30L, "상품1", "N", "Y");
            long pg2 = helper.persistProductGroup(10L, 20L, 30L, "상품2", "N", "Y");
            long pg3 = helper.persistProductGroup(10L, 20L, 30L, "상품3", "N", "Y");
            helper.flushAndClear();

            // when
            List<LegacyProductGroupDetailBundle> bundles =
                    adapter.searchProductGroups(defaultCriteria());

            // then
            assertThat(bundles).hasSize(3);
            assertThat(bundles.get(0).composite().productGroupId()).isEqualTo(pg3);
            assertThat(bundles.get(1).composite().productGroupId()).isEqualTo(pg2);
            assertThat(bundles.get(2).composite().productGroupId()).isEqualTo(pg1);
        }

        @Test
        @DisplayName("상품 없는 그룹 — composite만 있고 products는 빈 목록")
        void groupWithoutProducts_ReturnsBundleWithEmptyProducts() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L);
            helper.flushAndClear();

            // when
            List<LegacyProductGroupDetailBundle> bundles =
                    adapter.searchProductGroups(defaultCriteria());

            // then
            assertThat(bundles).hasSize(1);
            assertThat(bundles.get(0).products()).isEmpty();
        }

        @Test
        @DisplayName("여러 상품그룹에 각각 상품이 있는 경우 — 올바르게 그룹핑됩니다")
        void multipleGroupsWithProducts_CorrectlyGrouped() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            long pg1 = helper.persistProductGroup(10L, 20L, 30L, "그룹1", "N", "Y");
            long prod1 = helper.persistProduct(pg1, "N");
            helper.persistProductStock(prod1, 3);

            long pg2 = helper.persistProductGroup(10L, 20L, 30L, "그룹2", "N", "Y");
            long prod2a = helper.persistProduct(pg2, "N");
            helper.persistProductStock(prod2a, 5);
            long prod2b = helper.persistProduct(pg2, "Y");
            helper.persistProductStock(prod2b, 0);

            helper.flushAndClear();

            // when
            List<LegacyProductGroupDetailBundle> bundles =
                    adapter.searchProductGroups(defaultCriteria());

            // then
            assertThat(bundles).hasSize(2);

            // ID 내림차순이므로 pg2가 먼저
            LegacyProductGroupDetailBundle bundle2 = bundles.get(0);
            assertThat(bundle2.composite().productGroupId()).isEqualTo(pg2);
            assertThat(bundle2.products()).hasSize(2);

            LegacyProductGroupDetailBundle bundle1 = bundles.get(1);
            assertThat(bundle1.composite().productGroupId()).isEqualTo(pg1);
            assertThat(bundle1.products()).hasSize(1);
        }
    }

    // ========================================================================
    // 2. 검색 필터 시나리오
    // ========================================================================

    @Nested
    @DisplayName("검색 필터 E2E 시나리오")
    class SearchFilterE2ETest {

        @Test
        @DisplayName("sellerId 필터 — 해당 셀러의 상품그룹만 반환됩니다")
        void sellerIdFilter_ReturnsOnlyMatchingSeller() {
            // given
            helper.insertSeller(10L, "셀러A");
            helper.insertSeller(11L, "셀러B");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L, "셀러A 상품", "N", "Y");
            helper.persistProductGroup(11L, 20L, 30L, "셀러B 상품", "N", "Y");
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            10L, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 10);

            // when
            List<LegacyProductGroupDetailBundle> bundles = adapter.searchProductGroups(criteria);

            // then
            assertThat(bundles).hasSize(1);
            assertThat(bundles.get(0).composite().sellerName()).isEqualTo("셀러A");
        }

        @Test
        @DisplayName("soldOutYn 필터 — 품절 상품그룹만 반환됩니다")
        void soldOutFilter_ReturnsOnlySoldOut() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L, "일반 상품", "N", "Y");
            helper.persistProductGroup(10L, 20L, 30L, "품절 상품", "Y", "Y");
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, List.of(), null, "Y", null, null, null, null, null, null,
                            null, null, null, 0, 10);

            // when
            List<LegacyProductGroupDetailBundle> bundles = adapter.searchProductGroups(criteria);

            // then
            assertThat(bundles).hasSize(1);
            assertThat(bundles.get(0).composite().soldOut()).isTrue();
        }

        @Test
        @DisplayName("brandId 필터 — 해당 브랜드의 상품그룹만 반환됩니다")
        void brandIdFilter_ReturnsOnlyMatchingBrand() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "나이키");
            helper.insertBrand(21L, "아디다스");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L, "나이키 상품", "N", "Y");
            helper.persistProductGroup(10L, 21L, 30L, "아디다스 상품", "N", "Y");
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, 20L, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 10);

            // when
            List<LegacyProductGroupDetailBundle> bundles = adapter.searchProductGroups(criteria);

            // then
            assertThat(bundles).hasSize(1);
            assertThat(bundles.get(0).composite().brandName()).isEqualTo("나이키");
        }
    }

    // ========================================================================
    // 3. 페이징 시나리오
    // ========================================================================

    @Nested
    @DisplayName("페이징 E2E 시나리오")
    class PagingE2ETest {

        @Test
        @DisplayName("page=0, size=2 — 첫 페이지 2건만 반환됩니다")
        void firstPage_ReturnsLimitedResults() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            for (int i = 0; i < 5; i++) {
                helper.persistProductGroup(10L, 20L, 30L, "상품" + i, "N", "Y");
            }
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 2);

            // when
            List<LegacyProductGroupDetailBundle> bundles = adapter.searchProductGroups(criteria);

            // then
            assertThat(bundles).hasSize(2);
        }

        @Test
        @DisplayName("page=2, size=2 — 마지막 페이지 1건만 반환됩니다")
        void lastPage_ReturnsRemainingResults() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            for (int i = 0; i < 5; i++) {
                helper.persistProductGroup(10L, 20L, 30L, "상품" + i, "N", "Y");
            }
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 2, 2);

            // when
            List<LegacyProductGroupDetailBundle> bundles = adapter.searchProductGroups(criteria);

            // then
            assertThat(bundles).hasSize(1);
        }
    }

    // ========================================================================
    // 4. count 시나리오
    // ========================================================================

    @Nested
    @DisplayName("count E2E 시나리오")
    class CountE2ETest {

        @Test
        @DisplayName("전체 건수와 필터 건수가 올바르게 계산됩니다")
        void countWithFilter_ReturnsFilteredCount() {
            // given
            helper.insertSeller(10L, "셀러A");
            helper.insertSeller(11L, "셀러B");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(11L, 20L, 30L);
            helper.flushAndClear();

            // when
            long totalCount = adapter.count(defaultCriteria());

            LegacyProductGroupSearchCriteria sellerFilter =
                    LegacyProductGroupSearchCriteria.of(
                            10L, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 10);
            long sellerCount = adapter.count(sellerFilter);

            // then
            assertThat(totalCount).isEqualTo(3);
            assertThat(sellerCount).isEqualTo(2);
        }

        @Test
        @DisplayName("데이터 없으면 0 반환")
        void noData_ReturnsZero() {
            // when
            long count = adapter.count(defaultCriteria());

            // then
            assertThat(count).isZero();
        }
    }
}
