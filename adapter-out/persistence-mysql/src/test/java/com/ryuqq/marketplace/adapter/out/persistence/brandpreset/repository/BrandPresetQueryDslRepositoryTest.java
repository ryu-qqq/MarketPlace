package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.condition.BrandPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSortKey;
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
 * BrandPresetQueryDslRepositoryTest - BrandPreset QueryDslRepository 통합 테스트.
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
@DisplayName("BrandPresetQueryDslRepository 통합 테스트")
class BrandPresetQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private BrandPresetQueryDslRepository repository;

    // 공통 테스트 데이터
    private SalesChannelJpaEntity salesChannel;
    private SalesChannelJpaEntity salesChannel2;
    private SalesChannelBrandJpaEntity salesChannelBrand;
    private SalesChannelBrandJpaEntity salesChannelBrand2;
    private ShopJpaEntity shop;
    private ShopJpaEntity shop2;

    @BeforeEach
    void setUp() {
        repository =
                new BrandPresetQueryDslRepository(
                        new JPAQueryFactory(entityManager), new BrandPresetConditionBuilder());

        Instant now = Instant.now();

        salesChannel = persist(SalesChannelJpaEntity.create(null, "테스트채널", "ACTIVE", now, now));
        salesChannel2 = persist(SalesChannelJpaEntity.create(null, "테스트채널2", "ACTIVE", now, now));

        salesChannelBrand =
                persist(
                        SalesChannelBrandJpaEntity.create(
                                null, salesChannel.getId(), "B001", "나이키", "ACTIVE", now, now));

        salesChannelBrand2 =
                persist(
                        SalesChannelBrandJpaEntity.create(
                                null, salesChannel2.getId(), "B002", "아디다스", "ACTIVE", now, now));

        shop =
                persist(
                        ShopJpaEntity.create(
                                null,
                                salesChannel.getId(),
                                "테스트샵",
                                "account123",
                                "ACTIVE",
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

    private BrandPresetJpaEntity persistPreset(
            Long shopId, Long salesChannelBrandId, String presetName, String status) {
        Instant now = Instant.now();
        return persist(
                BrandPresetJpaEntity.create(
                        null, shopId, salesChannelBrandId, presetName, status, now, now));
    }

    private QueryContext<BrandPresetSortKey> defaultQueryContext() {
        return QueryContext.of(
                BrandPresetSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
    }

    private QueryContext<BrandPresetSortKey> queryContext(int page, int size) {
        return QueryContext.of(
                BrandPresetSortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(page, size));
    }

    private QueryContext<BrandPresetSortKey> queryContextWithDirection(SortDirection direction) {
        return QueryContext.of(BrandPresetSortKey.CREATED_AT, direction, PageRequest.of(0, 20));
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("활성 Entity는 findById로 조회됩니다")
        void findById_WithActiveEntity_ReturnsEntity() {
            // given
            BrandPresetJpaEntity saved =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "테스트프리셋", "ACTIVE");

            // when
            var result = repository.findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            var result = repository.findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("활성 Entity 목록을 ID 목록으로 조회합니다")
        void findAllByIds_WithActiveEntities_ReturnsEntities() {
            // given
            BrandPresetJpaEntity entity1 =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋1", "ACTIVE");
            BrandPresetJpaEntity entity2 =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋2", "ACTIVE");
            BrandPresetJpaEntity entity3 =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋3", "ACTIVE");

            // when
            var result =
                    repository.findAllByIds(
                            List.of(entity1.getId(), entity2.getId(), entity3.getId()));

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(BrandPresetJpaEntity::getId)
                    .containsExactlyInAnyOrder(entity1.getId(), entity2.getId(), entity3.getId());
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findAllByIds_WithEmptyList_ReturnsEmptyList() {
            // when
            var result = repository.findAllByIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findAllByIds_WithNonExistentIds_ReturnsEmptyList() {
            // when
            var result = repository.findAllByIds(List.of(998L, 999L));

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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("salesChannelIds 필터로 특정 채널의 프리셋만 조회한다")
        void findByCriteria_WithSalesChannelFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "채널1프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "채널2프리셋", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).salesChannelId()).isEqualTo(salesChannel.getId());
        }

        @Test
        @DisplayName("statuses 필터로 특정 상태의 프리셋만 조회한다")
        void findByCriteria_WithStatusFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "활성프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "비활성프리셋", "INACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, List.of("ACTIVE"), null, null, null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("PRESET_NAME 검색 필드로 프리셋 이름을 검색한다")
        void findByCriteria_WithPresetNameSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "나이키프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "아디다스프리셋", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, "PRESET_NAME", "나이키", null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).presetName()).isEqualTo("나이키프리셋");
        }

        @Test
        @DisplayName("SHOP_NAME 검색 필드로 샵 이름을 검색한다")
        void findByCriteria_WithShopNameSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, "SHOP_NAME", "테스트샵", null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).shopName()).isEqualTo("테스트샵");
        }

        @Test
        @DisplayName("ACCOUNT_ID 검색 필드로 계정 ID를 검색한다")
        void findByCriteria_WithAccountIdSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            "ACCOUNT_ID",
                            "account123",
                            null,
                            null,
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).accountId()).isEqualTo("account123");
        }

        @Test
        @DisplayName("BRAND_NAME 검색 필드로 브랜드 이름을 검색한다")
        void findByCriteria_WithBrandNameSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, "BRAND_NAME", "나이키", null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalBrandName()).isEqualTo("나이키");
        }

        @Test
        @DisplayName("BRAND_CODE 검색 필드로 브랜드 코드를 검색한다")
        void findByCriteria_WithBrandCodeSearch_ReturnsMatching() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, "BRAND_CODE", "B001", null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalBrandCode()).isEqualTo("B001");
        }

        @Test
        @DisplayName("startDate 필터로 지정 날짜 이후 생성된 프리셋을 조회한다")
        void findByCriteria_WithStartDateFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().minusDays(1),
                            null,
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("endDate 필터로 지정 날짜 이전 생성된 프리셋을 조회한다")
        void findByCriteria_WithEndDateFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("startDate + endDate 범위 필터로 프리셋을 조회한다")
        void findByCriteria_WithDateRangeFilter_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("모든 필터 조합을 동시에 적용하여 조회한다")
        void findByCriteria_WithAllFilters_ReturnsFiltered() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "특정프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "다른프리셋", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            List.of(salesChannel.getId()),
                            List.of("ACTIVE"),
                            "PRESET_NAME",
                            "특정",
                            LocalDate.now().minusDays(1),
                            LocalDate.now().plusDays(1),
                            defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).presetName()).isEqualTo("특정프리셋");
        }

        @Test
        @DisplayName("페이지네이션이 정상 동작한다")
        void findByCriteria_WithPagination_ReturnsPagedResults() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋3", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext(0, 2));

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("2번째 페이지 조회 시 나머지 결과를 반환한다")
        void findByCriteria_WithSecondPage_ReturnsRemainingResults() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋3", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, null, null, null, null, queryContext(1, 2));

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("ASC 정렬로 조회 시 오름차순으로 반환한다")
        void findByCriteria_WithAscSortDirection_ReturnsAscOrder() {
            // given
            BrandPresetJpaEntity first =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            BrandPresetJpaEntity second =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            queryContextWithDirection(SortDirection.ASC));

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(first.getId());
            assertThat(result.get(1).id()).isEqualTo(second.getId());
        }

        @Test
        @DisplayName("DESC 정렬로 조회 시 내림차순으로 반환한다")
        void findByCriteria_WithDescSortDirection_ReturnsDescOrder() {
            // given
            BrandPresetJpaEntity first =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");
            BrandPresetJpaEntity second =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋B", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            queryContextWithDirection(SortDirection.DESC));

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(second.getId());
            assertThat(result.get(1).id()).isEqualTo(first.getId());
        }

        @Test
        @DisplayName("데이터가 없을 때 빈 목록을 반환한다")
        void findByCriteria_WithNoData_ReturnsEmptyList() {
            // given
            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("복합 DTO의 필드가 올바르게 매핑된다")
        void findByCriteria_ResultDto_HasCorrectFieldMapping() {
            // given
            persistPreset(shop.getId(), salesChannelBrand.getId(), "매핑테스트", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
                            null, null, null, null, null, null, defaultQueryContext());

            // when
            List<BrandPresetCompositeDto> result = repository.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            BrandPresetCompositeDto dto = result.get(0);
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelBrandId()).isEqualTo(salesChannelBrand.getId());
            assertThat(dto.externalBrandCode()).isEqualTo("B001");
            assertThat(dto.externalBrandName()).isEqualTo("나이키");
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋3", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "채널1프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "채널2프리셋", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "활성1", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "활성2", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "비활성", "INACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "나이키프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "아디다스프리셋", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "프리셋A", "ACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
            persistPreset(shop.getId(), salesChannelBrand.getId(), "특정프리셋", "ACTIVE");
            persistPreset(shop2.getId(), salesChannelBrand2.getId(), "다른프리셋", "ACTIVE");
            persistPreset(shop.getId(), salesChannelBrand.getId(), "비활성프리셋", "INACTIVE");

            BrandPresetSearchCriteria criteria =
                    new BrandPresetSearchCriteria(
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
    @DisplayName("findSalesChannelIdBySalesChannelBrandId")
    class FindSalesChannelIdBySalesChannelBrandIdTest {

        @Test
        @DisplayName("존재하는 SalesChannelBrandId로 조회 시 SalesChannelId를 반환한다")
        void findSalesChannelIdBySalesChannelBrandId_WithExistingId_ReturnsId() {
            // when
            var result =
                    repository.findSalesChannelIdBySalesChannelBrandId(salesChannelBrand.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(salesChannel.getId());
        }

        @Test
        @DisplayName("존재하지 않는 SalesChannelBrandId로 조회 시 빈 Optional을 반환합니다")
        void findSalesChannelIdBySalesChannelBrandId_WithNonExistentId_ReturnsEmpty() {
            // when
            var result = repository.findSalesChannelIdBySalesChannelBrandId(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDetailCompositeById")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 프리셋 ID로 조회 시 BrandPresetDetailCompositeDto를 반환한다")
        void findDetailCompositeById_WithExistingId_ReturnsDto() {
            // given
            BrandPresetJpaEntity preset =
                    persistPreset(shop.getId(), salesChannelBrand.getId(), "테스트프리셋", "ACTIVE");

            // when
            Optional<BrandPresetDetailCompositeDto> result =
                    repository.findDetailCompositeById(preset.getId());

            // then
            assertThat(result).isPresent();
            BrandPresetDetailCompositeDto dto = result.get();
            assertThat(dto.id()).isEqualTo(preset.getId());
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelBrandId()).isEqualTo(salesChannelBrand.getId());
            assertThat(dto.externalBrandCode()).isEqualTo("B001");
            assertThat(dto.externalBrandName()).isEqualTo("나이키");
            assertThat(dto.presetName()).isEqualTo("테스트프리셋");
            assertThat(dto.status()).isEqualTo("ACTIVE");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty()를 반환한다")
        void findDetailCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<BrandPresetDetailCompositeDto> result =
                    repository.findDetailCompositeById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
