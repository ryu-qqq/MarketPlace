package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverCustomerInquiry;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQna;
import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceQnaMapper 단위 테스트")
class NaverCommerceQnaMapperTest {

    private final NaverCommerceQnaMapper sut = new NaverCommerceQnaMapper();

    @Nested
    @DisplayName("toExternalPayload(NaverCustomerInquiry)")
    class CustomerInquiryMapping {

        @Test
        @DisplayName("고객 문의를 ExternalQnaPayload로 변환한다")
        void convertsCustomerInquiry() {
            var inquiry =
                    new NaverCustomerInquiry(
                            100L, "배송", "배송 문의", "언제 도착하나요?",
                            "2026-03-20T10:00:00", null, null, null,
                            false, "ORD001", "PROD001", "상품명",
                            "customer1", "홍길동");

            ExternalQnaPayload result = sut.toExternalPayload(inquiry);

            assertThat(result.externalQnaId()).isEqualTo("INQUIRY-100");
            assertThat(result.qnaType()).isEqualTo("SHIPPING");
            assertThat(result.questionContent()).isEqualTo("언제 도착하나요?");
            assertThat(result.questionAuthor()).isEqualTo("홍길동");
            assertThat(result.rawPayload()).contains("CUSTOMER_INQUIRY");
            assertThat(result.rawPayload()).contains("\"inquiryNo\":100");
        }

        @Test
        @DisplayName("매핑되지 않는 카테고리는 ETC로 변환한다")
        void unknownCategoryMappedToEtc() {
            var inquiry =
                    new NaverCustomerInquiry(
                            200L, "기타문의", "제목", "내용",
                            null, null, null, null,
                            false, null, null, null,
                            null, "작성자");

            ExternalQnaPayload result = sut.toExternalPayload(inquiry);

            assertThat(result.qnaType()).isEqualTo("ETC");
        }

        @Test
        @DisplayName("반품 카테고리는 REFUND로 매핑한다")
        void refundCategory() {
            var inquiry =
                    new NaverCustomerInquiry(
                            300L, "반품", "반품 문의", "반품하고 싶어요",
                            null, null, null, null,
                            false, null, null, null,
                            null, "작성자");

            ExternalQnaPayload result = sut.toExternalPayload(inquiry);

            assertThat(result.qnaType()).isEqualTo("REFUND");
        }

        @Test
        @DisplayName("교환 카테고리는 EXCHANGE로 매핑한다")
        void exchangeCategory() {
            var inquiry =
                    new NaverCustomerInquiry(
                            400L, "교환", "교환 문의", "교환 원해요",
                            null, null, null, null,
                            false, null, null, null,
                            null, "작성자");

            ExternalQnaPayload result = sut.toExternalPayload(inquiry);

            assertThat(result.qnaType()).isEqualTo("EXCHANGE");
        }
    }

    @Nested
    @DisplayName("toExternalPayload(NaverProductQna)")
    class ProductQnaMapping {

        @Test
        @DisplayName("상품 문의를 ExternalQnaPayload로 변환한다")
        void convertsProductQna() {
            var qna =
                    new NaverProductQna(
                            500L, 12345L, "테스트 상품", "사이즈 문의입니다",
                            null, false, "user***", "2026-03-20");

            ExternalQnaPayload result = sut.toExternalPayload(qna);

            assertThat(result.externalQnaId()).isEqualTo("PRODUCT-QNA-500");
            assertThat(result.qnaType()).isEqualTo("PRODUCT");
            assertThat(result.questionContent()).isEqualTo("사이즈 문의입니다");
            assertThat(result.questionAuthor()).isEqualTo("user***");
            assertThat(result.rawPayload()).contains("PRODUCT_QNA");
            assertThat(result.rawPayload()).contains("\"questionId\":500");
            assertThat(result.rawPayload()).contains("\"productId\":12345");
        }
    }
}
