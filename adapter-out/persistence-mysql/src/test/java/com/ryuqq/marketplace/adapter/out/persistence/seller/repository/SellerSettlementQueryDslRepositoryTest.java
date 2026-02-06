package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerSettlementConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
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

/** SellerSettlementQueryDslRepositoryTest - 셀러 정산 QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerSettlementQueryDslRepository 통합 테스트")
class SellerSettlementQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerSettlementQueryDslRepository repository() {
        return new SellerSettlementQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerSettlementConditionBuilder());
    }

    private SellerSettlementJpaEntity persist(SellerSettlementJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SellerSettlementJpaEntity newDeletedEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                SellerSettlementJpaEntityFixtures.DEFAULT_SELLER_ID,
                SellerSettlementJpaEntityFixtures.DEFAULT_BANK_CODE,
                SellerSettlementJpaEntityFixtures.DEFAULT_BANK_NAME,
                "DEL-" + SellerSettlementJpaEntityFixtures.DEFAULT_ACCOUNT_NUMBER,
                SellerSettlementJpaEntityFixtures.DEFAULT_ACCOUNT_HOLDER_NAME,
                SellerSettlementJpaEntityFixtures.DEFAULT_SETTLEMENT_CYCLE,
                SellerSettlementJpaEntityFixtures.DEFAULT_SETTLEMENT_DAY,
                true,
                now,
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
            SellerSettlementJpaEntity saved =
                    persist(SellerSettlementJpaEntityFixtures.newEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerSettlementJpaEntity deleted = persist(newDeletedEntity());

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
            SellerSettlementJpaEntity saved =
                    persist(SellerSettlementJpaEntityFixtures.newEntity());

            // when
            var result = repository().findBySellerId(saved.getSellerId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(saved.getSellerId());
        }

        @Test
        @DisplayName("삭제된 Entity는 sellerId로 조회되지 않습니다")
        void findBySellerId_WithDeleted_ReturnsEmpty() {
            // given
            SellerSettlementJpaEntity deleted = persist(newDeletedEntity());

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
            SellerSettlementJpaEntity saved =
                    persist(SellerSettlementJpaEntityFixtures.newEntity());

            // when
            boolean result = repository().existsBySellerId(saved.getSellerId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity는 sellerId로 false를 반환합니다")
        void existsBySellerId_WithDeleted_ReturnsFalse() {
            // given
            SellerSettlementJpaEntity deleted = persist(newDeletedEntity());

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
}
