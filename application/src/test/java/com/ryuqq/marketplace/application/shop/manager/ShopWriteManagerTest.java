package com.ryuqq.marketplace.application.shop.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.port.out.command.ShopCommandPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
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
@DisplayName("ShopWriteManager 단위 테스트")
class ShopWriteManagerTest {

    @InjectMocks private ShopWriteManager sut;

    @Mock private ShopCommandPort commandPort;

    @Nested
    @DisplayName("persist() - Shop 저장")
    class PersistTest {

        @Test
        @DisplayName("Shop을 저장하고 ID를 반환한다")
        void persist_SavesShop_ReturnsId() {
            // given
            Shop shop = createShop(1L);
            Long expectedId = 1L;

            given(commandPort.persist(shop)).willReturn(expectedId);

            // when
            Long result = sut.persist(shop);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(shop);
        }

        @Test
        @DisplayName("신규 Shop을 저장할 수 있다")
        void persist_NewShop_SavesSuccessfully() {
            // given
            Instant now = Instant.now();
            Shop newShop = Shop.forNew(1L, "신규 외부몰", "new-account-123", now);
            Long expectedId = 100L;

            given(commandPort.persist(newShop)).willReturn(expectedId);

            // when
            Long result = sut.persist(newShop);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("수정된 Shop을 저장할 수 있다")
        void persist_UpdatedShop_SavesSuccessfully() {
            // given
            Shop shop = createShop(1L);
            Long expectedId = 1L;

            given(commandPort.persist(shop)).willReturn(expectedId);

            // when
            Long result = sut.persist(shop);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        private Shop createShop(Long shopId) {
            Instant now = Instant.now();
            return Shop.reconstitute(
                    ShopId.of(shopId),
                    1L,
                    "테스트 외부몰",
                    "test-account-" + shopId,
                    ShopStatus.ACTIVE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    now,
                    now);
        }
    }
}
