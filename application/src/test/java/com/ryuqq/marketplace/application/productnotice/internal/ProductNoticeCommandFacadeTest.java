package com.ryuqq.marketplace.application.productnotice.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeCommandManager;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeEntryCommandManager;
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
@DisplayName("ProductNoticeCommandFacade 단위 테스트")
class ProductNoticeCommandFacadeTest {

    @InjectMocks private ProductNoticeCommandFacade sut;

    @Mock private ProductNoticeCommandManager noticeCommandManager;
    @Mock private ProductNoticeEntryCommandManager entryCommandManager;

    @Nested
    @DisplayName("persist() - Notice + Entry 저장")
    class PersistTest {

        @Test
        @DisplayName("Notice를 저장하고 Entry에 noticeId를 할당 후 Entry를 저장한다")
        void persist_ValidProductNotice_ReturnsNoticeIdAndPersistsEntries() {
            // given
            ProductNotice productNotice = ProductNoticeFixtures.newProductNotice();
            Long expectedNoticeId = 1L;

            given(noticeCommandManager.persist(productNotice)).willReturn(expectedNoticeId);

            // when
            Long result = sut.persist(productNotice);

            // then
            assertThat(result).isEqualTo(expectedNoticeId);
            then(noticeCommandManager).should().persist(productNotice);
            then(entryCommandManager).should().persistAll(productNotice.entries());
        }

        @Test
        @DisplayName("Entry에 noticeId가 할당된다")
        void persist_ValidProductNotice_AssignsNoticeIdToEntries() {
            // given
            ProductNotice productNotice = ProductNoticeFixtures.newProductNotice();
            Long noticeId = 5L;

            given(noticeCommandManager.persist(productNotice)).willReturn(noticeId);

            // when
            sut.persist(productNotice);

            // then
            productNotice
                    .entries()
                    .forEach(entry -> assertThat(entry.productNoticeIdValue()).isEqualTo(noticeId));
        }
    }
}
