package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeSellerTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto.LegacySellerCompositeQueryDto;
import jakarta.persistence.EntityManager;
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
 * LegacySellerCompositeQueryDslRepository 통합 테스트.
 *
 * <p>seller + seller_business_info 2테이블 JOIN 쿼리를 검증합니다.
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
@DisplayName("LegacySellerCompositeQueryDslRepository 통합 테스트")
class LegacySellerCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacySellerCompositeQueryDslRepository repository;
    private LegacyCompositeSellerTestHelper helper;

    @BeforeEach
    void setUp() {
        repository =
                new LegacySellerCompositeQueryDslRepository(new JPAQueryFactory(entityManager));
        helper = new LegacyCompositeSellerTestHelper(entityManager);
    }

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("셀러 + 사업자 정보 JOIN 결과를 반환합니다")
        void findById_WithFullData_ReturnsCompositeDto() {
            // given
            long sellerId = helper.setupFullSellerData();

            // when
            Optional<LegacySellerCompositeQueryDto> result = repository.findById(sellerId);

            // then
            assertThat(result).isPresent();
            LegacySellerCompositeQueryDto dto = result.get();
            assertThat(dto.sellerId()).isEqualTo(sellerId);
            assertThat(dto.sellerName()).isEqualTo("테스트 셀러");
            assertThat(dto.sellerLogoUrl()).isEqualTo("https://cdn.example.com/logo.png");
            assertThat(dto.sellerDescription()).isEqualTo("셀러 설명");
            assertThat(dto.commissionRate()).isEqualTo(15.5);
            // business info
            assertThat(dto.registrationNumber()).isEqualTo("123-45-67890");
            assertThat(dto.companyName()).isEqualTo("테스트 주식회사");
            assertThat(dto.representative()).isEqualTo("홍길동");
            assertThat(dto.bankName()).isEqualTo("국민은행");
            assertThat(dto.csEmail()).isEqualTo("cs@test.com");
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<LegacySellerCompositeQueryDto> result = repository.findById(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("사업자 정보가 없는 셀러는 INNER JOIN으로 조회되지 않습니다")
        void findById_WithNoBusinessInfo_ReturnsEmpty() {
            // given - 셀러만 있고 사업자 정보 없음
            helper.insertSeller(20L, "사업자없는 셀러");
            helper.flushAndClear();

            // when
            Optional<LegacySellerCompositeQueryDto> result = repository.findById(20L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 셀러 중 특정 셀러만 정확히 조회됩니다")
        void findById_WithMultipleSellers_ReturnsExactMatch() {
            // given
            helper.insertSeller(10L, "셀러A", null, null, 10.0);
            helper.insertBusinessInfo(10L);
            helper.insertSeller(11L, "셀러B", null, null, 20.0);
            helper.insertBusinessInfo(
                    11L,
                    "999-99-99999",
                    "B회사",
                    "이순신",
                    "2025-002",
                    "12345",
                    "부산시",
                    "3층",
                    "신한은행",
                    "9999999",
                    "이순신",
                    "051-111-2222",
                    "010-9999-8888",
                    "b@test.com");
            helper.flushAndClear();

            // when
            Optional<LegacySellerCompositeQueryDto> result = repository.findById(11L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().sellerName()).isEqualTo("셀러B");
            assertThat(result.get().companyName()).isEqualTo("B회사");
            assertThat(result.get().commissionRate()).isEqualTo(20.0);
        }
    }
}
