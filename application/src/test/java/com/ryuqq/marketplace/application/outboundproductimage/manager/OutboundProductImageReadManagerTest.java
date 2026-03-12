package com.ryuqq.marketplace.application.outboundproductimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.query.OutboundProductImageQueryPort;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImages;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboundProductImageReadManager 단위 테스트")
class OutboundProductImageReadManagerTest {

    @InjectMocks private OutboundProductImageReadManager sut;

    @Mock private OutboundProductImageQueryPort queryPort;

    @Nested
    @DisplayName("findByOutboundProductId() - 아웃바운드 상품 ID로 이미지 조회")
    class FindByOutboundProductIdTest {

        @Test
        @DisplayName("이미지가 존재하면 OutboundProductImages로 감싸 반환한다")
        void findByOutboundProductId_WithImages_ReturnsOutboundProductImages() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            List<OutboundProductImage> images = List.of(
                    OutboundProductImageFixtures.activeThumbnailImage(),
                    OutboundProductImageFixtures.activeDetailImage(2L, 1));

            given(queryPort.findActiveByOutboundProductId(outboundProductId)).willReturn(images);

            // when
            OutboundProductImages result = sut.findByOutboundProductId(outboundProductId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toList()).hasSize(2);
            then(queryPort).should().findActiveByOutboundProductId(outboundProductId);
        }

        @Test
        @DisplayName("이미지가 없으면 빈 OutboundProductImages를 반환한다")
        void findByOutboundProductId_WithNoImages_ReturnsEmptyOutboundProductImages() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;

            given(queryPort.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of());

            // when
            OutboundProductImages result = sut.findByOutboundProductId(outboundProductId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("queryPort.findActiveByOutboundProductId를 정확히 1회 호출한다")
        void findByOutboundProductId_CallsQueryPortExactlyOnce() {
            // given
            Long outboundProductId = 999L;

            given(queryPort.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of());

            // when
            sut.findByOutboundProductId(outboundProductId);

            // then
            then(queryPort).should().findActiveByOutboundProductId(outboundProductId);
            then(queryPort).shouldHaveNoMoreInteractions();
        }
    }
}
