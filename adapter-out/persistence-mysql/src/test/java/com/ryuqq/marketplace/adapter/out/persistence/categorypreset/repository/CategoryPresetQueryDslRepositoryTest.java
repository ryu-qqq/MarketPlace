package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.condition.CategoryPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
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
 * CategoryPresetQueryDslRepositoryTest - CategoryPreset QueryDslRepository 통합 테스트.
 *
 * <p>실제 데이터베이스 연동을 통한 조회 기능 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
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
@DisplayName("CategoryPresetQueryDslRepository 통합 테스트")
class CategoryPresetQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CategoryPresetQueryDslRepository repository;

    // 공통 테스트 데이터
    private SalesChannelJpaEntity salesChannel;
    private SalesChannelJpaEntity salesChannel2;
    private SalesChannelCategoryJpaEntity salesChannelCategory;
    private SalesChannelCategoryJpaEntity salesChannelCategory2;
    private ShopJpaEntity shop;
    private ShopJpaEntity shop2;

    @BeforeEach
    void setUp() {
        repository =
                new CategoryPresetQueryDslRepository(
                        new JPAQueryFactory(entityManager), new CategoryPresetConditionBuilder());

        Instant now = Instant.now();

        salesChannel = persist(SalesChannelJpaEntity.create(null, "테스트채널", "ACTIVE", now, now));
        salesChannel2 = persist(SalesChannelJpaEntity.create(null, "테스트채널2", "ACTIVE", now, now));

        salesChannelCategory =
                persist(
                        SalesChannelCategoryJpaEntity.create(
                                null,
                                salesChannel.getId(),
                                "CAT001",
                                "테스트카테고리",
                                null,
                                1,
                                "1",
                                0,
                                true,
                                "ACTIVE",
                                "의류 > 상의",
                                now,
                                now));

        salesChannelCategory2 =
                persist(
                        SalesChannelCategoryJpaEntity.create(
                                null,
                                salesChannel2.getId(),
                                "CAT002",
                                "테스트카테고리2",
                                null,
                                1,
                                "2",
                                0,
                                true,
                                "ACTIVE",
                                "신발 > 스니커즈",
                                now,
                                now));

        shop =
                persist(
                        ShopJpaEntity.create(
                                null,
                                salesChannel.getId(),
                                "테스트샵",
                                "account123",
                                "ACTIVE",
                                null,
                                null,
                                null,
                                null,
                                null,
                                now,
                                now,
                                null));

        shop2 =
                persist(
                        ShopJpaEntity.create(
                                null,
                                salesChannel2.getId(),
                                "다른샵",
                                "account456",
                                "ACTIVE",
                                null,
                                null,
                                null,
                                null,
                                null,
                                now,
                                now,
                                null));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private CategoryPresetJpaEntity persistPreset(
            Long shopId, Long salesChannelCategoryId, String presetName, String status) {
        Instant now = Instant.now();
        return persist(
                CategoryPresetJpaEntity.create(
                        null, shopId, salesChannelCategoryId, presetName, status, now, now));
    }

    private QueryContext<CategoryPresetSortKey> defaultQueryContext() {
        return QueryContext.of(
                CategoryPresetSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
    }

    private QueryContext<CategoryPresetSortKey> queryContext(int page, int size) {
        return QueryContext.of(
                CategoryPresetSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(page, size));
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Entity를 반환한다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            CategoryPresetJpaEntity preset =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "테스트프리셋", "ACTIVE");

            // when
            Optional<CategoryPresetJpaEntity> result = repository.findById(preset.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(preset.getId());
            assertThat(result.get().getPresetName()).isEqualTo("테스트프리셋");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<CategoryPresetJpaEntity> result = repository.findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("활성 Entity 목록을 ID 목록으로 조회한다")
        void findAllByIds_WithActiveEntities_ReturnsEntities() {
            // given
            CategoryPresetJpaEntity preset1 =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋1", "ACTIVE");
            CategoryPresetJpaEntity preset2 =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋2", "ACTIVE");

            // when
            List<CategoryPresetJpaEntity> result =
                    repository.findAllByIds(List.of(preset1.getId(), preset2.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(CategoryPresetJpaEntity::getId)
                    .containsExactlyInAnyOrder(preset1.getId(), preset2.getId());
        }

        @Test
        @DisplayName("INACTIVE 상태의 Entity는 조회되지 않는다")
        void findAllByIds_WithInactiveEntities_ExcludesInactive() {
            // given
            CategoryPresetJpaEntity activePreset =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "활성프리셋", "ACTIVE");
            CategoryPresetJpaEntity inactivePreset =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "비활성프리셋", "INACTIVE");

            // when
            List<CategoryPresetJpaEntity> result =
                    repository.findAllByIds(List.of(activePreset.getId(), inactivePreset.getId()));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(activePreset.getId());
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록을 반환한다")
        void findAllByIds_WithEmptyList_ReturnsEmptyList() {
            // when
            List<CategoryPresetJpaEntity> result = repository.findAllByIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 목록으로 조회 시 빈 목록을 반환한다")
        void findAllByIds_WithNonExistentIds_ReturnsEmptyList() {
            // when
            List<CategoryPresetJpaEntity> result = repository.findAllByIds(List.of(998L, 999L));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("필터 없이 전체 조회 시 모든 프리셋을 반환한다")
        void findByCriteria_WithNoFilter_ReturnsAll() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋B", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("salesChannelIds 필터로 특정 채널의 프리셋만 조회한다")
        void findByCriteria_WithSalesChannelFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "채널1프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "채널2프리셋", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).salesChannelId()).isEqualTo(salesChannel.getId());
        }

        @Test
        @DisplayName("statuses 필터로 특정 상태의 프리셋만 조회한다")
        void findByCriteria_WithStatusFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "활성프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "비활성프리셋", "INACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, List.of("ACTIVE"), null, null, null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("PRESET_NAME 검색 필드로 프리셋 이름을 검색한다")
        void findByCriteria_WithPresetNameSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "나이키프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "아디다스프리셋", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, "PRESET_NAME", "나이키", null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).presetName()).isEqualTo("나이키프리셋");
        }

        @Test
        @DisplayName("SHOP_NAME 검색 필드로 샵 이름을 검색한다")
        void findByCriteria_WithShopNameSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "프리셋B", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, "SHOP_NAME", "테스트샵", null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).shopName()).isEqualTo("테스트샵");
        }

        @Test
        @DisplayName("ACCOUNT_ID 검색 필드로 계정 ID를 검색한다")
        void findByCriteria_WithAccountIdSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "프리셋B", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            "ACCOUNT_ID",
                            "account123",
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).accountId()).isEqualTo("account123");
        }

        @Test
        @DisplayName("CATEGORY_CODE 검색 필드로 카테고리 코드를 검색한다")
        void findByCriteria_WithCategoryCodeSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "프리셋B", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            "CATEGORY_CODE",
                            "CAT001",
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalCategoryCode()).isEqualTo("CAT001");
        }

        @Test
        @DisplayName("CATEGORY_PATH 검색 필드로 카테고리 경로를 검색한다")
        void findByCriteria_WithCategoryPathSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "프리셋B", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, "CATEGORY_PATH", "의류", null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).categoryPath()).contains("의류");
        }

        @Test
        @DisplayName("startDate 필터로 지정 날짜 이후 생성된 프리셋을 조회한다")
        void findByCriteria_WithStartDateFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().minusDays(1),
                            null,
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("endDate 필터로 지정 날짜 이전 생성된 프리셋을 조회한다")
        void findByCriteria_WithEndDateFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("startDate + endDate 범위 필터로 프리셋을 조회한다")
        void findByCriteria_WithDateRangeFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("모든 필터 조합을 동시에 적용하여 조회한다")
        void findByCriteria_WithAllFilters_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "특정프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "다른프리셋", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            List.of("ACTIVE"),
                            "PRESET_NAME",
                            "특정",
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).presetName()).isEqualTo("특정프리셋");
        }

        @Test
        @DisplayName("페이지네이션이 정상 동작한다")
        void findByCriteria_WithPagination_ReturnsPagedResults() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋3", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext(0, 2));

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("2번째 페이지 조회 시 나머지 결과를 반환한다")
        void findByCriteria_WithSecondPage_ReturnsRemainingResults() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋3", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext(1, 2));

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("데이터가 없을 때 빈 목록을 반환한다")
        void findByCriteria_WithNoData_ReturnsEmptyList() {
            // given
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("복합 DTO의 필드가 올바르게 매핑된다")
        void findByCriteria_ResultDto_HasCorrectFieldMapping() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "매핑테스트", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<CategoryPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            CategoryPresetCompositeDto dto = result.get(0);
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelCategoryId()).isEqualTo(salesChannelCategory.getId());
            assertThat(dto.externalCategoryCode()).isEqualTo("CAT001");
            assertThat(dto.categoryPath()).isEqualTo("의류 > 상의");
            assertThat(dto.presetName()).isEqualTo("매핑테스트");
            assertThat(dto.status()).isEqualTo("ACTIVE");
            assertThat(dto.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("필터 없이 전체 개수를 반환한다")
        void countByCriteria_WithNoFilter_ReturnsTotalCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋3", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("salesChannelIds 필터로 필터링된 개수를 반환한다")
        void countByCriteria_WithSalesChannelFilter_ReturnsFilteredCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "채널1프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "채널2프리셋", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("statuses 필터로 필터링된 개수를 반환한다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "활성1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "활성2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "비활성", "INACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, List.of("ACTIVE"), null, null, null, null, defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("검색 조건으로 필터링된 개수를 반환한다")
        void countByCriteria_WithSearchFilter_ReturnsFilteredCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "나이키프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "아디다스프리셋", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, "PRESET_NAME", "나이키", null, null, defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("날짜 범위 필터로 필터링된 개수를 반환한다")
        void countByCriteria_WithDateRangeFilter_ReturnsFilteredCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "프리셋A", "ACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("데이터가 없을 때 0을 반환한다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("모든 필터 조합을 동시에 적용하여 개수를 반환한다")
        void countByCriteria_WithAllFilters_ReturnsFilteredCount() {
            // given
            persistPreset(shop.getId(), salesChannelCategory.getId(), "특정프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelCategory2.getId(), "다른프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelCategory.getId(), "비활성프리셋", "INACTIVE");

            CategoryPresetSearchCriteria criteria =
                    new CategoryPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            List.of("ACTIVE"),
                            "PRESET_NAME",
                            "특정",
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            long count = repository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("findSalesChannelCategoryIdByCode")
    class FindSalesChannelCategoryIdByCodeTest {

        @Test
        @DisplayName("salesChannelId와 categoryCode로 카테고리 ID를 조회한다")
        void findSalesChannelCategoryIdByCode_WithExistingData_ReturnsId() {
            // when
            Optional<Long> result =
                    repository.findSalesChannelCategoryIdByCode(salesChannel.getId(), "CAT001");

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(salesChannelCategory.getId());
        }

        @Test
        @DisplayName("존재하지 않는 salesChannelId로 조회 시 빈 Optional을 반환한다")
        void findSalesChannelCategoryIdByCode_WithNonExistentSalesChannelId_ReturnsEmpty() {
            // when
            Optional<Long> result = repository.findSalesChannelCategoryIdByCode(999L, "CAT001");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 categoryCode로 조회 시 빈 Optional을 반환한다")
        void findSalesChannelCategoryIdByCode_WithNonExistentCode_ReturnsEmpty() {
            // when
            Optional<Long> result =
                    repository.findSalesChannelCategoryIdByCode(
                            salesChannel.getId(), "NON_EXISTENT");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDetailCompositeById")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 프리셋 ID로 조회 시 CategoryPresetDetailCompositeDto를 반환한다")
        void findDetailCompositeById_WithExistingId_ReturnsDto() {
            // given
            CategoryPresetJpaEntity preset =
                    persistPreset(shop.getId(), salesChannelCategory.getId(), "테스트프리셋", "ACTIVE");

            // when
            Optional<CategoryPresetDetailCompositeDto> result =
                    repository.findDetailCompositeById(preset.getId());

            // then
            assertThat(result).isPresent();
            CategoryPresetDetailCompositeDto dto = result.get();
            assertThat(dto.id()).isEqualTo(preset.getId());
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelCategoryId()).isEqualTo(salesChannelCategory.getId());
            assertThat(dto.externalCategoryCode()).isEqualTo("CAT001");
            assertThat(dto.categoryDisplayPath()).isEqualTo("의류 > 상의");
            assertThat(dto.presetName()).isEqualTo("테스트프리셋");
            assertThat(dto.status()).isEqualTo("ACTIVE");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty()를 반환한다")
        void findDetailCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<CategoryPresetDetailCompositeDto> result =
                    repository.findDetailCompositeById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
