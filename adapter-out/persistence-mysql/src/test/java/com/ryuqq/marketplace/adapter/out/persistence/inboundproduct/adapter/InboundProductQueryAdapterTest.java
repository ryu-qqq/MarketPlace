package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.InboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper.InboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository.InboundProductJpaRepository;
import com.ryuqq.marketplace.domain.inboundproduct.InboundProductFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InboundProductQueryAdapterTest - InboundProduct Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 JpaRepository 사용 (QueryDslRepository 없음).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundProductQueryAdapter 단위 테스트")
class InboundProductQueryAdapterTest {

    @Mock private InboundProductJpaRepository repository;

    @Mock private InboundProductJpaEntityMapper mapper;

    @InjectMocks private InboundProductQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByInboundSourceIdAndProductCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndProductCode 메서드 테스트")
    class FindByInboundSourceIdAndProductCodeTest {

        @Test
        @DisplayName("존재하는 소스ID와 상품코드로 조회 시 Domain을 반환합니다")
        void findByInboundSourceIdAndProductCode_WithExistingParams_ReturnsDomain() {
            // given
            Long inboundSourceId = InboundProductFixtures.DEFAULT_INBOUND_SOURCE_ID;
            String externalProductCode = InboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE;
            InboundProductJpaEntity entity =
                    InboundProductJpaEntityFixtures.receivedEntity(
                            inboundSourceId, externalProductCode);
            InboundProduct domain = InboundProductFixtures.receivedProduct();

            given(
                            repository.findByInboundSourceIdAndExternalProductCode(
                                    inboundSourceId, externalProductCode))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundProduct> result =
                    queryAdapter.findByInboundSourceIdAndProductCode(
                            inboundSourceId, externalProductCode);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID와 상품코드로 조회 시 빈 Optional을 반환합니다")
        void findByInboundSourceIdAndProductCode_WithNonExistingParams_ReturnsEmpty() {
            // given
            Long inboundSourceId = 9999L;
            String externalProductCode = "NON-EXIST-CODE";

            given(
                            repository.findByInboundSourceIdAndExternalProductCode(
                                    inboundSourceId, externalProductCode))
                    .willReturn(Optional.empty());

            // when
            Optional<InboundProduct> result =
                    queryAdapter.findByInboundSourceIdAndProductCode(
                            inboundSourceId, externalProductCode);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Repository가 정확히 한 번 호출됩니다")
        void findByInboundSourceIdAndProductCode_CallsRepositoryOnce() {
            // given
            Long inboundSourceId = InboundProductFixtures.DEFAULT_INBOUND_SOURCE_ID;
            String externalProductCode = InboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE;

            given(
                            repository.findByInboundSourceIdAndExternalProductCode(
                                    inboundSourceId, externalProductCode))
                    .willReturn(Optional.empty());

            // when
            queryAdapter.findByInboundSourceIdAndProductCode(inboundSourceId, externalProductCode);

            // then
            then(repository)
                    .should()
                    .findByInboundSourceIdAndExternalProductCode(
                            inboundSourceId, externalProductCode);
        }

        @Test
        @DisplayName("Entity가 존재할 때 Mapper가 호출됩니다")
        void findByInboundSourceIdAndProductCode_WhenEntityExists_CallsMapper() {
            // given
            Long inboundSourceId = InboundProductFixtures.DEFAULT_INBOUND_SOURCE_ID;
            String externalProductCode = InboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE;
            InboundProductJpaEntity entity =
                    InboundProductJpaEntityFixtures.receivedEntity(
                            inboundSourceId, externalProductCode);
            InboundProduct domain = InboundProductFixtures.receivedProduct();

            given(
                            repository.findByInboundSourceIdAndExternalProductCode(
                                    inboundSourceId, externalProductCode))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.findByInboundSourceIdAndProductCode(inboundSourceId, externalProductCode);

            // then
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("Entity가 없을 때 Mapper는 호출되지 않습니다")
        void findByInboundSourceIdAndProductCode_WhenEntityNotExists_DoesNotCallMapper() {
            // given
            Long inboundSourceId = 9999L;
            String externalProductCode = "NOT-FOUND";

            given(
                            repository.findByInboundSourceIdAndExternalProductCode(
                                    inboundSourceId, externalProductCode))
                    .willReturn(Optional.empty());

            // when
            queryAdapter.findByInboundSourceIdAndProductCode(inboundSourceId, externalProductCode);

            // then
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
