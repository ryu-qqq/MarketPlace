package com.ryuqq.marketplace.domain.seller.vo;

/**
 * 은행 계좌 정보 Value Object.
 *
 * @param bankCode 은행 코드
 * @param bankName 은행명
 * @param accountNumber 계좌번호
 * @param accountHolderName 예금주
 */
public record BankAccount(
        String bankCode, String bankName, String accountNumber, String accountHolderName) {

    public BankAccount {
        if (bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("은행명은 필수입니다");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("계좌번호는 필수입니다");
        }
        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException("예금주는 필수입니다");
        }
        // 계좌번호 숫자만 허용
        if (!accountNumber.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("계좌번호는 숫자만 입력 가능합니다");
        }
    }

    public static BankAccount of(
            String bankCode, String bankName, String accountNumber, String accountHolderName) {
        return new BankAccount(bankCode, bankName, accountNumber, accountHolderName);
    }

    public static BankAccount of(String bankName, String accountNumber, String accountHolderName) {
        return new BankAccount(null, bankName, accountNumber, accountHolderName);
    }

    /** 계좌번호 마스킹 (뒤 4자리만 표시). */
    public String maskedAccountNumber() {
        if (accountNumber.length() <= 4) {
            return "****";
        }
        return "*".repeat(accountNumber.length() - 4)
                + accountNumber.substring(accountNumber.length() - 4);
    }
}
