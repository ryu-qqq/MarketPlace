package com.ryuqq.marketplace.application.common.fallback;

import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofRefundPolicySyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofSellerAddressSyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofSellerSyncClient;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofShippingPolicySyncClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Setof Commerce 동기화 클라이언트 폴백 설정.
 *
 * <p>setof-commerce-client 모듈이 클래스패스에 없거나 service-token이 설정되지 않은 경우 NoOp 구현체를 제공합니다.
 */
@Configuration
public class SetofSyncClientFallbackConfig {

    @Bean
    @ConditionalOnMissingBean
    SetofSellerSyncClient noOpSetofSellerSyncClient() {
        return new SetofSellerSyncClient() {
            @Override
            public SetofSyncResult createSeller(Long sellerId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public SetofSyncResult updateSeller(Long sellerId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    SetofShippingPolicySyncClient noOpSetofShippingPolicySyncClient() {
        return new SetofShippingPolicySyncClient() {
            @Override
            public SetofSyncResult createShippingPolicy(Long sellerId, Long policyId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public SetofSyncResult updateShippingPolicy(Long sellerId, Long policyId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    SetofRefundPolicySyncClient noOpSetofRefundPolicySyncClient() {
        return new SetofRefundPolicySyncClient() {
            @Override
            public SetofSyncResult createRefundPolicy(Long sellerId, Long policyId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public SetofSyncResult updateRefundPolicy(Long sellerId, Long policyId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    SetofSellerAddressSyncClient noOpSetofSellerAddressSyncClient() {
        return new SetofSellerAddressSyncClient() {
            @Override
            public SetofSyncResult createSellerAddress(Long sellerId, Long addressId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public SetofSyncResult updateSellerAddress(Long sellerId, Long addressId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public SetofSyncResult deleteSellerAddress(Long sellerId, Long addressId) {
                return SetofSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "Setof Commerce 클라이언트가 설정되지 않았습니다");
            }
        };
    }
}
