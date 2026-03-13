package com.ryuqq.marketplace.application.outboundproductimage.manager;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.client.SalesChannelImageClient;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 채널 코드 기반 이미지 클라이언트 라우팅 매니저. */
@Component
public class SalesChannelImageClientManager {

    private final Map<String, SalesChannelImageClient> clientMap;

    @SuppressFBWarnings(
            value = "CT_CONSTRUCTOR_THROW",
            justification = "Spring @Component – 중복 channelCode는 빈 생성 시 즉시 실패해야 합니다")
    public SalesChannelImageClientManager(List<SalesChannelImageClient> clients) {
        this.clientMap =
                clients.stream()
                        .collect(
                                Collectors.toMap(
                                        SalesChannelImageClient::channelCode,
                                        Function.identity(),
                                        (a, b) -> {
                                            throw new IllegalStateException(
                                                    "중복된 channelCode가 존재합니다: " + a.channelCode());
                                        }));
    }

    /**
     * 이미지 URL 목록을 해당 채널에 업로드합니다.
     *
     * @param channelCode 판매채널 코드
     * @param imageUrls 원본 이미지 URL 목록
     * @return 외부 채널 CDN URL 목록
     */
    public List<String> uploadImages(String channelCode, List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return List.of();
        }
        return resolve(channelCode).uploadImages(imageUrls);
    }

    public boolean supports(String channelCode) {
        return clientMap.containsKey(channelCode);
    }

    private SalesChannelImageClient resolve(String channelCode) {
        SalesChannelImageClient client = clientMap.get(channelCode);
        if (client == null) {
            throw new IllegalArgumentException(
                    "이미지 업로드를 지원하지 않는 판매채널입니다: channelCode=" + channelCode);
        }
        return client;
    }
}
