package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAddressJpaEntityMapperTest - 셀러 주소 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAddressJpaEntityMapper 단위 테스트")
class SellerAddressJpaEntityMapperTest {

    private SellerAddressJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerAddressJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("SHIPPING 주소 Domain을 Entity로 변환합니다")
        void toEntity_WithShippingAddress_ConvertsCorrectly() {
            // given
            SellerAddress domain = SellerAddressFixtures.defaultShippingAddress(1L, 1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getAddressType()).isEqualTo(domain.addressType().name());
            assertThat(entity.getAddressName()).isEqualTo(domain.addressNameValue());
            assertThat(entity.getZipcode()).isEqualTo(domain.addressZipCode());
            assertThat(entity.getAddress()).isEqualTo(domain.addressRoad());
            assertThat(entity.getAddressDetail()).isEqualTo(domain.addressDetail());
            assertThat(entity.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("RETURN 주소 Domain을 Entity로 변환합니다")
        void toEntity_WithReturnAddress_ConvertsCorrectly() {
            // given
            SellerAddress domain = SellerAddressFixtures.defaultReturnAddress(1L, 1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getAddressType()).isEqualTo("RETURN");
            assertThat(entity.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("비기본 주소 Domain을 Entity로 변환합니다")
        void toEntity_WithNonDefaultAddress_ConvertsCorrectly() {
            // given
            SellerAddress domain = SellerAddressFixtures.nonDefaultShippingAddress(1L, 1L, "지점 창고");

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isDefaultAddress()).isFalse();
        }

        @Test
        @DisplayName("삭제된 주소 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedAddress_ConvertsCorrectly() {
            // given
            SellerAddress domain = SellerAddressFixtures.deletedAddress(1L, 1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로운 주소 Domain을 Entity로 변환합니다")
        void toEntity_WithNewAddress_ConvertsCorrectly() {
            // given
            SellerAddress domain = SellerAddressFixtures.newShippingAddress(1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSellerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("상세주소가 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutAddressDetail_ConvertsCorrectly() {
            // given
            SellerAddress domain =
                    SellerAddressFixtures.addressWithoutDetail(1L, 1L, AddressType.SHIPPING);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getAddressDetail()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("SHIPPING 주소 Entity를 Domain으로 변환합니다")
        void toDomain_WithShippingEntity_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.addressType().name()).isEqualTo(entity.getAddressType());
            assertThat(domain.addressNameValue()).isEqualTo(entity.getAddressName());
            assertThat(domain.addressZipCode()).isEqualTo(entity.getZipcode());
            assertThat(domain.addressRoad()).isEqualTo(entity.getAddress());
            assertThat(domain.addressDetail()).isEqualTo(entity.getAddressDetail());
            assertThat(domain.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("RETURN 주소 Entity를 Domain으로 변환합니다")
        void toDomain_WithReturnEntity_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.returnEntityWithId(1L, 1L, "반품 센터", true);

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.addressType()).isEqualTo(AddressType.RETURN);
            assertThat(domain.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("비기본 주소 Entity를 Domain으로 변환합니다")
        void toDomain_WithNonDefaultEntity_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.nonDefaultShippingEntityWithId(1L, 1L, "지점 창고");

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDefaultAddress()).isFalse();
        }

        @Test
        @DisplayName("삭제된 주소 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.deletedEntityWithId(1L, 1L);

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("상세주소가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutAddressDetail_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.entityWithoutAddressDetailWithId(
                            1L, 1L, "SHIPPING", "상세주소 없는 주소", false);

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.addressDetail()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.shippingEntity(1L, "본사 창고", true);

            // when
            SellerAddress domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.sellerIdValue()).isEqualTo(1L);
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
            SellerAddress original = SellerAddressFixtures.defaultShippingAddress(1L, 1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(original);
            SellerAddress converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.addressType()).isEqualTo(original.addressType());
            assertThat(converted.addressNameValue()).isEqualTo(original.addressNameValue());
            assertThat(converted.addressZipCode()).isEqualTo(original.addressZipCode());
            assertThat(converted.addressRoad()).isEqualTo(original.addressRoad());
            assertThat(converted.addressDetail()).isEqualTo(original.addressDetail());
            assertThat(converted.isDefaultAddress()).isEqualTo(original.isDefaultAddress());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerAddressJpaEntity original =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);

            // when
            SellerAddress domain = mapper.toDomain(original);
            SellerAddressJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getAddressType()).isEqualTo(original.getAddressType());
            assertThat(converted.getAddressName()).isEqualTo(original.getAddressName());
            assertThat(converted.getZipcode()).isEqualTo(original.getZipcode());
            assertThat(converted.getAddress()).isEqualTo(original.getAddress());
            assertThat(converted.getAddressDetail()).isEqualTo(original.getAddressDetail());
            assertThat(converted.isDefaultAddress()).isEqualTo(original.isDefaultAddress());
        }

        @Test
        @DisplayName("삭제된 주소 양방향 변환 시 데이터가 보존됩니다")
        void roundTrip_DeletedAddress_PreservesData() {
            // given
            SellerAddress original = SellerAddressFixtures.deletedAddress(1L, 1L);

            // when
            SellerAddressJpaEntity entity = mapper.toEntity(original);
            SellerAddress converted = mapper.toDomain(entity);

            // then
            assertThat(converted.isDeleted()).isTrue();
            assertThat(converted.deletedAt()).isNotNull();
        }
    }
}
