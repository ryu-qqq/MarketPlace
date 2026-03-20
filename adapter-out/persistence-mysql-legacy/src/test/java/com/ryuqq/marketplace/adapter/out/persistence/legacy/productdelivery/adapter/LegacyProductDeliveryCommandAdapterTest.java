package com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.repository.LegacyProductDeliveryJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ReturnMethod;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ShipmentCompanyCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductDeliveryCommandAdapterTest - 레거시 상품 배송정보 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductDeliveryCommandAdapter 단위 테스트")
class LegacyProductDeliveryCommandAdapterTest {

    @Mock private LegacyProductDeliveryJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductDeliveryCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("배송정보를 저장합니다")
        void persist_WithValidDelivery_SavesSuccessfully() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductDelivery delivery =
                    new LegacyProductDelivery(
                            "전국",
                            3000L,
                            3,
                            ReturnMethod.RETURN_CONSUMER,
                            ShipmentCompanyCode.SHIP04,
                            5000,
                            "서울시 강남구");

            LegacyProductDeliveryEntity entity =
                    LegacyProductDeliveryEntity.create(
                            1L,
                            "전국",
                            3000L,
                            3,
                            ReturnMethod.RETURN_CONSUMER.name(),
                            ShipmentCompanyCode.SHIP04.name(),
                            5000,
                            "서울시 강남구");

            given(mapper.toEntity(productGroupId, delivery)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(productGroupId, delivery);

            // then
            then(mapper).should().toEntity(productGroupId, delivery);
            then(repository).should().save(entity);
        }
    }
}
