package com.ryuqq.marketplace.application.productnotice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeCommandPort;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
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
@DisplayName("ProductNoticeCommandManager 단위 테스트")
class ProductNoticeCommandManagerTest {

    @InjectMocks private ProductNoticeCommandManager sut;

    @Mock private ProductNoticeCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 고시정보 저장")
    class PersistTest {

        @Test
        @DisplayName("고시정보를 저장하고 생성된 ID를 반환한다")
        void persist_ValidProductNotice_ReturnsNoticeId() {
            // given
            ProductNotice productNotice = ProductNoticeFixtures.newProductNotice();
            Long expectedId = 1L;

            given(commandPort.persist(productNotice)).willReturn(expectedId);

            // when
            Long result = sut.persist(productNotice);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(productNotice);
        }
    }
}
