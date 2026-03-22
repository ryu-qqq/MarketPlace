package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeProductTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter.LegacyProductCompositionQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter.LegacyProductGroupCompositionQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductGroupCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductCompositeQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupDetailQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
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
 * 레거시 상품 Composite 조회 E2E 테스트.
 *
 * <p>Adapter → Mapper → QueryDSL Repository → H2 DB 전체 흐름을 실제 객체로 검증합니다.
 * 상품그룹 상세 조회 + 상품 옵션 조회의 End-to-End 시나리오를 커버합니다.
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
@DisplayName("레거시 상품 Composite 조회 E2E 테스트")
class LegacyProductCompositeE2ETest {

    @Autowired private EntityManager entityManager;

    private LegacyProductCompositionQueryAdapter productAdapter;
    private LegacyProductGroupCompositionQueryAdapter productGroupAdapter;
    private LegacyCompositeProductTestHelper helper;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        LegacyProductCompositeMapper productMapper = new LegacyProductCompositeMapper();
        LegacyProductGroupCompositeMapper groupMapper = new LegacyProductGroupCompositeMapper();

        LegacyProductCompositeQueryDslRepository productRepo =
                new LegacyProductCompositeQueryDslRepository(queryFactory);
        LegacyProductGroupDetailQueryDslRepository groupDetailRepo =
                new LegacyProductGroupDetailQueryDslRepository(queryFactory);

