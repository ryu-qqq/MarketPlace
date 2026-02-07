package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerPolicyCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
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
 * SellerPolicyCompositeQueryDslRepositoryTest - 셀러 정책 Composite QueryDslRepository 통합 테스트.
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
@DisplayName("SellerPolicyCompositeQueryDslRepository 통합 테스트")
class SellerPolicyCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerPolicyCompositeQueryDslRepository repository() {
        return new SellerPolicyCompositeQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerPolicyConditionBuilder());
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
        @DisplayName("배송 정책과 환불 정책을 조회합니다")
        void findBySellerId_WithPolicies_ReturnsCompositeDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity shippingPolicy =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(shippingPolicy);

            RefundPolicyJpaEntity refundPolicy =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(refundPolicy);
            flushAndClear();

            // when
            Optional<SellerPolicyCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            SellerPolicyCompositeDto dto = result.get();
            assertThat(dto.sellerId()).isEqualTo(seller.getId());
            assertThat(dto.shippingPolicies()).hasSize(1);
            assertThat(dto.refundPolicies()).hasSize(1);
        }

        @Test
        @DisplayName("정책이 없어도 빈 리스트로 조회됩니다")
        void findBySellerId_WithoutPolicies_ReturnsEmptyLists() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            Optional<SellerPolicyCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().shippingPolicies()).isEmpty();
            assertThat(result.get().refundPolicies()).isEmpty();
        }

        @Test
        @DisplayName("삭제된 배송 정책은 조회되지 않습니다")
        void findBySellerId_WithDeletedShippingPolicy_FiltersDeleted() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity active =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(active);

            ShippingPolicyJpaEntity deleted =
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted);
            flushAndClear();

            // when
            Optional<SellerPolicyCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().shippingPolicies()).hasSize(1);
            assertThat(result.get().shippingPolicies().get(0).id()).isEqualTo(active.getId());
        }

        @Test
        @DisplayName("삭제된 환불 정책은 조회되지 않습니다")
        void findBySellerId_WithDeletedRefundPolicy_FiltersDeleted() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity active =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(active);

            RefundPolicyJpaEntity deleted =
                    RefundPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted);
            flushAndClear();

            // when
            Optional<SellerPolicyCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().refundPolicies()).hasSize(1);
            assertThat(result.get().refundPolicies().get(0).id()).isEqualTo(active.getId());
        }

        @Test
        @DisplayName("여러 정책을 모두 조회합니다")
        void findBySellerId_WithMultiplePolicies_ReturnsAll() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity shipping1 =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(shipping1);

            ShippingPolicyJpaEntity shipping2 =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(shipping2);

            RefundPolicyJpaEntity refund1 =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(refund1);

            RefundPolicyJpaEntity refund2 =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(refund2);
            flushAndClear();

            // when
            Optional<SellerPolicyCompositeDto> result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().shippingPolicies()).hasSize(2);
            assertThat(result.get().refundPolicies()).hasSize(2);
        }
    }
}
