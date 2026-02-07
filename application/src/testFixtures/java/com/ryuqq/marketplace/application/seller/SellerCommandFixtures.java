package com.ryuqq.marketplace.application.seller;

import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand.AddressCommand;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand.CsContactCommand;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand.SellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand.SellerInfoCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;

/**
 * Seller Command 테스트 Fixtures.
 *
 * <p>Seller 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class SellerCommandFixtures {

    private SellerCommandFixtures() {}

    // ===== RegisterSellerCommand =====

    public static RegisterSellerCommand registerCommand() {
        return new RegisterSellerCommand(sellerInfoCommand(), sellerBusinessInfoCommand());
    }

    public static RegisterSellerCommand registerCommand(String sellerName) {
        return new RegisterSellerCommand(
                sellerInfoCommand(sellerName), sellerBusinessInfoCommand());
    }

    public static SellerInfoCommand sellerInfoCommand() {
        return sellerInfoCommand("테스트 셀러");
    }

    public static SellerInfoCommand sellerInfoCommand(String sellerName) {
        return new SellerInfoCommand(
                sellerName, "테스트 스토어", "https://example.com/logo.png", "테스트 셀러 설명입니다.");
    }

    public static SellerInfoCommand sellerInfoCommandWithoutOptionals() {
        return new SellerInfoCommand("테스트 셀러", "테스트 스토어", null, null);
    }

    public static SellerBusinessInfoCommand sellerBusinessInfoCommand() {
        return new SellerBusinessInfoCommand(
                "123-45-67890",
                "테스트 주식회사",
                "홍길동",
                "2024-서울강남-0001",
                addressCommand(),
                csContactCommand());
    }

    public static SellerBusinessInfoCommand sellerBusinessInfoCommandWithoutOptionals() {
        return new SellerBusinessInfoCommand(
                "123-45-67890", "테스트 주식회사", "홍길동", null, addressCommand(), csContactCommand());
    }

    public static AddressCommand addressCommand() {
        return new AddressCommand("06141", "서울시 강남구 테헤란로 123", "테스트빌딩 5층");
    }

    public static CsContactCommand csContactCommand() {
        return new CsContactCommand("02-1234-5678", "cs@test.com", "010-1234-5678");
    }

    // ===== UpdateSellerCommand =====

    public static UpdateSellerCommand updateSellerCommand(Long sellerId) {
        return new UpdateSellerCommand(
                sellerId, "수정된 셀러명", "수정된 스토어", "https://example.com/new-logo.png", "수정된 설명입니다.");
    }

    public static UpdateSellerCommand updateSellerCommandWithoutOptionals(Long sellerId) {
        return new UpdateSellerCommand(sellerId, "수정된 셀러명", "수정된 스토어", null, null);
    }

    // ===== UpdateSellerBusinessInfoCommand =====

    public static UpdateSellerBusinessInfoCommand updateBusinessInfoCommand(Long sellerId) {
        return new UpdateSellerBusinessInfoCommand(
                sellerId,
                "987-65-43210",
                "수정된 주식회사",
                "김철수",
                "2024-서울강남-0002",
                updateBusinessInfoAddressCommand(),
                updateBusinessInfoCsContactCommand());
    }

    public static UpdateSellerBusinessInfoCommand updateBusinessInfoCommandWithoutOptionals(
            Long sellerId) {
        return new UpdateSellerBusinessInfoCommand(
                sellerId,
                "987-65-43210",
                "수정된 주식회사",
                "김철수",
                null,
                updateBusinessInfoAddressCommand(),
                updateBusinessInfoCsContactCommand());
    }

    public static UpdateSellerBusinessInfoCommand.AddressCommand
            updateBusinessInfoAddressCommand() {
        return new UpdateSellerBusinessInfoCommand.AddressCommand(
                "06142", "서울시 강남구 역삼로 456", "수정빌딩 10층");
    }

    public static UpdateSellerBusinessInfoCommand.CsContactCommand
            updateBusinessInfoCsContactCommand() {
        return new UpdateSellerBusinessInfoCommand.CsContactCommand(
                "02-5678-1234", "newcs@test.com", "010-5678-1234");
    }

    // ===== UpdateSellerFullCommand =====

    public static UpdateSellerFullCommand updateFullCommand(Long sellerId) {
        return new UpdateSellerFullCommand(
                sellerId,
                updateFullSellerInfoCommand(),
                updateFullBusinessInfoCommand(),
                updateFullCsInfoCommand(),
                updateFullContractInfoCommand(),
                updateFullSettlementInfoCommand());
    }

    public static UpdateSellerFullCommand.SellerInfoCommand updateFullSellerInfoCommand() {
        return new UpdateSellerFullCommand.SellerInfoCommand(
                "수정된 셀러명", "수정된 스토어", "https://example.com/new-logo.png", "수정된 설명입니다.");
    }

    public static UpdateSellerFullCommand.SellerInfoCommand
            updateFullSellerInfoCommandWithoutOptionals() {
        return new UpdateSellerFullCommand.SellerInfoCommand("수정된 셀러명", "수정된 스토어", null, null);
    }

    public static UpdateSellerFullCommand.SellerBusinessInfoCommand
            updateFullBusinessInfoCommand() {
        return new UpdateSellerFullCommand.SellerBusinessInfoCommand(
                "987-65-43210",
                "수정된 주식회사",
                "김철수",
                "2024-서울강남-0002",
                updateFullAddressCommand(),
                updateFullCsContactCommand());
    }

    public static UpdateSellerFullCommand.SellerBusinessInfoCommand
            updateFullBusinessInfoCommandWithoutOptionals() {
        return new UpdateSellerFullCommand.SellerBusinessInfoCommand(
                "987-65-43210",
                "수정된 주식회사",
                "김철수",
                null,
                updateFullAddressCommand(),
                updateFullCsContactCommand());
    }

    public static UpdateSellerFullCommand.AddressCommand updateFullAddressCommand() {
        return new UpdateSellerFullCommand.AddressCommand("06142", "서울시 강남구 역삼로 456", "수정빌딩 10층");
    }

    public static UpdateSellerFullCommand.CsContactCommand updateFullCsContactCommand() {
        return new UpdateSellerFullCommand.CsContactCommand(
                "02-5678-1234", "newcs@test.com", "010-5678-1234");
    }

    public static UpdateSellerFullCommand.CsInfoCommand updateFullCsInfoCommand() {
        return new UpdateSellerFullCommand.CsInfoCommand(
                updateFullCsContactCommand(),
                updateFullOperatingHoursCommand(),
                "월,화,수,목,금",
                "https://pf.kakao.com/test");
    }

    public static UpdateSellerFullCommand.OperatingHoursCommand updateFullOperatingHoursCommand() {
        return new UpdateSellerFullCommand.OperatingHoursCommand("09:00", "18:00");
    }

    public static UpdateSellerFullCommand.ContractInfoCommand updateFullContractInfoCommand() {
        return new UpdateSellerFullCommand.ContractInfoCommand(
                10.5, "2025-01-01", "2025-12-31", "특약사항 없음");
    }

    public static UpdateSellerFullCommand.SettlementInfoCommand updateFullSettlementInfoCommand() {
        return new UpdateSellerFullCommand.SettlementInfoCommand(
                updateFullBankAccountCommand(), "MONTHLY", 15);
    }

    public static UpdateSellerFullCommand.BankAccountCommand updateFullBankAccountCommand() {
        return new UpdateSellerFullCommand.BankAccountCommand(
                "004", "국민은행", "12345678901234", "홍길동");
    }
}
