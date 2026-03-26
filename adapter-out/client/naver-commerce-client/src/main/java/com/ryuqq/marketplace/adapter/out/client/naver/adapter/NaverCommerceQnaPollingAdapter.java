package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverCustomerInquiryPageResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQnaPageResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceQnaMapper;
import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.port.out.client.SalesChannelQnaClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 QnA 폴링 어댑터.
 *
 * <p>고객 문의 + 상품 문의를 모두 폴링하여 ExternalQnaPayload로 변환합니다. 주문 폴링과 동일하게 Shop 단위로 호출됩니다.
 */
@Component
@ConditionalOnBean(NaverCommerceQnaClientAdapter.class)
public class NaverCommerceQnaPollingAdapter implements SalesChannelQnaClient {

    private static final Logger log = LoggerFactory.getLogger(NaverCommerceQnaPollingAdapter.class);
    private static final String NAVER_CHANNEL_CODE = "NAVER";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NaverCommerceQnaClientAdapter qnaClient;
    private final NaverCommerceQnaMapper mapper;

    public NaverCommerceQnaPollingAdapter(
            NaverCommerceQnaClientAdapter qnaClient, NaverCommerceQnaMapper mapper) {
        this.qnaClient = qnaClient;
        this.mapper = mapper;
    }

    @Override
    public String channelCode() {
        return NAVER_CHANNEL_CODE;
    }

    @Override
    public List<ExternalQnaPayload> fetchNewQnas(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant from,
            Instant to,
            int batchSize) {
        List<ExternalQnaPayload> results = new ArrayList<>();

        // 1. 고객 문의 (1:1 문의) 폴링
        try {
            String startDate =
                    LocalDate.ofInstant(from, KST).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endDate = LocalDate.ofInstant(to, KST).format(DateTimeFormatter.ISO_LOCAL_DATE);

            NaverCustomerInquiryPageResponse inquiryResponse =
                    qnaClient.getCustomerInquiries(startDate, endDate, false, 1, batchSize);

            if (inquiryResponse != null && inquiryResponse.content() != null) {
                inquiryResponse.content().stream()
                        .filter(i -> !i.answered())
                        .map(mapper::toExternalPayload)
                        .forEach(results::add);
            }
        } catch (Exception e) {
            log.warn(
                    "네이버 고객 문의 폴링 실패: salesChannelId={}, shopId={}",
                    salesChannelId,
                    shopId,
                    e);
        }

        // 2. 상품 문의 폴링
        try {
            String fromDate = from.toString();
            String toDate = to.toString();

            NaverProductQnaPageResponse qnaResponse =
                    qnaClient.getProductQnas(fromDate, toDate, false, 1, batchSize);

            if (qnaResponse != null && qnaResponse.contents() != null) {
                qnaResponse.contents().stream()
                        .filter(q -> !q.answered())
                        .map(mapper::toExternalPayload)
                        .forEach(results::add);
            }
        } catch (Exception e) {
            log.warn(
                    "네이버 상품 문의 폴링 실패: salesChannelId={}, shopId={}",
                    salesChannelId,
                    shopId,
                    e);
        }

        log.info(
                "네이버 QnA 폴링 완료: salesChannelId={}, shopId={}, 수집 {}건",
                salesChannelId,
                shopId,
                results.size());
        return results;
    }
}
