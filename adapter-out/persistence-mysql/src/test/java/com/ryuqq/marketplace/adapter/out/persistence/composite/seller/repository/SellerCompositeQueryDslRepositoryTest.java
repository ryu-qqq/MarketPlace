package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerCompositeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerAdminCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerContractJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerCompositeQueryDslRepositoryTest - 셀러 Composite QueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 적용을 우선 검증합니다.
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
@DisplayName("SellerCompositeQueryDslRepository 통합 테스트")
class SellerCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerCompositeQueryDslRepository repository() {
        return new SellerCompositeQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerCompositeConditionBuilder());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // ========================================================================
    // 1. findBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerId 테스트")
    class FindBySellerIdTest {

        @Test
        @DisplayName("셀러 + BusinessInfo + Cs 조인 조회가 성공합니다")
        void findBySellerId_WithCompleteData_ReturnsCompositeDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerBusinessInfoJpaEntity businessInfo =
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId());
            entityManager.persist(businessInfo);

            SellerCsJpaEntity cs =
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId());
            entityManager.persist(cs);
            flushAndClear();

            // when
            Optional<SellerCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            SellerCompositeDto dto = result.get();
            assertThat(dto.sellerId()).isEqualTo(seller.getId());
            assertThat(dto.businessInfoId()).isEqualTo(businessInfo.getId());
            assertThat(dto.csId()).isEqualTo(cs.getId());
        }

        @Test
        @DisplayName("미삭제 셀러는 조회됩니다")
        void findBySellerId_WithNotDeleted_ReturnsEntity() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("삭제된 셀러는 조회되지 않습니다")
        void findBySellerId_WithDeleted_ReturnsEmpty() {
            // given
            SellerJpaEntity deleted = SellerJpaEntityFixtures.deletedEntity();
            entityManager.persist(deleted);
            flushAndClear();

            // when
            Optional<SellerCompositeDto> result = repository().findBySellerId(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("LEFT JOIN으로 BusinessInfo가 없어도 조회됩니다")
        void findBySellerId_WithoutBusinessInfo_ReturnsPartialDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().businessInfoId()).isNull();
        }

        @Test
        @DisplayName("LEFT JOIN으로 Cs가 없어도 조회됩니다")
        void findBySellerId_WithoutCs_ReturnsPartialDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().csId()).isNull();
        }
    }

    // ========================================================================
    // 2. findAdminCompositeById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAdminCompositeById 테스트")
    class FindAdminCompositeByIdTest {

        @Test
        @DisplayName("전체 정보 조인 조회가 성공합니다")
        void findAdminCompositeById_WithAllData_ReturnsFullCompositeDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerBusinessInfoJpaEntity businessInfo =
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId());
            entityManager.persist(businessInfo);

            SellerCsJpaEntity cs =
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId());
            entityManager.persist(cs);

            SellerContractJpaEntity contract =
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller.getId());
            entityManager.persist(contract);

            SellerSettlementJpaEntity settlement =
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller.getId());
            entityManager.persist(settlement);
            flushAndClear();

            // when
            Optional<SellerAdminCompositeDto> result =
                    repository().findAdminCompositeById(seller.getId());

            // then
            assertThat(result).isPresent();
            SellerAdminCompositeDto dto = result.get();
            assertThat(dto.sellerId()).isEqualTo(seller.getId());
            assertThat(dto.businessInfoId()).isEqualTo(businessInfo.getId());
            assertThat(dto.csId()).isEqualTo(cs.getId());
            assertThat(dto.contractId()).isEqualTo(contract.getId());
            assertThat(dto.settlementId()).isEqualTo(settlement.getId());
        }

        @Test
        @DisplayName("미삭제 셀러는 조회됩니다")
        void findAdminCompositeById_WithNotDeleted_ReturnsEntity() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerAdminCompositeDto> result =
                    repository().findAdminCompositeById(seller.getId());

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("삭제된 셀러는 조회되지 않습니다")
        void findAdminCompositeById_WithDeleted_ReturnsEmpty() {
            // given
            SellerJpaEntity deleted = SellerJpaEntityFixtures.deletedEntity();
            entityManager.persist(deleted);
            flushAndClear();

            // when
            Optional<SellerAdminCompositeDto> result =
                    repository().findAdminCompositeById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("LEFT JOIN으로 Contract가 없어도 조회됩니다")
        void findAdminCompositeById_WithoutContract_ReturnsPartialDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerAdminCompositeDto> result =
                    repository().findAdminCompositeById(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().contractId()).isNull();
        }

        @Test
        @DisplayName("LEFT JOIN으로 Settlement가 없어도 조회됩니다")
        void findAdminCompositeById_WithoutSettlement_ReturnsPartialDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerAdminCompositeDto> result =
                    repository().findAdminCompositeById(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().settlementId()).isNull();
        }
    }
}
