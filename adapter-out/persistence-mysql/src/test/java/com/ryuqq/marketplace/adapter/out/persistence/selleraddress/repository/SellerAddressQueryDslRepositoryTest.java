package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition.SellerAddressConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerAddressQueryDslRepositoryTest - 셀러 주소 QueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 적용을 우선 검증합니다.
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
@DisplayName("SellerAddressQueryDslRepository 통합 테스트")
class SellerAddressQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAddressQueryDslRepository repository() {
        return new SellerAddressQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAddressConditionBuilder());
    }

    private SellerAddressJpaEntity persist(SellerAddressJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            SellerAddressJpaEntity saved =
                    persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(1L));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerAddressJpaEntity deleted =
                    persist(SellerAddressJpaEntityFixtures.deletedEntity(1L));

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllBySellerId")
    class FindAllBySellerIdTest {

        @Test
        @DisplayName("셀러의 모든 주소를 조회합니다")
        void findAllBySellerId_ReturnsMultipleAddresses() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.defaultReturnEntity(sellerId));

            List<SellerAddressJpaEntity> result = repository().findAllBySellerId(sellerId);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 주소는 findAllBySellerId에서 제외됩니다")
        void findAllBySellerId_ExcludesDeleted() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            List<SellerAddressJpaEntity> result = repository().findAllBySellerId(sellerId);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findDefaultAddress")
    class FindDefaultAddressTest {

        @Test
        @DisplayName("기본 SHIPPING 주소를 조회합니다")
        void findDefaultAddress_WithShipping_ReturnsDefault() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(sellerId, "보조 창고"));

            var result =
                    repository()
                            .findDefaultAddress(
                                    sellerId,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING);

            assertThat(result).isPresent();
            assertThat(result.get().isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("기본 RETURN 주소를 조회합니다")
        void findDefaultAddress_WithReturn_ReturnsDefault() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultReturnEntity(sellerId));

            var result =
                    repository()
                            .findDefaultAddress(
                                    sellerId,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_RETURN);

            assertThat(result).isPresent();
            assertThat(result.get().isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("삭제된 기본 주소는 조회되지 않습니다")
        void findDefaultAddress_WithDeleted_ReturnsEmpty() {
            Long sellerId = 1L;
            SellerAddressJpaEntity deleted =
                    persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            var result =
                    repository()
                            .findDefaultAddress(
                                    sellerId,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBySellerId")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("미삭제 주소가 있으면 true를 반환합니다")
        void existsBySellerId_WithNotDeleted_ReturnsTrue() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));

            boolean exists = repository().existsBySellerId(sellerId);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("삭제된 주소만 있으면 false를 반환합니다")
        void existsBySellerId_WithOnlyDeleted_ReturnsFalse() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            boolean exists = repository().existsBySellerId(sellerId);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("주소가 없으면 false를 반환합니다")
        void existsBySellerId_WithNoAddress_ReturnsFalse() {
            boolean exists = repository().existsBySellerId(999L);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySellerIdAndAddressTypeAndAddressName")
    class ExistsBySellerIdAndAddressTypeAndAddressNameTest {

        @Test
        @DisplayName("동일한 셀러, 타입, 주소명이 있으면 true를 반환합니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithExisting_ReturnsTrue() {
            Long sellerId = 1L;
            String addressName = "본사 창고";
            persist(SellerAddressJpaEntityFixtures.shippingEntity(sellerId, addressName, true));

            boolean exists =
                    repository()
                            .existsBySellerIdAndAddressTypeAndAddressName(
                                    sellerId,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING,
                                    addressName);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("주소명이 null이면 false를 반환합니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithNull_ReturnsFalse() {
            boolean exists =
                    repository()
                            .existsBySellerIdAndAddressTypeAndAddressName(
                                    1L,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING,
                                    null);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("주소명이 빈 문자열이면 false를 반환합니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithBlank_ReturnsFalse() {
            boolean exists =
                    repository()
                            .existsBySellerIdAndAddressTypeAndAddressName(
                                    1L,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING,
                                    "");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 주소는 중복 검사에서 제외됩니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithDeleted_ReturnsFalse() {
            Long sellerId = 1L;
            String addressName = "삭제된 주소";
            persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            boolean exists =
                    repository()
                            .existsBySellerIdAndAddressTypeAndAddressName(
                                    sellerId,
                                    SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING,
                                    addressName);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("search")
    class SearchTest {

        @Test
        @DisplayName("BooleanBuilder 조건으로 검색합니다")
        void search_WithConditions_ReturnsFilteredResults() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.defaultReturnEntity(sellerId));

            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(sellerId));
            conditions.and(
                    conditionBuilder.addressTypeEq(
                            SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING));

            List<SellerAddressJpaEntity> result = repository().search(conditions, 0, 10);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("페이징 파라미터를 적용합니다")
        void search_WithPagination_AppliesLimitAndOffset() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(sellerId, "보조 창고1"));
            persist(SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(sellerId, "보조 창고2"));

            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(sellerId));

            List<SellerAddressJpaEntity> result = repository().search(conditions, 0, 2);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 주소는 검색 결과에서 제외됩니다")
        void search_ExcludesDeleted() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(sellerId));

            List<SellerAddressJpaEntity> result = repository().search(conditions, 0, 10);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("count")
    class CountTest {

        @Test
        @DisplayName("조건에 맞는 주소 개수를 반환합니다")
        void count_WithConditions_ReturnsCount() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.defaultReturnEntity(sellerId));

            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(sellerId));

            long count = repository().count(conditions);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("삭제된 주소는 개수에서 제외됩니다")
        void count_ExcludesDeleted() {
            Long sellerId = 1L;
            persist(SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            persist(SellerAddressJpaEntityFixtures.deletedEntity(sellerId));

            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(sellerId));

            long count = repository().count(conditions);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("조건에 맞는 주소가 없으면 0을 반환합니다")
        void count_WithNoMatches_ReturnsZero() {
            BooleanBuilder conditions = new BooleanBuilder();
            SellerAddressConditionBuilder conditionBuilder = new SellerAddressConditionBuilder();
            conditions.and(conditionBuilder.sellerIdEq(999L));

            long count = repository().count(conditions);

            assertThat(count).isZero();
        }
    }
}
