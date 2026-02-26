package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerBusinessInfoConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** SellerBusinessInfoQueryDslRepositoryTest - 셀러 사업자정보 QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerBusinessInfoQueryDslRepository 통합 테스트")
class SellerBusinessInfoQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerBusinessInfoQueryDslRepository repository() {
        return new SellerBusinessInfoQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerBusinessInfoConditionBuilder());
    }

    private SellerBusinessInfoJpaEntity persist(SellerBusinessInfoJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SellerBusinessInfoJpaEntity newDeletedEntity() {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_SELLER_ID,
                "DEL-" + SellerBusinessInfoJpaEntityFixtures.DEFAULT_REGISTRATION_NUMBER,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_COMPANY_NAME,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_REPRESENTATIVE,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_SALE_REPORT_NUMBER,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_BUSINESS_ZIPCODE,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_BUSINESS_ADDRESS,
                SellerBusinessInfoJpaEntityFixtures.DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                now);
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            SellerBusinessInfoJpaEntity saved =
                    persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(1L));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerBusinessInfoJpaEntity deleted = persist(newDeletedEntity());

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerId")
    class FindBySellerIdTest {

        @Test
        @DisplayName("미삭제 Entity는 sellerId로 조회됩니다")
        void findBySellerId_WithNotDeleted_ReturnsEntity() {
            // given
            Long sellerId = 10L;
            SellerBusinessInfoJpaEntity saved =
                    persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            // when
            var result = repository().findBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("삭제된 Entity는 sellerId로 조회되지 않습니다")
        void findBySellerId_WithDeleted_ReturnsEmpty() {
            // given
            SellerBusinessInfoJpaEntity deleted = persist(newDeletedEntity());

            // when
            var result = repository().findBySellerId(deleted.getSellerId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBySellerId")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("미삭제 Entity는 sellerId로 존재 확인됩니다")
        void existsBySellerId_WithNotDeleted_ReturnsTrue() {
            // given
            Long sellerId = 20L;
            persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            // when
            boolean result = repository().existsBySellerId(sellerId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity는 sellerId로 false를 반환합니다")
        void existsBySellerId_WithDeleted_ReturnsFalse() {
            // given
            SellerBusinessInfoJpaEntity deleted = persist(newDeletedEntity());

            // when
            boolean result = repository().existsBySellerId(deleted.getSellerId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 sellerId는 false를 반환합니다")
        void existsBySellerId_WithNonExistent_ReturnsFalse() {
            // when
            boolean result = repository().existsBySellerId(999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByRegistrationNumber")
    class ExistsByRegistrationNumberTest {

        @Test
        @DisplayName("미삭제 Entity의 사업자등록번호는 존재 확인됩니다")
        void existsByRegistrationNumber_WithNotDeleted_ReturnsTrue() {
            // given
            SellerBusinessInfoJpaEntity saved =
                    persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(30L));

            // when
            boolean result = repository().existsByRegistrationNumber(saved.getRegistrationNumber());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity의 사업자등록번호는 false를 반환합니다")
        void existsByRegistrationNumber_WithDeleted_ReturnsFalse() {
            // given
            SellerBusinessInfoJpaEntity deleted = persist(newDeletedEntity());

            // when
            boolean result =
                    repository().existsByRegistrationNumber(deleted.getRegistrationNumber());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 사업자등록번호는 false를 반환합니다")
        void existsByRegistrationNumber_WithNonExistent_ReturnsFalse() {
            // when
            boolean result = repository().existsByRegistrationNumber("000-00-00000");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByRegistrationNumberExcluding")
    class ExistsByRegistrationNumberExcludingTest {

        @Test
        @DisplayName("제외 sellerId가 아닌 사업자등록번호가 존재하면 true를 반환합니다")
        void existsByRegistrationNumberExcluding_WithDifferentSellerId_ReturnsTrue() {
            // given
            Long sellerId = 40L;
            SellerBusinessInfoJpaEntity saved =
                    persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            // when
            boolean result =
                    repository()
                            .existsByRegistrationNumberExcluding(
                                    saved.getRegistrationNumber(), 999L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("제외 sellerId의 사업자등록번호는 false를 반환합니다")
        void existsByRegistrationNumberExcluding_WithExcludedSellerId_ReturnsFalse() {
            // given
            Long sellerId = 50L;
            persist(SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            // when
            boolean result =
                    repository()
                            .existsByRegistrationNumberExcluding(
                                    SellerBusinessInfoJpaEntityFixtures.DEFAULT_REGISTRATION_NUMBER,
                                    sellerId);

            // then
            assertThat(result).isFalse();
        }
    }
}
