package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition.OmsProductConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchField;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSortKey;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * OmsProductCompositionQueryDslRepositoryTest - OMS 상품 목록 Composition QueryDSL 레포지토리 통합 테스트.
 *
 * <p>product_groups WHERE EXISTS(outbound_products) LEFT JOIN sellers LEFT JOIN brands + 필터 조회 검증.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("OmsProductCompositionQueryDslRepository 통합 테스트")
class OmsProductCompositionQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private OmsProductCompositionQueryDslRepository repository;

    // 공통 테스트 데이터
    private SellerJpaEntity seller;
    private SellerJpaEntity seller2;
    private BrandJpaEntity brand;
    private ShopJpaEntity shop;
    private ProductGroupJpaEntity activeProductGroup;
    private ProductGroupJpaEntity inactiveProductGroup;
    private ProductGroupJpaEntity deletedProductGroup;
    private ProductGroupJpaEntity noOutboundProductGroup;

    @BeforeEach
    void setUp() {
        repository =
                new OmsProductCompositionQueryDslRepository(
                        new JPAQueryFactory(entityManager), new OmsProductConditionBuilder());

        Instant now = Instant.now();

        seller = persist(createSeller(null, "테스트셀러", now));
        seller2 = persist(createSeller(null, "다른셀러", now));
        brand = persist(createBrand(null, "테스트브랜드", now));

        // outbound_products와 JOIN되는 Shop 엔티티 생성
        shop = persist(createShop(now));

        activeProductGroup =
                persist(
                        createProductGroup(
                                null, seller.getId(), brand.getId(), "활성상품", "ACTIVE", now));
        inactiveProductGroup =
                persist(
                        createProductGroup(
                                null, seller.getId(), brand.getId(), "중단상품", "INACTIVE", now));
        deletedProductGroup =
                persist(
                        createProductGroup(
                                null, seller.getId(), brand.getId(), "삭제상품", "DELETED", now));
        noOutboundProductGroup =
                persist(
                        createProductGroup(
                                null, seller.getId(), brand.getId(), "아웃바운드없음", "ACTIVE", now));

        // activeProductGroup, inactiveProductGroup, deletedProductGroup에는 OutboundProduct 생성
        persist(createOutboundProduct(null, activeProductGroup.getId(), 1L, shop.getId(), now));
        persist(createOutboundProduct(null, inactiveProductGroup.getId(), 1L, shop.getId(), now));
        persist(createOutboundProduct(null, deletedProductGroup.getId(), 1L, shop.getId(), now));
        // noOutboundProductGroup은 OutboundProduct 없음
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SellerJpaEntity createSeller(Long id, String name, Instant now) {
        return SellerJpaEntity.create(
                id, name, name + " 디스플레이", null, null, true, null, null, now, now, null);
    }

    private BrandJpaEntity createBrand(Long id, String nameKo, Instant now) {
        return BrandJpaEntity.create(
                id, nameKo + "CODE", nameKo, nameKo + "En", null, "ACTIVE", null, now, now, null);
    }

    private ProductGroupJpaEntity createProductGroup(
            Long id, Long sellerId, Long brandId, String name, String status, Instant now) {
        return ProductGroupJpaEntity.create(
                id, sellerId, brandId, 1L, 1L, 1L, name, "NONE", status, now, now);
    }

    private OutboundProductJpaEntity createOutboundProduct(
            Long id, Long productGroupId, Long salesChannelId, Long shopId, Instant now) {
        return OutboundProductJpaEntity.create(
                id, productGroupId, salesChannelId, shopId, null, "PENDING_REGISTRATION", now, now);
    }

    private ShopJpaEntity createShop(Instant now) {
        return ShopJpaEntity.create(
                null, 1L, "테스트샵", "test-account", "ACTIVE", "TEST", null, null, null, null, now,
                now, null);
    }

    private QueryContext<OmsProductSortKey> defaultQueryContext() {
        return QueryContext.of(
                OmsProductSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
    }

    // ========================================================================
    // 1. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("필터 없이 조회 시 DELETED 제외 + OutboundProduct 있는 상품만 반환합니다")
        void findByCriteria_WithNoFilter_ReturnsNonDeletedWithOutboundProduct() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            // ACTIVE + INACTIVE만 반환 (DELETED 제외, OutboundProduct 없는 것도 제외)
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(OmsProductListCompositeDto::productGroupName)
                    .doesNotContain("삭제상품", "아웃바운드없음");
        }

        @Test
        @DisplayName("OutboundProduct가 없는 상품그룹은 조회되지 않습니다")
        void findByCriteria_ProductGroupWithoutOutboundProduct_IsExcluded() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result)
                    .extracting(OmsProductListCompositeDto::productGroupId)
                    .doesNotContain(noOutboundProductGroup.getId());
        }

        @Test
        @DisplayName("ACTIVE 상태 필터 적용 시 ACTIVE 상품만 반환합니다")
        void findByCriteria_WithActiveStatusFilter_ReturnsOnlyActiveProducts() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(ProductGroupStatus.ACTIVE),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("셀러 ID 필터 적용 시 해당 셀러의 상품만 반환합니다")
        void findByCriteria_WithSellerIdFilter_ReturnsSellerProducts() {
            // given
            Instant now = Instant.now();
            ProductGroupJpaEntity seller2ProductGroup =
                    persist(
                            createProductGroup(
                                    null, seller2.getId(), brand.getId(), "셀러2상품", "ACTIVE", now));
            persist(createOutboundProduct(null, seller2ProductGroup.getId(), 1L, shop.getId(), now));

            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(seller2.getId()),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).sellerId()).isEqualTo(seller2.getId());
        }

        @Test
        @DisplayName("상품명 검색 조건 적용 시 해당 상품만 반환합니다")
        void findByCriteria_WithProductNameSearch_ReturnsMatchingProducts() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            OmsProductSearchField.PRODUCT_NAME,
                            "활성상품",
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).productGroupName()).isEqualTo("활성상품");
        }

        @Test
        @DisplayName("PG- 접두사 상품 코드로 검색 시 해당 상품을 반환합니다")
        void findByCriteria_WithProductCodePgPrefix_ReturnsMatchingProduct() {
            // given
            String productCode = "PG-" + activeProductGroup.getId();
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            OmsProductSearchField.PRODUCT_CODE,
                            productCode,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).productGroupId()).isEqualTo(activeProductGroup.getId());
        }

        @Test
        @DisplayName("날짜 범위 필터 적용 시 해당 범위 내 상품만 반환합니다")
        void findByCriteria_WithDateRangeFilter_ReturnsProductsInRange() {
            // given
            com.ryuqq.marketplace.domain.common.vo.DateRange dateRange =
                    com.ryuqq.marketplace.domain.common.vo.DateRange.of(
                            java.time.LocalDate.now().minusDays(1),
                            java.time.LocalDate.now().plusDays(1));

            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            dateRange,
                            "CREATED_AT",
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2); // ACTIVE + INACTIVE (DELETED 제외)
        }

        @Test
        @DisplayName("결과 DTO에 셀러명과 브랜드명이 올바르게 매핑됩니다")
        void findByCriteria_ResultDto_HasCorrectSellerAndBrandMapping() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(ProductGroupStatus.ACTIVE),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            OmsProductListCompositeDto dto = result.get(0);
            assertThat(dto.sellerName()).isEqualTo("테스트셀러");
            assertThat(dto.brandName()).isEqualTo("테스트브랜드");
            assertThat(dto.productGroupId()).isEqualTo(activeProductGroup.getId());
        }

        @Test
        @DisplayName("페이지네이션이 정상 동작합니다")
        void findByCriteria_WithPagination_ReturnsPagedResults() {
            // given
            QueryContext<OmsProductSortKey> pageContext =
                    QueryContext.of(
                            OmsProductSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 1));
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            pageContext);

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("데이터가 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoMatchingData_ReturnsEmptyList() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(999999L),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<OmsProductListCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("필터 없이 전체 개수를 반환합니다 (DELETED 제외 + OutboundProduct 있는 것만)")
        void countByCriteria_WithNoFilter_ReturnsTotalCount() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L); // ACTIVE + INACTIVE
        }

        @Test
        @DisplayName("ACTIVE 상태 필터 적용 시 해당 개수를 반환합니다")
        void countByCriteria_WithActiveStatusFilter_ReturnsFilteredCount() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(ProductGroupStatus.ACTIVE),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("매칭되는 데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoMatchingData_ReturnsZero() {
            // given
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(999999L),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
