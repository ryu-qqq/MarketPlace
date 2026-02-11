package com.ryuqq.marketplace.application.saleschannelbrand.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelbrand.port.out.command.SalesChannelBrandCommandPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
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
@DisplayName("SalesChannelBrandCommandManager 단위 테스트")
class SalesChannelBrandCommandManagerTest {

    @InjectMocks private SalesChannelBrandCommandManager sut;

    @Mock private SalesChannelBrandCommandPort commandPort;

    @Nested
    @DisplayName("persist() - SalesChannelBrand 저장")
    class PersistTest {

        @Test
        @DisplayName("SalesChannelBrand를 저장하고 ID를 반환한다")
        void persist_ReturnsBrandId() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.newSalesChannelBrand();
            Long expectedId = 1L;

            given(commandPort.persist(brand)).willReturn(expectedId);

            // when
            Long result = sut.persist(brand);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(brand);
        }

        @Test
        @DisplayName("다른 판매채널의 브랜드를 저장한다")
        void persist_DifferentSalesChannel_ReturnsBrandId() {
            // given
            SalesChannelBrand brand =
                    SalesChannelBrandFixtures.newSalesChannelBrand(2L, "BRAND-002", "다른 브랜드");
            Long expectedId = 2L;

            given(commandPort.persist(brand)).willReturn(expectedId);

            // when
            Long result = sut.persist(brand);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(brand);
        }
    }
}
