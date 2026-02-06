package com.ryuqq.marketplace.application.seller.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerCommandPort;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
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
@DisplayName("SellerCommandManager 단위 테스트")
class SellerCommandManagerTest {

    @InjectMocks private SellerCommandManager sut;

    @Mock private SellerCommandPort commandPort;

    @Nested
    @DisplayName("persist() - Seller 저장")
    class PersistTest {

        @Test
        @DisplayName("Seller를 저장하고 ID를 반환한다")
        void persist_ReturnsSellerId() {
            // given
            Seller seller = SellerFixtures.newSeller();
            Long expectedId = 1L;

            given(commandPort.persist(seller)).willReturn(expectedId);

            // when
            Long result = sut.persist(seller);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(seller);
        }
    }

    @Nested
    @DisplayName("persistAll() - Seller 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Seller 목록을 저장한다")
        void persistAll_SavesAllSellers() {
            // given
            List<Seller> sellers = List.of(SellerFixtures.newSeller(), SellerFixtures.newSeller());

            // when
            sut.persistAll(sellers);

            // then
            then(commandPort).should().persistAll(sellers);
        }
    }
}
