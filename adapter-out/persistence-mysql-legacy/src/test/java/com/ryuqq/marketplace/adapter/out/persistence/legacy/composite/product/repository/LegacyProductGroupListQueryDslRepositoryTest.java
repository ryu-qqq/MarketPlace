package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeProductTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupListQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
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
 * LegacyProductGroupListQueryDslRepositoryTest - 상품그룹 목록 조회 QueryDSL Repository 통합 테스트.
 *
 * <p>3-Phase Query 패턴(ID 조회 → 상세 조회 → 상품+옵션 조회) 및 검색 조건을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
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
@DisplayName("LegacyProductGroupListQueryDslRepository 통합 테스트")
class LegacyProductGroupListQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyProductGroupListQueryDslRepository repository;
    private LegacyCompositeProductTestHelper helper;

    @BeforeEach
    void setUp() {
        LegacyProductGroupListConditionBuilder conditionBuilder =
                new LegacyProductGroupListConditionBuilder();
        repository =
                new LegacyProductGroupListQueryDslRepository(
                        new JPAQueryFactory(entityManager), conditionBuilder);
        helper = new LegacyCompositeProductTestHelper(entityManager);
    }

    private LegacyProductGroupSearchCriteria defaultCriteria() {
        return LegacyProductGroupSearchCriteria.of(
                null, null, List.of(), null, null, null, null, null, null, null, null, null, null,
                null, 0, 10);
    }

    // ========================================================================
    // 1. fetchProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchProductGroupIds 메서드 테스트")
    class FetchProductGroupIdsTest {

        @Test
        @DisplayName("검색 조건에 맞는 상품그룹 ID 목록을 ID 내림차순으로 조회합니다")
        void fetchProductGroupIds_WithData_ReturnsIdsDescending() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            long id1 = helper.persistProductGroup(10L, 20L, 30L);
            long id2 = helper.persistProductGroup(10L, 20L, 30L);
            long id3 = helper.persistProductGroup(10L, 20L, 30L);
            helper.flushAndClear();

            // when
            List<Long> ids = repository.fetchProductGroupIds(defaultCriteria());

            // then
            assertThat(ids).hasSize(3);
            assertThat(ids).containsExactly(id3, id2, id1); // ID 내림차순
        }

        @Test
        @DisplayName("삭제된 상품그룹은 조회에서 제외됩니다")
        void fetchProductGroupIds_WithDeletedGroup_ExcludesDeleted() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L); // 활성

            // 삭제된 상품그룹
            entityManager
                    .createNativeQuery(
                            "INSERT INTO product_group (product_group_name, seller_id, brand_id, category_id, "
                                    + "option_type, management_type, regular_price, current_price, sale_price, "
                                    + "sold_out_yn, display_yn, delete_yn) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                    .setParameter(1, "삭제됨")
                    .setParameter(2, 10L)
                    .setParameter(3, 20L)
                    .setParameter(4, 30L)
                    .setParameter(5, "SINGLE")
                    .setParameter(6, "SYSTEM")
                    .setParameter(7, 50000L)
                    .setParameter(8, 45000L)
                    .setParameter(9, 45000L)
                    .setParameter(10, "N")
                    .setParameter(11, "Y")
                    .setParameter(12, "Y")
                    .executeUpdate();
            helper.flushAndClear();

            // when
            List<Long> ids = repository.fetchProductGroupIds(defaultCriteria());

            // then
            assertThat(ids).hasSize(1);
        }

        @Test
        @DisplayName("페이징(offset/limit)이 올바르게 적용됩니다")
        void fetchProductGroupIds_WithPaging_AppliesOffsetAndLimit() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            for (int i = 0; i < 5; i++) {
                helper.persistProductGroup(10L, 20L, 30L);
            }
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            null, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 1, 2); // page=1, size=2

            // when
            List<Long> ids = repository.fetchProductGroupIds(criteria);

            // then
            assertThat(ids).hasSize(2);
        }

        @Test
        @DisplayName("sellerId 조건으로 필터링됩니다")
        void fetchProductGroupIds_WithSellerIdFilter_FiltersCorrectly() {
            // given
            helper.insertSeller(10L, "셀러A");
            helper.insertSeller(11L, "셀러B");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(11L, 20L, 30L);
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            10L, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 10);

            // when
            List<Long> ids = repository.fetchProductGroupIds(criteria);

            // then
            assertThat(ids).hasSize(2);
        }

        @Test
        @DisplayName("soldOutYn 조건으로 필터링됩니다")
        void fetchProductGroupIds_WithSoldOutFilter_FiltersCorrectly() {
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
            List<Long> ids = repository.fetchProductGroupIds(criteria);

            // then
            assertThat(ids).hasSize(1);
        }

        @Test
        @DisplayName("데이터가 없으면 빈 목록을 반환합니다")
        void fetchProductGroupIds_WithNoData_ReturnsEmptyList() {
            // when
            List<Long> ids = repository.fetchProductGroupIds(defaultCriteria());

            // then
            assertThat(ids).isEmpty();
        }
    }

    // ========================================================================
    // 2. fetchProductGroupDetails 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchProductGroupDetails 메서드 테스트")
    class FetchProductGroupDetailsTest {

        @Test
        @DisplayName("ID 목록으로 상품그룹 상세 정보를 조회합니다")
        void fetchProductGroupDetails_WithIds_ReturnsDetails() {
            // given
            helper.insertSeller(10L, "테스트 셀러");
            helper.insertBrand(20L, "나이키");
            helper.insertCategory(30L, "상의", "패션>의류>상의");

            long pgId = helper.persistProductGroup(10L, 20L, 30L);
            helper.persistDelivery(pgId);
            helper.persistImage(pgId, "MAIN", "https://cdn.example.com/main.jpg");
            helper.flushAndClear();

            // when
            List<LegacyProductGroupListQueryDto> details =
                    repository.fetchProductGroupDetails(List.of(pgId));

            // then
            assertThat(details).hasSize(1);
            LegacyProductGroupListQueryDto dto = details.get(0);
            assertThat(dto.productGroupId()).isEqualTo(pgId);
            assertThat(dto.sellerName()).isEqualTo("테스트 셀러");
            assertThat(dto.brandName()).isEqualTo("나이키");
            assertThat(dto.categoryPath()).isEqualTo("패션>의류>상의");
            assertThat(dto.mainImageUrl()).isEqualTo("https://cdn.example.com/main.jpg");
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 목록을 반환합니다")
        void fetchProductGroupDetails_WithEmptyIds_ReturnsEmptyList() {
            // when
            List<LegacyProductGroupListQueryDto> details =
                    repository.fetchProductGroupDetails(List.of());

            // then
            assertThat(details).isEmpty();
        }

        @Test
        @DisplayName("null ID 목록 입력 시 빈 목록을 반환합니다")
        void fetchProductGroupDetails_WithNullIds_ReturnsEmptyList() {
            // when
            List<LegacyProductGroupListQueryDto> details =
                    repository.fetchProductGroupDetails(null);

            // then
            assertThat(details).isEmpty();
        }

        @Test
        @DisplayName("MAIN 이미지가 없는 경우 mainImageUrl이 null입니다")
        void fetchProductGroupDetails_WithNoMainImage_ReturnsNullMainImageUrl() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            long pgId = helper.persistProductGroup(10L, 20L, 30L);
            helper.flushAndClear();

            // when
            List<LegacyProductGroupListQueryDto> details =
                    repository.fetchProductGroupDetails(List.of(pgId));

            // then
            assertThat(details).hasSize(1);
            assertThat(details.get(0).mainImageUrl()).isNull();
        }
    }

    // ========================================================================
    // 3. fetchProductsWithOptions 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchProductsWithOptions 메서드 테스트")
    class FetchProductsWithOptionsTest {

        @Test
        @DisplayName("ID 목록으로 상품+옵션 flat 데이터를 조회합니다")
        void fetchProductsWithOptions_WithIds_ReturnsFlatRows() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            long pgId = helper.persistProductGroup(10L, 20L, 30L);
            long productId = helper.persistProduct(pgId, "N");
            helper.persistProductStock(productId, 10);

            long groupId = helper.persistOptionGroup("색상");
            long detailId = helper.persistOptionDetail(groupId, "블랙");
            helper.persistProductOption(productId, groupId, detailId);
            helper.flushAndClear();

            // when
            List<LegacyProductOptionQueryDto> rows =
                    repository.fetchProductsWithOptions(List.of(pgId));

            // then
            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).productGroupId()).isEqualTo(pgId);
            assertThat(rows.get(0).optionGroupId()).isEqualTo(groupId);
            assertThat(rows.get(0).optionValue()).isEqualTo("블랙");
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 목록을 반환합니다")
        void fetchProductsWithOptions_WithEmptyIds_ReturnsEmptyList() {
            // when
            List<LegacyProductOptionQueryDto> rows =
                    repository.fetchProductsWithOptions(List.of());

            // then
            assertThat(rows).isEmpty();
        }

        @Test
        @DisplayName("null ID 목록 입력 시 빈 목록을 반환합니다")
        void fetchProductsWithOptions_WithNullIds_ReturnsEmptyList() {
            // when
            List<LegacyProductOptionQueryDto> rows =
                    repository.fetchProductsWithOptions(null);

            // then
            assertThat(rows).isEmpty();
        }
    }

    // ========================================================================
    // 4. count 테스트
    // ========================================================================

    @Nested
    @DisplayName("count 메서드 테스트")
    class CountTest {

        @Test
        @DisplayName("검색 조건에 맞는 전체 건수를 반환합니다")
        void count_WithData_ReturnsCorrectCount() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(10L, 20L, 30L);
            helper.flushAndClear();

            // when
            long count = repository.count(defaultCriteria());

            // then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void count_WithNoData_ReturnsZero() {
            // when
            long count = repository.count(defaultCriteria());

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("삭제된 상품그룹은 카운트에서 제외됩니다")
        void count_WithDeletedGroup_ExcludesDeleted() {
            // given
            helper.insertSeller(10L, "셀러");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L); // 활성

            entityManager
                    .createNativeQuery(
                            "INSERT INTO product_group (product_group_name, seller_id, brand_id, category_id, "
                                    + "option_type, management_type, regular_price, current_price, sale_price, "
                                    + "sold_out_yn, display_yn, delete_yn) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                    .setParameter(1, "삭제됨")
                    .setParameter(2, 10L)
                    .setParameter(3, 20L)
                    .setParameter(4, 30L)
                    .setParameter(5, "SINGLE")
                    .setParameter(6, "SYSTEM")
                    .setParameter(7, 50000L)
                    .setParameter(8, 45000L)
                    .setParameter(9, 45000L)
                    .setParameter(10, "N")
                    .setParameter(11, "Y")
                    .setParameter(12, "Y")
                    .executeUpdate();
            helper.flushAndClear();

            // when
            long count = repository.count(defaultCriteria());

            // then
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("sellerId 조건으로 카운트가 필터링됩니다")
        void count_WithSellerIdFilter_CountsCorrectly() {
            // given
            helper.insertSeller(10L, "셀러A");
            helper.insertSeller(11L, "셀러B");
            helper.insertBrand(20L, "브랜드");
            helper.insertCategory(30L, "카테고리", "100");

            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(10L, 20L, 30L);
            helper.persistProductGroup(11L, 20L, 30L);
            helper.flushAndClear();

            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupSearchCriteria.of(
                            10L, null, List.of(), null, null, null, null, null, null, null, null,
                            null, null, null, 0, 10);

            // when
            long count = repository.count(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }
    }
}
