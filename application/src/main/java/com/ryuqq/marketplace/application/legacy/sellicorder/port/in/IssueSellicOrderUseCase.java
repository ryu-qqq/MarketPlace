package com.ryuqq.marketplace.application.legacy.sellicorder.port.in;

/**
 * 셀릭 주문 발행 UseCase.
 *
 * <p>셀릭 API에서 주문을 폴링하여 luxurydb에 레거시 형식으로 저장하고, LegacyOrderConversionOutbox를 생성합니다. 스케줄러에서 주기적으로
 * 호출됩니다.
 */
public interface IssueSellicOrderUseCase {

    /**
     * 셀릭 주문을 폴링하여 레거시 스키마에 저장합니다.
     *
     * @param salesChannelId 판매채널 ID (SELLIC = 16)
     * @param batchSize 한 번에 처리할 최대 건수
     */
    void execute(long salesChannelId, int batchSize);
}
