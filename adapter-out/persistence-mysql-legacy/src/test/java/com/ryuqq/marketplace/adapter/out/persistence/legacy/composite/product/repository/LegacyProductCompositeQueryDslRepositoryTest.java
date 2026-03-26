package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeProductTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
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
 * LegacyProductCompositeQueryDslRepositoryTest - 상품 Composite QueryDSL Repository 통합 테스트.
 *
 * <p>Product + ProductStock + ProductOption + OptionGroup + OptionDetail 5테이블 조인 쿼리를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("LegacyProductCompositeQueryDslRepository 통합 테스트")
class LegacyProductCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyProductCompositeQueryDslRepository repository;
    private LegacyCompositeProductTestHelper helper;

    @BeforeEach
    void setUp() {
        repository =
                new LegacyProductCompositeQueryDslRepository(new JPAQueryFactory(entityManager));
        helper = new LegacyCompositeProductTestHelper(entityManager);
    }

    @Nested
    @DisplayName("fetchProductsWithOptions 메서드 테스트")
    class FetchProductsWithOptionsTest {

        @Test
        @DisplayName("상품+옵션+재고 데이터를 flat 조인으로 조회합니다")
        void fetchProductsWithOptions_WithFullData_ReturnsFlatRows() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            List<LegacyProductOptionQueryDto> rows = repository.fetchProductsWithOptions(pgId);

            // then
            assertThat(rows).hasSize(2); // 2개 옵션 → 2행
            assertThat(rows).allMatch(r -> r.productGroupId() == pgId);
            assertThat(rows).allMatch(r -> r.stockQuantity() == 10);
            assertThat(rows).allMatch(r -> "N".equals(r.soldOutYn()));
        }

        @Test
        @DisplayName("옵션이 없는 상품도 LEFT JOIN으로 포함됩니다")
        void fetchProductsWithOptions_WithNoOptions_ReturnsProductRow() {
            // given
            helper.insertSeller(11L, "셀러1");
            helper.insertBrand(21L, "브랜드1");
            helper.insertCategory(31L, "카테고리1", "100");

            long pgId = helper.persistProductGroup(11L, 21L, 31L);
            long productId = helper.persistProduct(pgId, "N");
            helper.persistProductStock(productId, 5);
            helper.flushAndClear();

            // when
            List<LegacyProductOptionQueryDto> rows = repository.fetchProductsWithOptions(pgId);

            // then
            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).productId()).isEqualTo(productId);
            assertThat(rows.get(0).optionGroupId()).isNull();
            assertThat(rows.get(0).optionDetailId()).isNull();
        }

        @Test
        @DisplayName("삭제된 상품(deleteYn='Y')은 조회에서 제외됩니다")
        void fetchProductsWithOptions_WithDeletedProduct_ExcludesDeleted() {
            // given
            helper.insertSeller(12L, "셀러2");
            helper.insertBrand(22L, "브랜드2");
            helper.insertCategory(32L, "카테고리2", "200");

            long pgId = helper.persistProductGroup(12L, 22L, 32L);
            long activeProductId = helper.persistProduct(pgId, "N");
            helper.persistProductStock(activeProductId, 5);

            // 삭제된 상품은 Native SQL로 삽입
            entityManager
                    .createNativeQuery(
                            "INSERT INTO product (product_group_id, sold_out_yn, display_yn,"
                                    + " delete_yn) VALUES (?, ?, ?, ?)")
                    .setParameter(1, pgId)
                    .setParameter(2, "N")
                    .setParameter(3, "Y")
                    .setParameter(4, "Y")
                    .executeUpdate();
            helper.flushAndClear();

            // when
            List<LegacyProductOptionQueryDto> rows = repository.fetchProductsWithOptions(pgId);

            // then
            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).productId()).isEqualTo(activeProductId);
        }

        @Test
        @DisplayName("존재하지 않는 상품그룹 ID로 조회 시 빈 목록을 반환합니다")
        void fetchProductsWithOptions_WithNonExistentId_ReturnsEmptyList() {
            // when
            List<LegacyProductOptionQueryDto> rows = repository.fetchProductsWithOptions(99999L);

            // then
            assertThat(rows).isEmpty();
        }

        @Test
        @DisplayName("여러 상품이 각각 옵션을 가진 경우 모든 행이 반환됩니다")
        void fetchProductsWithOptions_WithMultipleProducts_ReturnsAllRows() {
            // given
            helper.insertSeller(13L, "셀러3");
            helper.insertBrand(23L, "브랜드3");
            helper.insertCategory(33L, "카테고리3", "300");

            long pgId = helper.persistProductGroup(13L, 23L, 33L);

            long product1 = helper.persistProduct(pgId, "N");
            helper.persistProductStock(product1, 3);
            long product2 = helper.persistProduct(pgId, "Y");
            helper.persistProductStock(product2, 0);

            long groupId = helper.persistOptionGroup("색상");
            long detailId = helper.persistOptionDetail(groupId, "블랙");
            helper.persistProductOption(product1, groupId, detailId);
            helper.flushAndClear();

            // when
            List<LegacyProductOptionQueryDto> rows = repository.fetchProductsWithOptions(pgId);

            // then
            assertThat(rows).hasSize(2); // product1(옵션1행) + product2(옵션없음1행)

            LegacyProductOptionQueryDto product1Row =
                    rows.stream().filter(r -> r.productId() == product1).findFirst().orElseThrow();
            assertThat(product1Row.stockQuantity()).isEqualTo(3);
            assertThat(product1Row.optionGroupId()).isEqualTo(groupId);

            LegacyProductOptionQueryDto product2Row =
                    rows.stream().filter(r -> r.productId() == product2).findFirst().orElseThrow();
            assertThat(product2Row.stockQuantity()).isZero();
            assertThat(product2Row.soldOutYn()).isEqualTo("Y");
            assertThat(product2Row.optionGroupId()).isNull();
        }
    }
}