        productAdapter = new LegacyProductCompositionQueryAdapter(productRepo, productMapper);
        productGroupAdapter = new LegacyProductGroupCompositionQueryAdapter(groupDetailRepo, groupMapper);
        helper = new LegacyCompositeProductTestHelper(entityManager);
    }

    // ========================================================================
    // 1. 상품그룹 상세 조회 시나리오
    // ========================================================================

    @Nested
    @DisplayName("상품그룹 상세 조회 E2E 시나리오")
    class ProductGroupDetailE2ETest {

        @Test
        @DisplayName("전체 데이터 — 7테이블 JOIN 결과가 Composite DTO로 올바르게 변환됩니다")
        void fullData_ReturnsCompleteCompositeResult() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            Optional<LegacyProductGroupCompositeResult> result =
                    productGroupAdapter.findCompositeById(pgId);

            // then
            assertThat(result).isPresent();
            LegacyProductGroupCompositeResult composite = result.get();

            // 기본 필드
            assertThat(composite.productGroupId()).isEqualTo(pgId);
            assertThat(composite.productGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(composite.optionType()).isEqualTo("SINGLE");
            assertThat(composite.managementType()).isEqualTo("SYSTEM");
            assertThat(composite.soldOut()).isFalse();
            assertThat(composite.displayed()).isTrue();
            assertThat(composite.productCondition()).isEqualTo("NEW");

            // Seller JOIN
            assertThat(composite.sellerId()).isEqualTo(10L);
            assertThat(composite.sellerName()).isEqualTo("테스트 셀러");

            // Brand JOIN
            assertThat(composite.brandId()).isEqualTo(20L);
            assertThat(composite.brandName()).isEqualTo("나이키");

            // Category JOIN
            assertThat(composite.categoryId()).isEqualTo(30L);
            assertThat(composite.categoryPath()).isEqualTo("패션>의류>상의");

            // Delivery JOIN
            assertThat(composite.delivery()).isNotNull();
            assertThat(composite.delivery().deliveryArea()).isEqualTo("NATIONWIDE");
            assertThat(composite.delivery().deliveryFee()).isEqualTo(3000);

            // Description JOIN
            assertThat(composite.detailDescription()).isEqualTo("<p>상품 상세 설명</p>");

            // Notice JOIN
            assertThat(composite.notice()).isNotNull();
            assertThat(composite.notice().material()).isEqualTo("면100%");
            assertThat(composite.notice().color()).isEqualTo("블랙");
            assertThat(composite.notice().maker()).isEqualTo("나이키");

            // Images (별도 쿼리)
            assertThat(composite.images()).hasSize(2);
            assertThat(composite.images())
                    .extracting(LegacyProductGroupCompositeResult.ImageInfo::imageType)
                    .containsExactlyInAnyOrder("MAIN", "DETAIL");
        }

        @Test
        @DisplayName("최소 데이터 — 필수 JOIN만 있고 옵셔널 데이터 없는 경우")
        void minimalData_ReturnsCompositeWithNullOptionals() {
            // given
            helper.insertSeller(100L, "최소 셀러");
            helper.insertBrand(200L, "최소 브랜드");
            helper.insertCategory(300L, "최소 카테고리", "100");

            long pgId = helper.persistProductGroup(100L, 200L, 300L);
            helper.flushAndClear();

            // when
            Optional<LegacyProductGroupCompositeResult> result =
                    productGroupAdapter.findCompositeById(pgId);

            // then
            assertThat(result).isPresent();
            LegacyProductGroupCompositeResult composite = result.get();
            assertThat(composite.sellerName()).isEqualTo("최소 셀러");
            assertThat(composite.brandName()).isEqualTo("최소 브랜드");
            assertThat(composite.delivery()).isNull();
            assertThat(composite.notice()).isNull();
            assertThat(composite.detailDescription()).isNull();
            assertThat(composite.images()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID — 빈 Optional 반환")
        void nonExistentId_ReturnsEmpty() {
            // when
            Optional<LegacyProductGroupCompositeResult> result =
                    productGroupAdapter.findCompositeById(99999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. 상품+옵션 조회 시나리오
    // ========================================================================

    @Nested
    @DisplayName("상품+옵션 조회 E2E 시나리오")
    class ProductCompositeE2ETest {

        @Test
        @DisplayName("옵션 있는 상품 — flat JOIN 결과가 productId별 그룹핑되어 옵션 매핑이 완성됩니다")
        void productWithOptions_GroupedByProductIdWithOptionMappings() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            List<LegacyProductCompositeResult> results =
                    productAdapter.findProductsByProductGroupId(pgId);

            // then
            assertThat(results).hasSize(1); // 1개 상품
            LegacyProductCompositeResult product = results.get(0);
            assertThat(product.productGroupId()).isEqualTo(pgId);
            assertThat(product.stockQuantity()).isEqualTo(10);
            assertThat(product.soldOut()).isFalse();

            // 색상(빨강) + 사이즈(M) = 2개 옵션 매핑
            assertThat(product.optionMappings()).hasSize(2);
            assertThat(product.optionMappings())
                    .extracting(LegacyProductCompositeResult.OptionMapping::optionValue)
                    .containsExactlyInAnyOrder("빨강", "M");
            assertThat(product.optionMappings())
                    .extracting(LegacyProductCompositeResult.OptionMapping::optionGroupName)
                    .containsExactlyInAnyOrder("색상", "사이즈");
        }

        @Test
        @DisplayName("옵션 없는 상품 — 빈 옵션 매핑 목록으로 반환됩니다")
        void productWithoutOptions_ReturnsEmptyOptionMappings() {
            // given
            helper.insertSeller(101L, "셀러A");
            helper.insertBrand(201L, "브랜드A");
            helper.insertCategory(301L, "카테고리A", "100");

            long pgId = helper.persistProductGroup(101L, 201L, 301L);
            long productId = helper.persistProduct(pgId, "N");
            helper.persistProductStock(productId, 7);
            helper.flushAndClear();

            // when
            List<LegacyProductCompositeResult> results =
                    productAdapter.findProductsByProductGroupId(pgId);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).stockQuantity()).isEqualTo(7);
            assertThat(results.get(0).optionMappings()).isEmpty();
        }

        @Test
        @DisplayName("다중 상품 — 각 상품이 독립적으로 옵션 매핑됩니다")
        void multipleProducts_EachWithIndependentOptionMappings() {
            // given
            helper.insertSeller(102L, "셀러B");
            helper.insertBrand(202L, "브랜드B");
            helper.insertCategory(302L, "카테고리B", "200");

            long pgId = helper.persistProductGroup(102L, 202L, 302L);

            // 상품1: 색상=블랙, 재고 5
            long product1 = helper.persistProduct(pgId, "N");
            helper.persistProductStock(product1, 5);
            long colorGroup = helper.persistOptionGroup("색상");
            long blackDetail = helper.persistOptionDetail(colorGroup, "블랙");
            helper.persistProductOption(product1, colorGroup, blackDetail);

            // 상품2: 사이즈=L, 재고 0, 품절
            long product2 = helper.persistProduct(pgId, "Y");
            helper.persistProductStock(product2, 0);
            long sizeGroup = helper.persistOptionGroup("사이즈");
            long lDetail = helper.persistOptionDetail(sizeGroup, "L");
            helper.persistProductOption(product2, sizeGroup, lDetail);

            helper.flushAndClear();

            // when
            List<LegacyProductCompositeResult> results =
                    productAdapter.findProductsByProductGroupId(pgId);

            // then
            assertThat(results).hasSize(2);

            LegacyProductCompositeResult prod1 =
                    results.stream()
                            .filter(r -> r.productId() == product1)
                            .findFirst()
                            .orElseThrow();
            assertThat(prod1.stockQuantity()).isEqualTo(5);
            assertThat(prod1.soldOut()).isFalse();
            assertThat(prod1.optionMappings()).hasSize(1);
            assertThat(prod1.optionMappings().get(0).optionValue()).isEqualTo("블랙");

            LegacyProductCompositeResult prod2 =
                    results.stream()
                            .filter(r -> r.productId() == product2)
                            .findFirst()
                            .orElseThrow();
            assertThat(prod2.stockQuantity()).isZero();
            assertThat(prod2.soldOut()).isTrue();
            assertThat(prod2.optionMappings()).hasSize(1);
            assertThat(prod2.optionMappings().get(0).optionValue()).isEqualTo("L");
        }

        @Test
        @DisplayName("상품 없는 상품그룹 — 빈 목록 반환")
        void noProducts_ReturnsEmptyList() {
            // given
            helper.insertSeller(103L, "셀러C");
            helper.insertBrand(203L, "브랜드C");
            helper.insertCategory(303L, "카테고리C", "300");

            long pgId = helper.persistProductGroup(103L, 203L, 303L);
            helper.flushAndClear();

            // when
            List<LegacyProductCompositeResult> results =
                    productAdapter.findProductsByProductGroupId(pgId);

            // then
            assertThat(results).isEmpty();
        }
    }
}
