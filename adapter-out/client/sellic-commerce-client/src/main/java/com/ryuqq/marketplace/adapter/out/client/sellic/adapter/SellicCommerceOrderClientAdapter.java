package com.ryuqq.marketplace.adapter.out.client.sellic.adapter;

import com.ryuqq.marketplace.adapter.out.client.sellic.client.SellicCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.mapper.SellicCommerceOrderMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀릭 커머스 주문 폴링 클라이언트 어댑터.
 *
 * <p>{@link SalesChannelOrderClient} 구현체. 셀릭 API /openapi/get_order를 호출하여 주문을 폴링합니다.
 *
 * <p>인증: ShopCredentials.vendorId() → customer_id, ShopCredentials.apiKey() → api_key
 */
@Component
@ConditionalOnProperty(prefix = "sellic-commerce", name = "base-url")
public class SellicCommerceOrderClientAdapter implements SalesChannelOrderClient {

    private static final Logger log =
            LoggerFactory.getLogger(SellicCommerceOrderClientAdapter.class);

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SellicCommerceApiClient apiClient;
    private final SellicCommerceOrderMapper mapper;

    public SellicCommerceOrderClientAdapter(
            SellicCommerceApiClient apiClient, SellicCommerceOrderMapper mapper) {
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public String channelCode() {
        return "SELLIC";
    }

    @Override
    public List<ExternalOrderPayload> fetchNewOrders(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime) {

        String startDate = fromTime.atZone(KST).format(DATE_FORMAT);
        String endDate = toTime.atZone(KST).format(DATE_FORMAT);

        log.info(
                "셀릭 커머스 주문 폴링: salesChannelId={}, shopId={}, period={}~{}",
                salesChannelId,
                shopId,
                startDate,
                endDate);

        SellicOrderQueryRequest request =
                new SellicOrderQueryRequest(
                        credentials.vendorId(),
                        credentials.apiKey(),
                        startDate,
                        endDate,
                        null,
                        null);

        SellicOrderQueryResponse response = apiClient.queryOrders(request);

        if (!response.isSuccess()) {
            log.warn("셀릭 커머스 주문 조회 실패: message={}", response.message());
            return List.of();
        }

        if (response.datas() == null || response.datas().isEmpty()) {
            log.info("셀릭 커머스 주문 조회 결과 0건");
            return List.of();
        }

        List<ExternalOrderPayload> payloads = mapper.toExternalOrderPayloads(response.datas());

        log.info("셀릭 커머스 주문 폴링 완료: 원본={}건, 변환={}건", response.datas().size(), payloads.size());

        return payloads;
    }
}
