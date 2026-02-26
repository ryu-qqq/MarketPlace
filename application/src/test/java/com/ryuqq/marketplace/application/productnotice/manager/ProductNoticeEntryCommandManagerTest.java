package com.ryuqq.marketplace.application.productnotice.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeEntryCommandPort;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
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
@DisplayName("ProductNoticeEntryCommandManager 단위 테스트")
class ProductNoticeEntryCommandManagerTest {

    @InjectMocks private ProductNoticeEntryCommandManager sut;

    @Mock private ProductNoticeEntryCommandPort commandPort;

    @Nested
    @DisplayName("persistAll() - 고시정보 항목 다건 저장")
    class PersistAllTest {

        @Test
        @DisplayName("고시정보 항목 목록을 모두 저장한다")
        void persistAll_MultipleEntries_PersistsEachEntry() {
            // given
            List<ProductNoticeEntry> entries = ProductNoticeFixtures.defaultEntries();

            // when
            sut.persistAll(entries);

            // then
            entries.forEach(entry -> then(commandPort).should().persist(entry));
        }

        @Test
        @DisplayName("빈 항목 목록은 아무것도 저장하지 않는다")
        void persistAll_EmptyList_DoesNothing() {
            // given
            List<ProductNoticeEntry> emptyEntries = List.of();

            // when
            sut.persistAll(emptyEntries);

            // then
            then(commandPort).shouldHaveNoInteractions();
        }
    }
}
