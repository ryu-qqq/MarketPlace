package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerBusinessInfoJpaEntityMapperTest - 셀러 사업자 정보 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerBusinessInfoJpaEntityMapper 단위 테스트")
class SellerBusinessInfoJpaEntityMapperTest {

    private SellerBusinessInfoJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerBusinessInfoJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveBusinessInfo_ConvertsCorrectly() {
            // given
            SellerBusinessInfo domain = SellerFixtures.activeSellerBusinessInfo();

            // when
            SellerBusinessInfoJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getRegistrationNumber()).isEqualTo(domain.registrationNumberValue());
            assertThat(entity.getCompanyName()).isEqualTo(domain.companyNameValue());
            assertThat(entity.getRepresentative()).isEqualTo(domain.representativeValue());
            assertThat(entity.getSaleReportNumber()).isEqualTo(domain.saleReportNumberValue());
            assertThat(entity.getBusinessZipcode()).isEqualTo(domain.businessAddressZipCode());
            assertThat(entity.getBusinessAddress()).isEqualTo(domain.businessAddressRoad());
            assertThat(entity.getBusinessAddressDetail()).isEqualTo(domain.businessAddressDetail());
        }

        @Test
        @DisplayName("삭제된 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedBusinessInfo_ConvertsCorrectly() {
            // given
            SellerBusinessInfo domain = SellerFixtures.deletedSellerBusinessInfo();

            // when
            SellerBusinessInfoJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewBusinessInfo_ConvertsCorrectly() {
            // given
            SellerBusinessInfo domain = SellerFixtures.newSellerBusinessInfo();

            // when
            SellerBusinessInfoJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getRegistrationNumber()).isEqualTo(domain.registrationNumberValue());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.activeEntity();

            // when
            SellerBusinessInfo domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.registrationNumberValue()).isEqualTo(entity.getRegistrationNumber());
            assertThat(domain.companyNameValue()).isEqualTo(entity.getCompanyName());
            assertThat(domain.representativeValue()).isEqualTo(entity.getRepresentative());
            assertThat(domain.saleReportNumberValue()).isEqualTo(entity.getSaleReportNumber());
            assertThat(domain.businessAddressZipCode()).isEqualTo(entity.getBusinessZipcode());
            assertThat(domain.businessAddressRoad()).isEqualTo(entity.getBusinessAddress());
            assertThat(domain.businessAddressDetail()).isEqualTo(entity.getBusinessAddressDetail());
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            SellerBusinessInfoJpaEntity entity =
                    SellerBusinessInfoJpaEntityFixtures.deletedEntity();

            // when
            SellerBusinessInfo domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("통신판매업신고번호가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutSaleReportNumber_ConvertsCorrectly() {
            // given
            SellerBusinessInfoJpaEntity entity =
                    SellerBusinessInfoJpaEntityFixtures.entityWithoutSaleReportNumber();

            // when
            SellerBusinessInfo domain = mapper.toDomain(entity);

            // then
            assertThat(domain.saleReportNumberValue()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.newEntity();

            // when
            SellerBusinessInfo domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.registrationNumberValue()).isEqualTo(entity.getRegistrationNumber());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            SellerBusinessInfo original = SellerFixtures.activeSellerBusinessInfo();

            // when
            SellerBusinessInfoJpaEntity entity = mapper.toEntity(original);
            SellerBusinessInfo converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.registrationNumberValue())
                    .isEqualTo(original.registrationNumberValue());
            assertThat(converted.companyNameValue()).isEqualTo(original.companyNameValue());
            assertThat(converted.representativeValue()).isEqualTo(original.representativeValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerBusinessInfoJpaEntity original =
                    SellerBusinessInfoJpaEntityFixtures.activeEntity();

            // when
            SellerBusinessInfo domain = mapper.toDomain(original);
            SellerBusinessInfoJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getRegistrationNumber())
                    .isEqualTo(original.getRegistrationNumber());
            assertThat(converted.getCompanyName()).isEqualTo(original.getCompanyName());
            assertThat(converted.getRepresentative()).isEqualTo(original.getRepresentative());
        }
    }
}
