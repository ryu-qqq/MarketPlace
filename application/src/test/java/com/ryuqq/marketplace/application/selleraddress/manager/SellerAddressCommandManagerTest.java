package com.ryuqq.marketplace.application.selleraddress.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleraddress.port.out.command.SellerAddressCommandPort;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
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
@DisplayName("SellerAddressCommandManager 단위 테스트")
class SellerAddressCommandManagerTest {

    @InjectMocks private SellerAddressCommandManager sut;

    @Mock private SellerAddressCommandPort commandPort;

    @Nested
    @DisplayName("persist() - SellerAddress 저장")
    class PersistTest {

        @Test
        @DisplayName("SellerAddress를 저장하고 ID를 반환한다")
        void persist_ReturnsAddressId() {
            // given
            Long sellerId = 1L;
            SellerAddress address = SellerAddressFixtures.newShippingAddress(sellerId);
            Long expectedId = 1L;

            given(commandPort.persist(address)).willReturn(expectedId);

            // when
            Long result = sut.persist(address);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(address);
        }
    }

    @Nested
    @DisplayName("persistAll() - SellerAddress 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("SellerAddress 목록을 저장한다")
        void persistAll_SavesAllAddresses() {
            // given
            Long sellerId = 1L;
            List<SellerAddress> addresses =
                    List.of(
                            SellerAddressFixtures.newShippingAddress(sellerId),
                            SellerAddressFixtures.newReturnAddress(sellerId));

            // when
            sut.persistAll(addresses);

            // then
            then(commandPort).should().persistAll(addresses);
        }
    }
}
